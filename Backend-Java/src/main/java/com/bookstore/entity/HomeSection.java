package com.bookstore.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "home_sections")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomeSection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title; // Tên hiển thị, ví dụ: "Sách Bán Chạy Nhất"

    @Column(nullable = false, unique = true)
    private String sectionKey; // Key định danh: "FEATURED", "FLASH_SALE", "NEW_BOOKS"

    @Column(nullable = false)
    private Integer itemCount; // Số lượng sách hiển thị

    @Column(nullable = false)
    private Integer displayOrder; // Thứ tự hiển thị trên trang

    @Column(nullable = false)
    private Boolean active = true;
}
