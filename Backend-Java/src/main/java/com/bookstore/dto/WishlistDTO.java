package com.bookstore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WishlistDTO {

    private Long id;
    private String bookId;
    private String bookTitle;
    private String bookAuthors;
    private String bookThumbnail;
    private Float price;
    private LocalDateTime addedAt;
}
