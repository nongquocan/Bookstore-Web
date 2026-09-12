package com.bookstore.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_code", unique = true, nullable = false)
    private String orderCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String status;

    // ─── Thanh toán ───────────────────────────────────────────────────────────
    // paymentMethod: cod | vnpay | momo | card
    @Column(name = "payment_method")
    private String paymentMethod;

    // paymentStatus: UNPAID | PAID | PAYMENT_FAILED | REFUNDED
    @Column(name = "payment_status")
    private String paymentStatus = "UNPAID";

    // ─── Vận chuyển ───────────────────────────────────────────────────────────
    // shippingMethod: standard | express | sameday
    @Column(name = "shipping_method")
    private String shippingMethod;

    @Column(name = "shipping_fee", precision = 12, scale = 2)
    private BigDecimal shippingFee = BigDecimal.ZERO;

    // ─── Tiền ─────────────────────────────────────────────────────────────────
    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "discount_amount", precision = 12, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "voucher_code")
    private String voucherCode;

    // ─── Thông tin người nhận ─────────────────────────────────────────────────
    @Column(name = "recipient_name")
    private String recipientName;

    @Column(name = "recipient_phone")
    private String recipientPhone;

    @Column(name = "recipient_email")
    private String recipientEmail;

    // ─── Địa chỉ giao hàng ───────────────────────────────────────────────────
    @Column(name = "shipping_address", columnDefinition = "TEXT")
    private String shippingAddress; // Địa chỉ đầy đủ

    @Column(name = "street")
    private String street;

    @Column(name = "district")
    private String district;

    @Column(name = "city")
    private String city;

    @Column(name = "unit_number")
    private String unitNumber;

    @Column(name = "delivery_note", columnDefinition = "TEXT")
    private String deliveryNote;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    // ─── Timestamps ───────────────────────────────────────────────────────────
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ─── Order items ──────────────────────────────────────────────────────────

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new java.util.ArrayList<>();

    // ─── Lifecycle hooks ──────────────────────────────────────────────────────
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        // Tự sinh mã đơn hàng nếu chưa có
        if (orderCode == null || orderCode.isBlank()) {
            orderCode = generateOrderCode();
        }
        // Đảm bảo paymentStatus có giá trị mặc định
        if (paymentStatus == null) {
            paymentStatus = "UNPAID";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    private String generateOrderCode() {
        String date = java.time.LocalDate.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String suffix = UUID.randomUUID().toString()
                .replace("-", "")
                .substring(0, 6)
                .toUpperCase();
        return "BS-" + date + "-" + suffix;
    }
}