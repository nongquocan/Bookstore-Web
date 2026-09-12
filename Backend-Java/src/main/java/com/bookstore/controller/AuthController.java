package com.bookstore.controller;

import com.bookstore.dto.ApiResponse;
import com.bookstore.dto.AuthResponse;
import com.bookstore.dto.LoginRequest;
import com.bookstore.dto.RegisterRequest;
import com.bookstore.dto.UserDTO;
import com.bookstore.entity.User;
import com.bookstore.service.AuthService;
import com.bookstore.service.EmailVerificationService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {
        "http://localhost:3000", "http://localhost:5500",
        "http://localhost:8000", "http://127.0.0.1:5500",
        "http://127.0.0.1:3000", "http://127.0.0.1:8000"
}, maxAge = 3600)
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private EmailVerificationService emailVerificationService;

    // ─── Login ───────────────────────────────────────────────────────────────
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            AuthResponse response = authService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Login error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Login failed", e.getMessage()));
        }
    }

    // ─── Send OTP ─────────────────────────────────────────────────────────────
    /**
     * Gửi OTP về email để xác thực trước khi đăng ký.
     * Frontend gọi endpoint này sau khi user điền form.
     * Body: { "email": "user@example.com" }
     */
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Email không được để trống"));
        }
        email = email.trim().toLowerCase();

        // Kiểm tra email đã tồn tại chưa
        if (authService.emailExists(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error("Email này đã được đăng ký. Vui lòng dùng email khác hoặc đăng nhập."));
        }

        try {
            emailVerificationService.generateAndSendOtp(email);
            return ResponseEntity.ok(ApiResponse.success(
                    "Mã OTP đã được gửi đến " + email + ". Kiểm tra hộp thư (kể cả thư rác).",
                    Map.of("email", email)
            ));
        } catch (Exception e) {
            log.error("Send OTP error for {}: {}", email, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Không thể gửi OTP. Vui lòng thử lại sau.", e.getMessage()));
        }
    }

    // ─── Verify OTP ──────────────────────────────────────────────────────────
    /**
     * Xác nhận OTP.
     * Body: { "email": "...", "otp": "123456" }
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String otp   = body.get("otp");

        if (email == null || otp == null || email.isBlank() || otp.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Email và mã OTP không được để trống"));
        }

        boolean verified = emailVerificationService.verifyOtp(email.trim().toLowerCase(), otp.trim());
        if (verified) {
            return ResponseEntity.ok(ApiResponse.success("Xác nhận email thành công!", null));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Mã OTP không đúng hoặc đã hết hạn. Vui lòng yêu cầu mã mới."));
        }
    }

    // ─── Register ────────────────────────────────────────────────────────────
    /**
     * Đăng ký tài khoản — chỉ cho phép nếu email đã được verify OTP.
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            // Kiểm tra email đã xác thực OTP chưa
            if (!emailVerificationService.isEmailVerified(registerRequest.getEmail().trim().toLowerCase())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error("Email chưa được xác thực. Vui lòng xác thực OTP trước."));
            }

            AuthResponse response = authService.register(registerRequest);
            // Xoá trạng thái verified sau khi đăng ký thành công
            emailVerificationService.markEmailUsed(registerRequest.getEmail().trim().toLowerCase());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            log.error("Registration error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Đăng ký thất bại", e.getMessage()));
        }
    }

    // ─── Get Current User ─────────────────────────────────────────────────────
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()
                    && authentication.getPrincipal() instanceof User) {
                User user = (User) authentication.getPrincipal();
                UserDTO userDTO = authService.getCurrentUser(user.getEmail());
                return ResponseEntity.ok(ApiResponse.success("User information retrieved", userDTO));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Not authenticated", null));
        } catch (Exception e) {
            log.error("Get current user error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error retrieving user information", e.getMessage()));
        }
    }

    // ─── Change Password ──────────────────────────────────────────────────────
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()
                    && authentication.getPrincipal() instanceof User) {
                User user = (User) authentication.getPrincipal();
                boolean success = authService.changePassword(user.getEmail(), oldPassword, newPassword);
                if (success) {
                    return ResponseEntity.ok(ApiResponse.success("Password changed successfully", null));
                }
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Not authenticated", null));
        } catch (Exception e) {
            log.error("Change password error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to change password", e.getMessage()));
        }
    }

    // ─── Update Profile Address ───────────────────────────────────────────────
    @PutMapping("/profile/address")
    public ResponseEntity<?> updateAddress(@RequestBody com.bookstore.dto.AddressDTO addressDTO) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()
                    && authentication.getPrincipal() instanceof User) {
                User user = (User) authentication.getPrincipal();
                boolean success = authService.updateAddress(user.getEmail(), addressDTO);
                if (success) {
                    return ResponseEntity.ok(ApiResponse.success("Address updated successfully", null));
                }
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Not authenticated", null));
        } catch (Exception e) {
            log.error("Update address error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update address", e.getMessage()));
        }
    }
}
