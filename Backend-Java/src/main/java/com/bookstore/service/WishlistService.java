package com.bookstore.service;

import com.bookstore.dto.WishlistDTO;
import com.bookstore.entity.Book;
import com.bookstore.entity.User;
import com.bookstore.entity.Wishlist;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private BookRepository bookRepository;

    public List<WishlistDTO> getWishlistItems(User user) {
        List<Wishlist> wishlists = wishlistRepository.findByUser(user);
        return wishlists.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public WishlistDTO addToWishlist(User user, String bookId) {
        Optional<Book> bookOpt = bookRepository.findById(bookId);
        if (!bookOpt.isPresent()) {
            throw new RuntimeException("Book not found");
        }
        Book book = bookOpt.get();

        Optional<Wishlist> existing = wishlistRepository.findByUserAndBook(user, bookId);
        if (existing.isPresent()) {
            return convertToDTO(existing.get());
        } else {
            Wishlist wishlist = new Wishlist();
            wishlist.setUser(user);
            wishlist.setBook(book);
            wishlistRepository.save(wishlist);
            return convertToDTO(wishlist);
        }
    }

    @Transactional
    public void removeFromWishlist(User user, String bookId) {
        wishlistRepository.deleteByUserAndBookId(user, bookId);
    }

    private WishlistDTO convertToDTO(Wishlist wishlist) {
        WishlistDTO dto = new WishlistDTO();
        dto.setId(wishlist.getId());
        dto.setBookId(wishlist.getBook().getId());
        dto.setBookTitle(wishlist.getBook().getTitle());
        dto.setBookAuthors(wishlist.getBook().getAuthors());
        dto.setBookThumbnail(wishlist.getBook().getThumbnail());
        dto.setPrice(wishlist.getBook().getListPrice());
        dto.setAddedAt(wishlist.getAddedAt());
        return dto;
    }
}
