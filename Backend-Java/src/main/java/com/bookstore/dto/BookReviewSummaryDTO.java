package com.bookstore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookReviewSummaryDTO {
    private String bookId;
    private String bookTitle;
    private Long reviewCount;
    private Double averageRating;
}
