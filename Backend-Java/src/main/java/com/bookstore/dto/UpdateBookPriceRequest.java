package com.bookstore.dto;

import lombok.Data;

@Data
public class UpdateBookPriceRequest {
    private Float listPrice;
    private Integer stockQuantity;
}
