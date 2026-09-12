package com.bookstore.controller;

import com.bookstore.dto.ApiResponse;
import com.bookstore.entity.HomeSection;
import com.bookstore.repository.HomeSectionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class HomeSectionController {

    @Autowired
    private HomeSectionRepository homeSectionRepository;

    /**
     * GET /api/home-layout - Lấy cấu trúc trang chủ (Công khai)
     */
    @GetMapping("/home-layout")
    public ResponseEntity<ApiResponse<List<HomeSection>>> getHomeLayout() {
        try {
            List<HomeSection> sections = homeSectionRepository.findAllByActiveTrueOrderByDisplayOrderAsc();
            return ResponseEntity.ok(ApiResponse.success(sections));
        } catch (Exception e) {
            log.error("Error fetching home layout: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to fetch layout", e.getMessage()));
        }
    }

    /**
     * GET /api/admin/home-layout - Lấy toàn bộ cấu trúc (Kể cả ẩn - Chỉ Admin)
     */
    @GetMapping("/admin/home-layout")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<HomeSection>>> getAdminHomeLayout() {
        try {
            List<HomeSection> sections = homeSectionRepository.findAllByOrderByDisplayOrderAsc();
            return ResponseEntity.ok(ApiResponse.success(sections));
        } catch (Exception e) {
            log.error("Error fetching admin home layout: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to fetch layout for admin", e.getMessage()));
        }
    }

    /**
     * PUT /api/admin/home-layout/{id} - Cập nhật cấu trúc (Chỉ Admin)
     */
    @PutMapping("/admin/home-layout/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<HomeSection>> updateHomeLayout(
            @PathVariable Long id,
            @RequestBody HomeSection updatedSection) {
        try {
            HomeSection section = homeSectionRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Section not found"));

            section.setTitle(updatedSection.getTitle());
            section.setItemCount(updatedSection.getItemCount());
            section.setDisplayOrder(updatedSection.getDisplayOrder());
            section.setActive(updatedSection.getActive());

            HomeSection saved = homeSectionRepository.save(section);
            log.info("Admin updated home section: {}", saved.getSectionKey());
            return ResponseEntity.ok(ApiResponse.success("Updated successfully", saved));
        } catch (Exception e) {
            log.error("Error updating home section: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to update section", e.getMessage()));
        }
    }
}
