package com.bookstore.dto;

import lombok.Data;

@Data
public class Createorderrequest {

    // Phương thức thanh toán: "cod", "vnpay", "momo", "card"
    private String paymentMethod;

    // Phương thức vận chuyển: "standard", "express", "sameday"
    private String shippingMethod;

    // Mã giảm giá
    private String voucherCode;

    // Thông tin người nhận
    private String recipientName;
    private String recipientPhone;
    private String recipientEmail;

    // Địa chỉ giao hàng (từ hidden fields trong checkout.html)
    private String shippingAddress;   // Địa chỉ đầy đủ
    private String street;
    private String district;
    private String city;
    private Double latitude;
    private Double longitude;

    // Thêm chi tiết địa chỉ
    private String unitNumber;        // Số nhà / tầng / căn hộ
    private String deliveryNote;      // Ghi chú cho người giao hàng

    // Chỉ dùng khi paymentMethod = "card" (Stripe)
    private String stripePaymentMethodId;
}