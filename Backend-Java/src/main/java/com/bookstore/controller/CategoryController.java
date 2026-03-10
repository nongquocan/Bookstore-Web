package com.bookstore.controller;

import com.bookstore.dto.ApiResponse;
import com.bookstore.entity.Category;
import com.bookstore.repository.CategoryRepository;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;



@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5500", "http://localhost:8000"})
@Slf4j
public class CategoryController {

    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * GET /api/categories - Lấy tất cả danh mục
     */
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllCategories() {
        try {
            List<Category> categories = categoryRepository.findAll();
            return ResponseEntity.ok(ApiResponse.success(categories));
        } catch (Exception e) {
            log.error("Error getting categories: {}", e.getMessage());
            return ResponseEntity.ok(ApiResponse.error("Failed to get categories", e.getMessage()));
        }
    }

    /**
     * GET /api/categories/:id - Lấy danh mục theo ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getCategoryById(@PathVariable Long id) {
        try {
            Category category = categoryRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            return ResponseEntity.ok(ApiResponse.success(category));
        } catch (Exception e) {
            log.error("Error getting category: {}", e.getMessage());
            return ResponseEntity.ok(ApiResponse.error("Category not found", e.getMessage()));
        }
    }

    /**
     * Health check
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<?>> health() {
        return ResponseEntity.ok(ApiResponse.success("Category API is running"));
    }
}
