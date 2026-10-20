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

    @Transactional
    public CartDTO addToCart(User user, String bookId, Integer quantity) {
        requirePositive(quantity);
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));

        Cart cart = cartRepository.findByUserAndBook(user, bookId).orElse(null);
        int newQuantity = (cart != null ? cart.getQuantity() : 0) + quantity;
        requireInStock(book, newQuantity);

        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cart.setBook(book);
        }
        cart.setQuantity(newQuantity);
        cartRepository.save(cart);
        return convertToDTO(cart);
    }

    @Transactional
    public CartDTO updateCartItem(User user, String bookId, Integer quantity) {
        requirePositive(quantity);
        Cart cart = cartRepository.findByUserAndBook(user, bookId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));
        requireInStock(cart.getBook(), quantity);
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

    private static void requirePositive(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
        }
    }

    private static void requireInStock(Book book, int quantity) {
        int stock = book.getStockQuantity() != null ? book.getStockQuantity() : 0;
        if (quantity > stock) {
            throw new IllegalArgumentException("Chỉ còn " + stock + " cuốn \"" + book.getTitle() + "\" trong kho");
        }
    }

    private CartDTO convertToDTO(Cart cart) {
        CartDTO dto = new CartDTO();
        dto.setId(cart.getId());
        dto.setBookId(cart.getBook().getId());
        dto.setBookTitle(cart.getBook().getTitle());
        dto.setBookThumbnail(cart.getBook().getThumbnail());
        dto.setQuantity(cart.getQuantity());
        Float price = cart.getBook().resolveEffectivePrice();
        dto.setPrice(price);
        dto.setSubtotal(price * cart.getQuantity());
        dto.setAddedAt(cart.getAddedAt());
        return dto;
    }
}
