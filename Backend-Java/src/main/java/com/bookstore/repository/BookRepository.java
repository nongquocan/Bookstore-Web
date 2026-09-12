package com.bookstore.repository;

import com.bookstore.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, String> {

    // Tìm sách theo search_category (text) - exact match
    Page<Book> findBySearchCategory(String searchCategory, Pageable pageable);
    List<Book> findBySearchCategory(String searchCategory);

    // Tìm sách theo search_category (text) - LIKE, không phân biệt hoa thường
    @Query("SELECT b FROM Book b WHERE LOWER(b.searchCategory) LIKE LOWER(CONCAT('%', :category, '%'))")
    Page<Book> findBySearchCategoryContaining(@Param("category") String category, Pageable pageable);

    @Query("SELECT b FROM Book b WHERE LOWER(b.searchCategory) LIKE LOWER(CONCAT('%', :category, '%'))")
    List<Book> findBySearchCategoryContaining(@Param("category") String category);

    // Tìm sách theo category ID qua junction table book_categories (CHÍNH XÁC NHẤT)
    @Query("SELECT b FROM Book b JOIN b.bookCategories c WHERE c.id = :categoryId")
    Page<Book> findByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);

    // Tìm kiếm theo title, authors, description
    @Query("SELECT b FROM Book b WHERE " +
            "LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(b.authors) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(b.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Book> searchBooks(@Param("query") String query, Pageable pageable);

    // ======================================
    // Flash Sale Queries
    // ======================================

    /** Lấy sách đang bật flash sale (is_flash_sale = true) */
    List<Book> findByIsFlashSaleTrue();

    /** Lấy sách flash sale chưa hết hạn (đang active thật sự) */
    @Query("SELECT b FROM Book b WHERE b.isFlashSale = true AND b.flashSaleEndTime > :now")
    List<Book> findActiveFlashSaleBooks(@Param("now") LocalDateTime now);

    /** Lấy sách flash sale đã hết hạn nhưng chưa được reset */
    @Query("SELECT b FROM Book b WHERE b.isFlashSale = true AND b.flashSaleEndTime <= :now")
    List<Book> findExpiredFlashSales(@Param("now") LocalDateTime now);

    /** Lấy tất cả sách từng được thêm vào flash sale (để admin quản lý) */
    @Query("SELECT b FROM Book b WHERE b.isFlashSale = true")
    Page<Book> findAllFlashSaleBooks(Pageable pageable);
}