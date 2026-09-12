package com.bookstore.repository;

import com.bookstore.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Tìm tất cả review của một sách
    Page<Review> findByBookId(String bookId, Pageable pageable);
    List<Review> findByBookId(String bookId);

    // Tìm review của user cho một sách cụ thể
    Review findByBookIdAndUserId(String bookId, Long userId);

    // Tính trung bình rating cho một sách
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.book.id = :bookId")
    Double findAverageRatingByBookId(@Param("bookId") String bookId);

    // Đếm số review cho một sách
    @Query("SELECT COUNT(r) FROM Review r WHERE r.book.id = :bookId")
    Long countByBookId(@Param("bookId") String bookId);

    // Lấy danh sách tổng hợp review theo sách
    @Query("SELECT new com.bookstore.dto.BookReviewSummaryDTO(r.book.id, r.book.title, COUNT(r), AVG(r.rating)) " +
           "FROM Review r GROUP BY r.book.id, r.book.title ORDER BY COUNT(r) DESC")
    Page<com.bookstore.dto.BookReviewSummaryDTO> findBookReviewSummaries(Pageable pageable);
}
