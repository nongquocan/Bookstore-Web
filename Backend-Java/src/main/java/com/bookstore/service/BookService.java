package com.bookstore.service;

import com.bookstore.dto.BookDTO;
import com.bookstore.entity.Book;
import com.bookstore.exception.EntityNotFoundException;
import com.bookstore.repository.BookRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * Lấy tất cả sách với filter và pagination
     */
    public Page<BookDTO> getAllBooks(
            int page,
            int limit,
            String category,
            String search,
            String sort,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            BigDecimal minRating) {

        Pageable pageable = createPageable(page, limit, sort);

        Page<Book> booksPage;

        if (search != null && !search.isEmpty()) {
            booksPage = bookRepository.searchBooks(search, pageable);
        } else if (category != null && !category.isEmpty()) {
            booksPage = bookRepository.findByCategoryAndIsAvailableTrue(category, pageable);
        } else {
            booksPage = bookRepository.findAll(pageable);
        }

        return booksPage.map(BookDTO::fromEntity);
    }

    /**
     * Lấy sách theo ID
     */
    public BookDTO getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + id));
        return BookDTO.fromEntity(book);
    }

    /**
     * Tìm kiếm sách
     */
    public List<BookDTO> searchBooks(String query, int limit) {
        Pageable pageable = PageRequest.of(0, Math.min(limit, 20), Sort.by("rating").descending());
        return bookRepository.searchBooks(query, pageable)
                .stream()
                .map(BookDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Lấy sách featured
     */
    public List<BookDTO> getFeaturedBooks(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by("rating").descending());
        return bookRepository.findByIsFeaturedTrueAndIsAvailableTrue(pageable)
                .stream()
                .map(BookDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Lấy sách mới
     */
    public List<BookDTO> getNewBooks(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by("createdAt").descending());
        return bookRepository.findByIsNewTrueAndIsAvailableTrue(pageable)
                .stream()
                .map(BookDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Lấy flash sale
     */
    public List<BookDTO> getFlashSaleBooks(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by("discountPercent").descending());
        return bookRepository.findByDiscountPercentGreaterThan25AndIsAvailableTrue(pageable)
                .stream()
                .map(BookDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Lấy sách theo danh mục
     */
    public List<BookDTO> getBooksByCategory(String category, int limit) {
        return bookRepository.findByCategoryAndIsAvailableTrue(category)
                .stream()
                .limit(limit)
                .map(BookDTO::fromEntity)
                .collect(Collectors.toList());
    }



    /**
     * Lấy thống kê
     */
    public StatsDTO getStats() {
        StatsDTO stats = new StatsDTO();
        stats.setTotalBooks(bookRepository.countByIsAvailableTrue());
        stats.setAverageRating(BigDecimal.valueOf(4.5)); // TODO: Calculate from DB
        return stats;
    }

    /**
     * Tạo Pageable
     */
    private Pageable createPageable(int page, int limit, String sortBy) {
        Sort sort = Sort.by("createdAt").descending();

        if (sortBy != null) {
            switch (sortBy.toLowerCase()) {
                case "price-asc":
                    sort = Sort.by("price").ascending();
                    break;
                case "price-desc":
                    sort = Sort.by("price").descending();
                    break;
                case "rating":
                    sort = Sort.by("rating").descending();
                    break;
                case "bestseller":
                    sort = Sort.by("ratingCount").descending();
                    break;
                case "newest":
                default:
                    sort = Sort.by("createdAt").descending();
            }
        }

        return PageRequest.of(Math.max(0, page - 1), Math.min(limit, 100), sort);
    }

    // DTOs for responses
    public static class StatsDTO {
        private Long totalBooks;
        private BigDecimal averageRating;

        public Long getTotalBooks() { return totalBooks; }
        public void setTotalBooks(Long totalBooks) { this.totalBooks = totalBooks; }
        public BigDecimal getAverageRating() { return averageRating; }
        public void setAverageRating(BigDecimal averageRating) { this.averageRating = averageRating; }
    }
}
