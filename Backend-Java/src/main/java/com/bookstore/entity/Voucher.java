package com.bookstore.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "vouchers")
@Data
public class Voucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private int discountPercent;

    private Double maxDiscountAmount; // Giảm tối đa
    private Double minOrderValue; // Đơn hàng tối thiểu để áp dụng

    private LocalDateTime expiryDate;

    private boolean isActive = true;

    private int usageLimit = 0; // 0 = không giới hạn
    private int usedCount = 0;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
