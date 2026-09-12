package com.bookstore.dto;

import com.bookstore.entity.Book;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
    private String id;
    private String title;
    private String subtitle;
    private String authors;
    private String publisher;
    private String publishedDate;
    private String description;
    private String categories;
    private String searchCategory;
    private Integer pageCount;
    private String language;
    private String isbn13;
    private String isbn10;
    private Float averageRating;
    private Integer ratingsCount;
    private Float listPrice;
    private String currency;
    private String buyable;
    private String previewLink;
    private String infoLink;
    private String thumbnail;
    // Danh sách category ID từ bảng book_categories (dùng cho filter frontend)
    private List<Long> categoryIds;
    
    private Integer stockQuantity;

    // Flash Sale Fields
    private Float flashSalePrice;
    private Integer flashSaleDiscountPercent;
    private LocalDateTime flashSaleStartTime;
    private LocalDateTime flashSaleEndTime;
    private Boolean isFlashSale;
    // Giá hiệu quả: flashSalePrice nếu đang sale, ngược lại listPrice
    private Float effectivePrice;

    public static BookDTO fromEntity(Book book) {
        BookDTO dto = new BookDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setSubtitle(book.getSubtitle());
        dto.setAuthors(book.getAuthors());
        dto.setPublisher(book.getPublisher());
        dto.setPublishedDate(book.getPublishedDate());
        dto.setDescription(book.getDescription());
        dto.setCategories(book.getCategories());
        dto.setSearchCategory(book.getSearchCategory());
        dto.setPageCount(book.getPageCount());
        dto.setLanguage(book.getLanguage());
        dto.setIsbn13(book.getIsbn13());
        dto.setIsbn10(book.getIsbn10());
        dto.setAverageRating(book.getAverageRating());
        dto.setRatingsCount(book.getRatingsCount());
        dto.setListPrice(book.getListPrice());
        dto.setCurrency(book.getCurrency());
        dto.setBuyable(book.getBuyable());
        dto.setPreviewLink(book.getPreviewLink());
        dto.setInfoLink(book.getInfoLink());
        dto.setThumbnail(book.getThumbnail());
        // Map danh sách category ID từ junction table
        if (book.getBookCategories() != null) {
            dto.setCategoryIds(
                book.getBookCategories().stream()
                    .map(cat -> cat.getId())
                    .collect(Collectors.toList())
            );
        }
        
        dto.setStockQuantity(book.getStockQuantity() != null ? book.getStockQuantity() : 0);

        // Flash Sale
        dto.setFlashSalePrice(book.getFlashSalePrice());
        dto.setFlashSaleDiscountPercent(book.getFlashSaleDiscountPercent());
        dto.setFlashSaleStartTime(book.getFlashSaleStartTime());
        dto.setFlashSaleEndTime(book.getFlashSaleEndTime());
        dto.setIsFlashSale(book.getIsFlashSale());
        // Tính giá hiệu quả
        boolean activeFlashSale = Boolean.TRUE.equals(book.getIsFlashSale())
                && book.getFlashSalePrice() != null
                && book.getFlashSaleEndTime() != null
                && book.getFlashSaleEndTime().isAfter(LocalDateTime.now());
        dto.setEffectivePrice(activeFlashSale ? book.getFlashSalePrice() : book.getListPrice());
        return dto;
    }
}