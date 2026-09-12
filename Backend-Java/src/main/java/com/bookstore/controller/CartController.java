package com.bookstore.controller;

import com.bookstore.dto.ApiResponse;
import com.bookstore.dto.CartDTO;
import com.bookstore.entity.User;
import com.bookstore.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CartDTO>>> getCart(@AuthenticationPrincipal User user) {
        List<CartDTO> cart = cartService.getCartItems(user);
        return ResponseEntity.ok(ApiResponse.success("Cart retrieved successfully", cart));
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<CartDTO>> addToCart(@AuthenticationPrincipal User user,
                                                          @RequestParam String bookId,
                                                          @RequestParam(defaultValue = "1") Integer quantity) {
        CartDTO item = cartService.addToCart(user, bookId, quantity);
        return ResponseEntity.ok(ApiResponse.success("Added to cart", item));
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<CartDTO>> updateCart(@AuthenticationPrincipal User user,
                                                           @RequestParam String bookId,
                                                           @RequestParam Integer quantity) {
        CartDTO item = cartService.updateCartItem(user, bookId, quantity);
        return ResponseEntity.ok(ApiResponse.success("Cart updated", item));
    }

    @DeleteMapping("/remove")
    public ResponseEntity<ApiResponse<Void>> removeFromCart(@AuthenticationPrincipal User user,
                                                            @RequestParam String bookId) {
        cartService.removeFromCart(user, bookId);
        return ResponseEntity.ok(ApiResponse.success("Removed from cart", null));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<Void>> clearCart(@AuthenticationPrincipal User user) {
        cartService.clearCart(user);
        return ResponseEntity.ok(ApiResponse.success("Cart cleared", null));
    }
}
