package com.bookstore.service;

import com.bookstore.entity.Voucher;
import com.bookstore.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VoucherService {

    private final VoucherRepository voucherRepository;

    public List<Voucher> getAllVouchers() {
        return voucherRepository.findAll();
    }

    public Voucher createVoucher(Voucher voucher) {
        voucher.setCode(voucher.getCode().toUpperCase());
        return voucherRepository.save(voucher);
    }

    public Voucher updateVoucher(Long id, Voucher updated) {
        Voucher existing = voucherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Voucher not found"));
        existing.setCode(updated.getCode().toUpperCase());
        existing.setDiscountPercent(updated.getDiscountPercent());
        existing.setMaxDiscountAmount(updated.getMaxDiscountAmount());
        existing.setMinOrderValue(updated.getMinOrderValue());
        existing.setExpiryDate(updated.getExpiryDate());
        existing.setActive(updated.isActive());
        existing.setUsageLimit(updated.getUsageLimit());
        return voucherRepository.save(existing);
    }

    public void deleteVoucher(Long id) {
        voucherRepository.deleteById(id);
    }

    public Voucher validateVoucher(String code, Double orderTotal) {
        Voucher voucher = voucherRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Mã giảm giá không tồn tại"));

        if (!voucher.isActive()) {
            throw new RuntimeException("Mã giảm giá đã bị khóa");
        }

        if (voucher.getExpiryDate() != null && voucher.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Mã giảm giá đã hết hạn");
        }

        if (voucher.getUsageLimit() > 0 && voucher.getUsedCount() >= voucher.getUsageLimit()) {
            throw new RuntimeException("Mã giảm giá đã hết lượt sử dụng");
        }

        if (voucher.getMinOrderValue() != null && orderTotal < voucher.getMinOrderValue()) {
            throw new RuntimeException("Đơn hàng chưa đạt giá trị tối thiểu để áp dụng mã này");
        }

        return voucher;
    }

    public void incrementUsedCount(String code) {
        voucherRepository.findByCode(code).ifPresent(voucher -> {
            voucher.setUsedCount(voucher.getUsedCount() + 1);
            voucherRepository.save(voucher);
        });
    }
}
