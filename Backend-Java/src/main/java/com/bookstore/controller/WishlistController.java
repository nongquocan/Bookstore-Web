package com.bookstore.controller;

import com.bookstore.dto.ApiResponse;
import com.bookstore.dto.WishlistDTO;
import com.bookstore.entity.User;
import com.bookstore.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<WishlistDTO>>> getWishlist(@AuthenticationPrincipal User user) {
        List<WishlistDTO> wishlist = wishlistService.getWishlistItems(user);
        return ResponseEntity.ok(ApiResponse.success("Wishlist retrieved successfully", wishlist));
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<WishlistDTO>> addToWishlist(@AuthenticationPrincipal User user,
                                                                  @RequestParam String bookId) {
        WishlistDTO item = wishlistService.addToWishlist(user, bookId);
        return ResponseEntity.ok(ApiResponse.success("Added to wishlist", item));
    }

    @DeleteMapping("/remove")
    public ResponseEntity<ApiResponse<Void>> removeFromWishlist(@AuthenticationPrincipal User user,
                                                                @RequestParam String bookId) {
        wishlistService.removeFromWishlist(user, bookId);
        return ResponseEntity.ok(ApiResponse.success("Removed from wishlist", null));
    }
}
