package com.bookstore.dto;

import com.bookstore.entity.Book;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
    private Long id;
    private String googleId;
    private String title;
    private String author;
    private String category;
    private String description;
    private String publisher;
    private LocalDate publishedDate;
    private String imageUrl;
    private String thumbnailUrl;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer discountPercent;
    private BigDecimal rating;
    private Integer ratingCount;
    private Integer pages;
    private String language;
    private String sku;
    private Integer quantity;
    private Boolean isNew;
    private Boolean isFeatured;
    private Boolean isAvailable;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // From Book entity to BookDTO
    public static BookDTO fromEntity(Book book) {
        BookDTO dto = new BookDTO();
        dto.setId(book.getId());
        dto.setGoogleId(book.getGoogleId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setCategory(book.getCategory());
        dto.setDescription(book.getDescription());
        dto.setPublisher(book.getPublisher());
        dto.setPublishedDate(book.getPublishedDate());
        dto.setImageUrl(book.getImageUrl());
        dto.setThumbnailUrl(book.getThumbnailUrl());
        dto.setPrice(book.getPrice());
        dto.setOriginalPrice(book.getOriginalPrice());
        dto.setDiscountPercent(book.getDiscountPercent());
        dto.setRating(book.getRating());
        dto.setRatingCount(book.getRatingCount());
        dto.setPages(book.getPages());
        dto.setLanguage(book.getLanguage());
        dto.setSku(book.getSku());
        dto.setQuantity(book.getQuantity());
        dto.setIsNew(book.getIsNew());
        dto.setIsFeatured(book.getIsFeatured());
        dto.setIsAvailable(book.getIsAvailable());
        dto.setCreatedAt(book.getCreatedAt());
        dto.setUpdatedAt(book.getUpdatedAt());
        return dto;
    }
}
