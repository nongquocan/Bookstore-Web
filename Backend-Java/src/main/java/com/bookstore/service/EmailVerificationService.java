package com.bookstore.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class EmailVerificationService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${app.otp.expiry-minutes:5}")
    private int otpExpiryMinutes;

    @Value("${app.otp.from-email:noreply@bookstore.com}")
    private String fromEmail;

    @Value("${app.store-name:BookStore}")
    private String storeName;

    // email → OtpRecord
    private final ConcurrentHashMap<String, OtpRecord> otpStore = new ConcurrentHashMap<>();
    // email → đã xác thực (dùng cho bước register)
    private final ConcurrentHashMap<String, LocalDateTime> verifiedEmails = new ConcurrentHashMap<>();

    private static final SecureRandom random = new SecureRandom();

    // ─── Inner class ────────────────────────────────────────────────────────
    private static class OtpRecord {
        final String code;
        final LocalDateTime expiry;

        OtpRecord(String code, LocalDateTime expiry) {
            this.code = code;
            this.expiry = expiry;
        }

        boolean isExpired() {
            return LocalDateTime.now().isAfter(expiry);
        }
    }

    // ─── Generate & Send OTP ─────────────────────────────────────────────────
    public void generateAndSendOtp(String email) throws Exception {
        // Xoá OTP cũ nếu có
        otpStore.remove(email);
        verifiedEmails.remove(email);

        String otp = String.format("%06d", random.nextInt(1_000_000));
        OtpRecord record = new OtpRecord(otp, LocalDateTime.now().plusMinutes(otpExpiryMinutes));
        otpStore.put(email, record);

        log.info("OTP generated for {}: {} (expires {})", email, otp, record.expiry);

        if (mailSender != null) {
            try {
                sendOtpEmail(email, otp);
            } catch (Exception e) {
                log.error("=====================================================");
                log.error("KHÔNG THỂ GỬI EMAIL: {}", e.getMessage());
                log.error("Vui lòng cấu hình 'spring.mail.username' và 'password' đúng trong application.properties!");
                log.error(">> MÃ OTP CỦA {} LÀ: {} <<", email, otp);
                log.error("=====================================================");
                // Thay vì ném lỗi 500 làm đứng hệ thống, in OTP ra console để dev test.
            }
        } else {
            // Fallback khi chưa cấu hình starter mail
            log.warn(">> JavaMailSender chưa có. MÃ OTP CỦA {} LÀ: {} <<", email, otp);
        }
    }

    private void sendOtpEmail(String email, String otp) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        String safeFrom = (fromEmail != null && !fromEmail.isBlank()) ? fromEmail : "noreply@bookstore.com";
        String safeName = (storeName != null && !storeName.isBlank()) ? storeName : "BookStore";
        helper.setFrom(safeFrom, safeName);
        helper.setTo(email);
        helper.setSubject("[" + safeName + "] Mã xác nhận đăng ký tài khoản");

        String html = buildOtpEmailHtml(otp);
        helper.setText(html, true);

        mailSender.send(message);
        log.info("OTP email sent to {}", email);
    }

    // ─── Verify OTP ──────────────────────────────────────────────────────────
    /**
     * @return true nếu OTP đúng và còn hạn
     */
    public boolean verifyOtp(String email, String code) {
        OtpRecord record = otpStore.get(email);
        if (record == null) {
            log.warn("No OTP found for email: {}", email);
            return false;
        }
        if (record.isExpired()) {
            otpStore.remove(email);
            log.warn("OTP expired for email: {}", email);
            return false;
        }
        if (!record.code.equals(code.trim())) {
            log.warn("Wrong OTP for email: {} (expected {}, got {})", email, record.code, code);
            return false;
        }
        // Xoá OTP sau khi verify thành công
        otpStore.remove(email);
        // Đánh dấu email đã verified (valid 10 phút để hoàn tất đăng ký)
        verifiedEmails.put(email, LocalDateTime.now().plusMinutes(10));
        log.info("OTP verified successfully for {}", email);
        return true;
    }

    // ─── Check verified status ───────────────────────────────────────────────
    public boolean isEmailVerified(String email) {
        LocalDateTime expiry = verifiedEmails.get(email);
        if (expiry == null)
            return false;
        if (LocalDateTime.now().isAfter(expiry)) {
            verifiedEmails.remove(email);
            return false;
        }
        return true;
    }

    public void markEmailUsed(String email) {
        verifiedEmails.remove(email);
    }

    // ─── HTML Email Template ──────────────────────────────────────────────────
    private String buildOtpEmailHtml(String otp) {
        return """
                <!DOCTYPE html>
                <html lang="vi">
                <head><meta charset="UTF-8"></head>
                <body style="font-family: Arial, sans-serif; background: #f5f5f5; margin: 0; padding: 20px;">
                  <div style="max-width: 500px; margin: 0 auto; background: #fff; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,.1);">
                    <div style="background: #d32f2f; padding: 24px; text-align: center;">
                      <h1 style="color: #fff; margin: 0; font-size: 22px;">📚 %s</h1>
                    </div>
                    <div style="padding: 32px 24px; text-align: center;">
                      <h2 style="color: #212121; margin-bottom: 8px;">Xác nhận email của bạn</h2>
                      <p style="color: #666; margin-bottom: 24px;">Mã xác nhận của bạn là:</p>
                      <div style="background: #f5f5f5; border: 2px dashed #d32f2f; border-radius: 8px; padding: 20px; margin-bottom: 24px; display: inline-block;">
                        <span style="font-size: 36px; font-weight: 700; color: #d32f2f; letter-spacing: 8px;">%s</span>
                      </div>
                      <p style="color: #888; font-size: 13px;">Mã có hiệu lực trong <strong>%d phút</strong>. Không chia sẻ mã này cho bất kỳ ai.</p>
                    </div>
                    <div style="background: #f9f9f9; padding: 16px 24px; text-align: center; border-top: 1px solid #eee;">
                      <p style="color: #aaa; font-size: 12px; margin: 0;">Nếu bạn không thực hiện yêu cầu này, hãy bỏ qua email này.</p>
                    </div>
                  </div>
                </body>
                </html>
                """
                .formatted(storeName, otp, otpExpiryMinutes);
    }
}
