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
import java.time.LocalDateTime;
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
            Long categoryId,
            String search,
            String sort,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            BigDecimal minRating) {

        Pageable pageable = createPageable(page, limit, sort);

        Page<Book> booksPage;

        if (search != null && !search.isEmpty()) {
            booksPage = bookRepository.searchBooks(search, pageable);
        } else if (categoryId != null) {
            // Ưu tiên filter theo category ID qua junction table (chính xác nhất)
            booksPage = bookRepository.findByCategoryId(categoryId, pageable);
        } else if (category != null && !category.isEmpty()) {
            // Fallback: filter theo search_category text (LIKE)
            booksPage = bookRepository.findBySearchCategoryContaining(category, pageable);
        } else {
            booksPage = bookRepository.findAll(pageable);
        }

        return booksPage.map(BookDTO::fromEntity);
    }

    /**
     * Lấy sách theo ID
     */
    public BookDTO getBookById(String id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + id));
        return BookDTO.fromEntity(book);
    }

    /**
     * Tìm kiếm sách
     */
    public List<BookDTO> searchBooks(String query, int limit) {
        Pageable pageable = PageRequest.of(0, Math.min(limit, 20), Sort.by("averageRating").descending());
        return bookRepository.searchBooks(query, pageable)
                .stream()
                .map(BookDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Lấy sách featured (rating cao nhất)
     */
    public List<BookDTO> getFeaturedBooks(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by("averageRating").descending());
        return bookRepository.findAll(pageable)
                .stream()
                .map(BookDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Lấy sách mới (theo thứ tự trong DB)
     */
    public List<BookDTO> getNewBooks(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return bookRepository.findAll(pageable)
                .stream()
                .map(BookDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Lấy flash sale - sách đang có flash sale hợp lệ (chưa hết hạn)
     */
    public List<BookDTO> getFlashSaleBooks(int limit) {
        LocalDateTime now = LocalDateTime.now();
        List<Book> activeFlashSale = bookRepository.findActiveFlashSaleBooks(now);
        if (activeFlashSale.isEmpty()) {
            // Fallback: trả sách có giá cao nhất nếu chưa có flash sale nào
            Pageable pageable = PageRequest.of(0, limit, Sort.by("listPrice").descending());
            return bookRepository.findAll(pageable)
                    .stream()
                    .map(BookDTO::fromEntity)
                    .collect(Collectors.toList());
        }
        return activeFlashSale.stream()
                .limit(limit)
                .map(BookDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Lấy sách theo danh mục
     */
    public List<BookDTO> getBooksByCategory(String category, int limit) {
        return bookRepository.findBySearchCategoryContaining(category)
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
        stats.setTotalBooks(bookRepository.count());
        stats.setAverageRating(BigDecimal.valueOf(4.5));
        return stats;
    }

    /**
     * Tạo Pageable
     */
    private Pageable createPageable(int page, int limit, String sortBy) {
        Sort sort = Sort.by("averageRating").descending();

        if (sortBy != null) {
            switch (sortBy.toLowerCase()) {
                case "price-asc":
                    sort = Sort.by("listPrice").ascending();
                    break;
                case "price-desc":
                    sort = Sort.by("listPrice").descending();
                    break;
                case "rating":
                    sort = Sort.by("averageRating").descending();
                    break;
                case "bestseller":
                    sort = Sort.by("ratingsCount").descending();
                    break;
                default:
                    sort = Sort.by("averageRating").descending();
            }
        }

        return PageRequest.of(Math.max(0, page - 1), Math.min(limit, 2000), sort);
    }

    public static class StatsDTO {
        private Long totalBooks;
        private BigDecimal averageRating;

        public Long getTotalBooks() { return totalBooks; }
        public void setTotalBooks(Long totalBooks) { this.totalBooks = totalBooks; }
        public BigDecimal getAverageRating() { return averageRating; }
        public void setAverageRating(BigDecimal averageRating) { this.averageRating = averageRating; }
    }
}