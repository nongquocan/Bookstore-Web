package com.bookstore.controller;

import com.bookstore.dto.ApiResponse;
import com.bookstore.dto.ReviewDTO;
import com.bookstore.dto.ReviewRequestDTO;
import com.bookstore.service.ReviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/api/books/{bookId}/reviews")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5500", "http://localhost:8000"})
@Slf4j
@Validated
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    /**
     * GET /api/books/{bookId}/reviews - Lấy tất cả review của một sách
     */
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getReviewsByBookId(
            @PathVariable String bookId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {

        try {
            Page<ReviewDTO> reviews = reviewService.getReviewsByBookId(bookId, page, limit);

            Map<String, Object> data = new HashMap<>();
            data.put("reviews", reviews.getContent());
            data.put("currentPage", reviews.getNumber() + 1);
            data.put("totalPages", reviews.getTotalPages());
            data.put("totalElements", reviews.getTotalElements());
            data.put("size", reviews.getSize());

            return ResponseEntity.ok(ApiResponse.success("Reviews retrieved successfully", data));
        } catch (Exception e) {
            log.error("Error getting reviews for book {}: {}", bookId, e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * POST /api/books/{bookId}/reviews - Tạo review mới
     */
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> createReview(
            @PathVariable String bookId,
            @Valid @RequestBody ReviewRequestDTO request,
            HttpServletRequest httpRequest) {

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            com.bookstore.entity.User user = (com.bookstore.entity.User) auth.getPrincipal();
            Long userId = user.getId();
            ReviewDTO review = reviewService.createReview(bookId, userId, request);
            return ResponseEntity.ok(ApiResponse.success("Review created successfully", review));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error creating review for book {}: {}", bookId, e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * PUT /api/books/{bookId}/reviews/{reviewId} - Cập nhật review
     */
    @PutMapping("/{reviewId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> updateReview(
            @PathVariable String bookId,
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewRequestDTO request,
            HttpServletRequest httpRequest) {

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            com.bookstore.entity.User user = (com.bookstore.entity.User) auth.getPrincipal();
            Long userId = user.getId();
            ReviewDTO review = reviewService.updateReview(reviewId, userId, request);
            return ResponseEntity.ok(ApiResponse.success("Review updated successfully", review));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error updating review {}: {}", reviewId, e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * DELETE /api/books/{bookId}/reviews/{reviewId} - Xóa review
     */
    @DeleteMapping("/{reviewId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> deleteReview(
            @PathVariable String bookId,
            @PathVariable Long reviewId,
            HttpServletRequest httpRequest) {

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            com.bookstore.entity.User user = (com.bookstore.entity.User) auth.getPrincipal();
            Long userId = user.getId();
            reviewService.deleteReview(reviewId, userId);
            return ResponseEntity.ok(ApiResponse.success("Review deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error deleting review {}: {}", reviewId, e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
