package com.bookstore.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @Id
    @Column(name = "book_id", length = 50, nullable = false)
    private String id; 

    @Column(name = "title", length = 500)
    private String title;

    @Column(name = "subtitle", length = 500)
    private String subtitle;

    @Column(name = "authors", length = 300)
    private String authors;

    @Column(name = "publisher", length = 300)
    private String publisher;

    @Column(name = "published_date", length = 50)
    private String publishedDate;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "categories", length = 200)
    private String categories;

    @Column(name = "search_category", length = 200)
    private String searchCategory;

    @Column(name = "page_count")
    private Integer pageCount;

    @Column(name = "language", length = 20)
    private String language;

    @Column(name = "isbn_13", length = 20)
    private String isbn13;

    @Column(name = "isbn_10", length = 20)
    private String isbn10;

    @Column(name = "average_rating")
    private Float averageRating;

    @Column(name = "ratings_count")
    private Integer ratingsCount;

    @Column(name = "list_price")
    private Float listPrice;

    @Column(name = "currency", length = 10)
    private String currency;

    @Column(name = "buyable", length = 10)
    private String buyable;

    @Column(name = "preview_link", columnDefinition = "TEXT")
    private String previewLink;

    @Column(name = "info_link", columnDefinition = "TEXT")
    private String infoLink;

    @Column(name = "thumbnail", columnDefinition = "TEXT")
    private String thumbnail;

    // ============================================
    // Inventory Field
    // ============================================
    @Column(name = "stock_quantity")
    private Integer stockQuantity = 100; // Default 100

    // ============================================
    // Flash Sale Fields
    // ============================================

    @Column(name = "flash_sale_price")
    private Float flashSalePrice;

    @Column(name = "flash_sale_discount_percent")
    private Integer flashSaleDiscountPercent;

    @Column(name = "flash_sale_start_time")
    private LocalDateTime flashSaleStartTime;

    @Column(name = "flash_sale_end_time")
    private LocalDateTime flashSaleEndTime;

    @Column(name = "is_flash_sale")
    private Boolean isFlashSale = false;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "book_categories",
        joinColumns = @JoinColumn(name = "book_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> bookCategories = new HashSet<>();
}