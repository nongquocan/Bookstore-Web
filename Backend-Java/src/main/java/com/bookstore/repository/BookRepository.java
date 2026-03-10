package com.bookstore.repository;

import com.bookstore.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    // Tìm sách theo SKU
    Optional<Book> findBySku(String sku);
    
    // Tìm sách featured
    Page<Book> findByIsFeaturedTrueAndIsAvailableTrue(Pageable pageable);
    
    // Tìm sách mới
    Page<Book> findByIsNewTrueAndIsAvailableTrue(Pageable pageable);
    
    // Tìm flash sale (discount > 25%)
    Page<Book> findByDiscountPercentGreaterThan25AndIsAvailableTrue(Pageable pageable);
    
    // Tìm sách theo danh mục
    Page<Book> findByCategoryAndIsAvailableTrue(String category, Pageable pageable);
    
    // Tìm kiếm
    @Query("SELECT b FROM Book b WHERE " +
           "(LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.author) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.description) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "AND b.isAvailable = true")
    Page<Book> searchBooks(@Param("query") String query, Pageable pageable);
    
    // Lấy sách với discount cao
    Page<Book> findByDiscountPercentGreaterThanAndIsAvailableTrueOrderByDiscountPercentDesc(
            Integer discountPercent, 
            Pageable pageable);
    
    // Đếm tổng số sách
    Long countByIsAvailableTrue();
    
    // Lấy tất cả sách theo danh mục
    List<Book> findByCategoryAndIsAvailableTrue(String category);
}
