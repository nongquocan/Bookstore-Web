package com.bookstore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {

    private Long id;
    private String bookId;
    private String bookTitle;
    private String bookThumbnail;
    private Integer quantity;
    private Float price;
    private Float subtotal;
    private LocalDateTime addedAt;
}
