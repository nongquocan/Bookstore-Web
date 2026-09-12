package com.bookstore.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bookstore.dto.ApiResponse;
import com.bookstore.dto.BookDTO;
import com.bookstore.service.BookService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5500", "http://localhost:8000"})
@Slf4j
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * GET /api/books - Lấy tất cả sách
     */
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllBooks(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int limit,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) BigDecimal minRating) {

        try {
            Page<BookDTO> booksPage = bookService.getAllBooks(
                    page, limit, category, categoryId, search, sort, minPrice, maxPrice, minRating);

            Map<String, Object> response = new HashMap<>();
            response.put("data", booksPage.getContent());
            response.put("pagination", Map.of(
                    "page", booksPage.getNumber() + 1,
                    "limit", booksPage.getSize(),
                    "total", booksPage.getTotalElements(),
                    "totalPages", booksPage.getTotalPages()
            ));

            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            log.error("Error getting books: {}", e.getMessage());
            return ResponseEntity.ok(ApiResponse.error("Failed to get books", e.getMessage()));
        }
    }

    /**
     * GET /api/books/:id - Lấy sách theo ID (String vì book_id là varchar)
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getBookById(@PathVariable String id) {
        try {
            BookDTO book = bookService.getBookById(id);
            return ResponseEntity.ok(ApiResponse.success(book));
        } catch (Exception e) {
            log.error("Error getting book: {}", e.getMessage());
            return ResponseEntity.ok(ApiResponse.error("Book not found", e.getMessage()));
        }
    }

    /**
     * GET /api/books/search/{query} - Tìm kiếm sách
     */
    @GetMapping("/search/{query}")
    public ResponseEntity<ApiResponse<?>> searchBooks(
            @PathVariable String query,
            @RequestParam(defaultValue = "20") int limit) {

        try {
            List<BookDTO> books = bookService.searchBooks(query, limit);
            return ResponseEntity.ok(ApiResponse.success(books));
        } catch (Exception e) {
            log.error("Error searching books: {}", e.getMessage());
            return ResponseEntity.ok(ApiResponse.error("Search failed", e.getMessage()));
        }
    }

    /**
     * GET /api/books/featured/list - Lấy sách featured
     */
    @GetMapping("/featured/list")
    public ResponseEntity<ApiResponse<?>> getFeaturedBooks(
            @RequestParam(defaultValue = "8") int limit) {

        try {
            List<BookDTO> books = bookService.getFeaturedBooks(limit);
            return ResponseEntity.ok(ApiResponse.success(books));
        } catch (Exception e) {
            log.error("Error getting featured books: {}", e.getMessage());
            return ResponseEntity.ok(ApiResponse.error("Failed to get featured books", e.getMessage()));
        }
    }

    /**
     * GET /api/books/new/list - Lấy sách mới
     */
    @GetMapping("/new/list")
    public ResponseEntity<ApiResponse<?>> getNewBooks(
            @RequestParam(defaultValue = "8") int limit) {

        try {
            List<BookDTO> books = bookService.getNewBooks(limit);
            return ResponseEntity.ok(ApiResponse.success(books));
        } catch (Exception e) {
            log.error("Error getting new books: {}", e.getMessage());
            return ResponseEntity.ok(ApiResponse.error("Failed to get new books", e.getMessage()));
        }
    }

    /**
     * GET /api/books/flash-sale/list - Lấy flash sale
     */
    @GetMapping("/flash-sale/list")
    public ResponseEntity<ApiResponse<?>> getFlashSaleBooks(
            @RequestParam(defaultValue = "10") int limit) {

        try {
            List<BookDTO> books = bookService.getFlashSaleBooks(limit);
            return ResponseEntity.ok(ApiResponse.success(books));
        } catch (Exception e) {
            log.error("Error getting flash sale books: {}", e.getMessage());
            return ResponseEntity.ok(ApiResponse.error("Failed to get flash sale books", e.getMessage()));
        }
    }

    /**
     * GET /api/books/category/{name} - Lấy sách theo danh mục
     */
    @GetMapping("/category/{name}")
    public ResponseEntity<ApiResponse<?>> getBooksByCategory(
            @PathVariable String name,
            @RequestParam(defaultValue = "12") int limit) {

        try {
            List<BookDTO> books = bookService.getBooksByCategory(name, limit);
            return ResponseEntity.ok(ApiResponse.success(books));
        } catch (Exception e) {
            log.error("Error getting books by category: {}", e.getMessage());
            return ResponseEntity.ok(ApiResponse.error("Failed to get books by category", e.getMessage()));
        }
    }

    /**
     * GET /api/books/stats/overview - Thống kê
     */
    @GetMapping("/stats/overview")
    public ResponseEntity<ApiResponse<?>> getStats() {
        try {
            BookService.StatsDTO stats = bookService.getStats();
            return ResponseEntity.ok(ApiResponse.success(stats));
        } catch (Exception e) {
            log.error("Error getting stats: {}", e.getMessage());
            return ResponseEntity.ok(ApiResponse.error("Failed to get stats", e.getMessage()));
        }
    }

    /**
     * Health check
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<?>> health() {
        Map<String, Object> data = Map.of(
                "status", "ok",
                "message", "BookStore API is running"
        );
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}