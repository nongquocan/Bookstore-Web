package com.bookstore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

    private Long id;
    private String orderCode;
    private String status;
    private String paymentMethod;
    private String paymentStatus;
    private String shippingMethod;
    private BigDecimal shippingFee;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private String voucherCode;
    private String recipientName;
    private String recipientPhone;
    private String shippingAddress;
    private String deliveryNote;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderItemDTO> items;
}
