package com.bookstore.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FlashSaleRequest {
    /** Phần trăm giảm giá (1-99) */
    private Integer discountPercent;
    /** Thời gian bắt đầu flash sale */
    private LocalDateTime startTime;
    /** Thời gian kết thúc flash sale */
    private LocalDateTime endTime;
}
