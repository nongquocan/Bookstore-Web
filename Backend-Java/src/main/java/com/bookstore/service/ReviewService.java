package com.bookstore.service;

import com.bookstore.dto.ReviewDTO;
import com.bookstore.dto.ReviewRequestDTO;
import com.bookstore.entity.Book;
import com.bookstore.entity.Review;
import com.bookstore.entity.User;
import com.bookstore.exception.EntityNotFoundException;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.ReviewRepository;
import com.bookstore.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public ReviewService(ReviewRepository reviewRepository, BookRepository bookRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    /**
     * Lấy tất cả review của một sách
     */
    public Page<ReviewDTO> getReviewsByBookId(String bookId, int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("createdAt").descending());
        Page<Review> reviews = reviewRepository.findByBookId(bookId, pageable);
        return reviews.map(ReviewDTO::fromEntity);
    }

    /**
     * Lấy toàn bộ review trên hệ thống (dành cho Admin)
     */
    public Page<ReviewDTO> getAllReviews(Pageable pageable) {
        Page<Review> reviews = reviewRepository.findAll(pageable);
        return reviews.map(ReviewDTO::fromEntity);
    }

    /**
     * Lấy danh sách tổng hợp sách có review (dành cho Admin)
     */
    public Page<com.bookstore.dto.BookReviewSummaryDTO> getReviewSummaries(Pageable pageable) {
        return reviewRepository.findBookReviewSummaries(pageable);
    }

    /**
     * Tạo review mới
     */
    public ReviewDTO createReview(String bookId, Long userId, ReviewRequestDTO request) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Kiểm tra xem user đã review sách này chưa
        Review existingReview = reviewRepository.findByBookIdAndUserId(bookId, userId);
        if (existingReview != null) {
            throw new IllegalArgumentException("User has already reviewed this book");
        }

        Review review = new Review();
        review.setBook(book);
        review.setUser(user);
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        Review savedReview = reviewRepository.save(review);

        // Cập nhật averageRating và ratingsCount cho sách
        updateBookRating(bookId);

        return ReviewDTO.fromEntity(savedReview);
    }

    /**
     * Cập nhật review
     */
    public ReviewDTO updateReview(Long reviewId, Long userId, ReviewRequestDTO request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));

        if (!review.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("User can only update their own reviews");
        }

        review.setRating(request.getRating());
        review.setComment(request.getComment());

        Review savedReview = reviewRepository.save(review);

        // Cập nhật averageRating và ratingsCount cho sách
        updateBookRating(review.getBook().getId());

        return ReviewDTO.fromEntity(savedReview);
    }

    /**
     * Xóa review
     */
    public void deleteReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));

        if (!review.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("User can only delete their own reviews");
        }

        String bookId = review.getBook().getId();
        reviewRepository.delete(review);

        // Cập nhật averageRating và ratingsCount cho sách
        updateBookRating(bookId);
    }

    /**
     * Xóa review bởi Admin (không cần xét quyền sở hữu)
     */
    public void deleteReviewByAdmin(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));

        String bookId = review.getBook().getId();
        reviewRepository.delete(review);

        // Cập nhật averageRating và ratingsCount cho sách
        updateBookRating(bookId);
    }

    /**
     * Cập nhật rating trung bình và số lượng review cho sách
     */
    private void updateBookRating(String bookId) {
        Double avgRating = reviewRepository.findAverageRatingByBookId(bookId);
        Long count = reviewRepository.countByBookId(bookId);

        Book book = bookRepository.findById(bookId).orElseThrow(() -> new EntityNotFoundException("Book not found"));
        book.setAverageRating(avgRating != null ? avgRating.floatValue() : 0.0f);
        book.setRatingsCount(count.intValue());
        bookRepository.save(book);
    }
}
