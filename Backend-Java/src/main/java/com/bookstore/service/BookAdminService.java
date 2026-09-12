package com.bookstore.service;

import com.bookstore.dto.BookDTO;
import com.bookstore.dto.FlashSaleRequest;
import com.bookstore.dto.UpdateBookPriceRequest;
import com.bookstore.entity.Book;
import com.bookstore.exception.EntityNotFoundException;
import com.bookstore.repository.BookRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class BookAdminService {

    @Autowired
    private BookRepository bookRepository;

    // =========================================================
    // CẬP NHẬT GIÁ SÁCH
    // =========================================================

    /**
     * Admin sửa giá sách (list_price)
     */
    public BookDTO updateBookPrice(String bookId, UpdateBookPriceRequest req) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found: " + bookId));

        if (req.getListPrice() == null || req.getListPrice() < 0) {
            throw new IllegalArgumentException("Giá sách không hợp lệ");
        }

        float oldPrice = book.getListPrice() != null ? book.getListPrice() : 0f;
        book.setListPrice(req.getListPrice());
        
        if (req.getStockQuantity() != null && req.getStockQuantity() >= 0) {
            book.setStockQuantity(req.getStockQuantity());
        }

        // Nếu đang có flash sale, tính lại flashSalePrice theo % giảm giá mới
        if (Boolean.TRUE.equals(book.getIsFlashSale()) && book.getFlashSaleDiscountPercent() != null) {
            float flashSalePrice = req.getListPrice() * (1f - book.getFlashSaleDiscountPercent() / 100f);
            book.setFlashSalePrice(flashSalePrice);
        }

        Book saved = bookRepository.save(book);
        log.info("[Admin] Đã cập nhật giá sách {} từ {} → {}", bookId, oldPrice, req.getListPrice());
        return BookDTO.fromEntity(saved);
    }

    // =========================================================
    // FLASH SALE
    // =========================================================

    /**
     * Bật flash sale cho một cuốn sách
     */
    public BookDTO enableFlashSale(String bookId, FlashSaleRequest req) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found: " + bookId));

        if (req.getDiscountPercent() == null || req.getDiscountPercent() < 1 || req.getDiscountPercent() > 99) {
            throw new IllegalArgumentException("Phần trăm giảm giá phải từ 1 đến 99");
        }
        if (req.getEndTime() == null) {
            throw new IllegalArgumentException("Thời gian kết thúc flash sale không được để trống");
        }
        if (req.getEndTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Thời gian kết thúc phải sau thời điểm hiện tại");
        }

        float listPrice = book.getListPrice() != null ? book.getListPrice() : 0f;
        float flashSalePrice = listPrice * (1f - req.getDiscountPercent() / 100f);

        book.setIsFlashSale(true);
        book.setFlashSaleDiscountPercent(req.getDiscountPercent());
        book.setFlashSalePrice(flashSalePrice);
        book.setFlashSaleStartTime(req.getStartTime() != null ? req.getStartTime() : LocalDateTime.now());
        book.setFlashSaleEndTime(req.getEndTime());

        Book saved = bookRepository.save(book);
        log.info("[Admin] Đã bật flash sale cho sách {} - giảm {}% - giá {} → {}",
                bookId, req.getDiscountPercent(), listPrice, flashSalePrice);
        return BookDTO.fromEntity(saved);
    }

    /**
     * Tắt flash sale cho một cuốn sách (giá trở về bình thường)
     */
    public BookDTO disableFlashSale(String bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found: " + bookId));

        book.setIsFlashSale(false);
        book.setFlashSalePrice(null);
        book.setFlashSaleDiscountPercent(null);
        book.setFlashSaleStartTime(null);
        book.setFlashSaleEndTime(null);

        Book saved = bookRepository.save(book);
        log.info("[Admin] Đã tắt flash sale cho sách {}", bookId);
        return BookDTO.fromEntity(saved);
    }

    /**
     * Lấy danh sách sách đang trong flash sale
     */
    @Transactional(readOnly = true)
    public List<BookDTO> getActiveFlashSaleBooks() {
        return bookRepository.findByIsFlashSaleTrue()
                .stream()
                .map(BookDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Lấy tất cả sách flash sale (kể cả hết hạn) để admin xem
     */
    @Transactional(readOnly = true)
    public Page<BookDTO> getAllFlashSaleBooks(Pageable pageable) {
        return bookRepository.findAllFlashSaleBooks(pageable)
                .map(BookDTO::fromEntity);
    }

    /**
     * Scheduler gọi để reset các flash sale đã hết hạn
     */
    @Transactional
    public int expireFlashSales() {
        LocalDateTime now = LocalDateTime.now();
        List<Book> expired = bookRepository.findExpiredFlashSales(now);
        for (Book book : expired) {
            book.setIsFlashSale(false);
            book.setFlashSalePrice(null);
            book.setFlashSaleDiscountPercent(null);
            book.setFlashSaleStartTime(null);
            book.setFlashSaleEndTime(null);
            bookRepository.save(book);
            log.info("[Scheduler] Flash sale hết hạn - sách {} giá quay về bình thường", book.getId());
        }
        return expired.size();
    }
}
