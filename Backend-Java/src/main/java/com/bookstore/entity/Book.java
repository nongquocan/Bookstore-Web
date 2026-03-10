package com.bookstore.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "books", indexes = {
    @Index(name = "idx_title", columnList = "title"),
    @Index(name = "idx_category", columnList = "category"),
    @Index(name = "idx_author", columnList = "author"),
    @Index(name = "idx_google_id", columnList = "google_id"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String googleId;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(length = 255)
    private String author;

    @Column(length = 100)
    private String category;

    @Column(columnDefinition = "LONGTEXT")
    private String description;

    @Column(length = 255)
    private String publisher;

    private LocalDate publishedDate;

    @Column(length = 500)
    private String imageUrl;

    @Column(length = 500)
    private String thumbnailUrl;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Column(precision = 10, scale = 2)
    private BigDecimal originalPrice;

    private Integer discountPercent;

    @Column(precision = 3, scale = 2)
    private BigDecimal rating;

    private Integer ratingCount;

    private Integer pages;

    @Column(length = 20)
    private String language;

    @Column(unique = true, length = 50)
    private String sku;

    private Integer quantity;

    private Boolean isNew;

    private Boolean isFeatured;

    private Boolean isAvailable;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isNew == null) isNew = true;
        if (isFeatured == null) isFeatured = false;
        if (isAvailable == null) isAvailable = true;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
