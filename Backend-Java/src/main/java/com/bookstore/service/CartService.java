package com.bookstore.service;

import com.bookstore.dto.CartDTO;
import com.bookstore.entity.Book;
import com.bookstore.entity.Cart;
import com.bookstore.entity.User;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private BookRepository bookRepository;

    public List<CartDTO> getCartItems(User user) {
        List<Cart> carts = cartRepository.findByUser(user);
        return carts.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public CartDTO addToCart(User user, String bookId, Integer quantity) {
        Optional<Book> bookOpt = bookRepository.findById(bookId);
        if (!bookOpt.isPresent()) {
            throw new RuntimeException("Book not found");
        }
        Book book = bookOpt.get();

        Optional<Cart> existing = cartRepository.findByUserAndBook(user, bookId);
        if (existing.isPresent()) {
            Cart cart = existing.get();
            cart.setQuantity(cart.getQuantity() + quantity);
            cartRepository.save(cart);
            return convertToDTO(cart);
        } else {
            Cart cart = new Cart();
            cart.setUser(user);
            cart.setBook(book);
            cart.setQuantity(quantity);
            cartRepository.save(cart);
            return convertToDTO(cart);
        }
    }

    public CartDTO updateCartItem(User user, String bookId, Integer quantity) {
        Optional<Cart> cartOpt = cartRepository.findByUserAndBook(user, bookId);
        if (!cartOpt.isPresent()) {
            throw new RuntimeException("Cart item not found");
        }
        Cart cart = cartOpt.get();
        cart.setQuantity(quantity);
        cartRepository.save(cart);
        return convertToDTO(cart);
    }

    @Transactional
    public void removeFromCart(User user, String bookId) {
        cartRepository.deleteByUserAndBookId(user, bookId);
    }

    @Transactional
    public void clearCart(User user) {
        List<Cart> carts = cartRepository.findByUser(user);
        cartRepository.deleteAll(carts);
    }

    private CartDTO convertToDTO(Cart cart) {
        CartDTO dto = new CartDTO();
        dto.setId(cart.getId());
        dto.setBookId(cart.getBook().getId());
        dto.setBookTitle(cart.getBook().getTitle());
        dto.setBookThumbnail(cart.getBook().getThumbnail());
        dto.setQuantity(cart.getQuantity());
        dto.setPrice(cart.getBook().getListPrice());
        dto.setSubtotal(cart.getBook().getListPrice() * cart.getQuantity());
        dto.setAddedAt(cart.getAddedAt());
        return dto;
    }
}
