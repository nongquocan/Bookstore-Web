package com.bookstore.controller;

import com.bookstore.dto.ApiResponse;
import com.bookstore.dto.BookDTO;
import com.bookstore.dto.FlashSaleRequest;
import com.bookstore.dto.UpdateBookPriceRequest;
import com.bookstore.dto.UserDTO;
import com.bookstore.dto.OrderDTO;
import com.bookstore.dto.ReviewDTO;
import com.bookstore.service.BookAdminService;
import com.bookstore.service.OrderService;
import com.bookstore.service.ReviewService;
import com.bookstore.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    @Autowired
    private UserService userService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private BookAdminService bookAdminService;
    @Autowired
    private com.bookstore.repository.UserRepository userRepository;
    @Autowired
    private com.bookstore.repository.BookRepository bookRepository;
    @Autowired
    private com.bookstore.repository.OrderRepository orderRepository;

    // ==========================================
    // BOOKS MANAGEMENT ENDPOINTS
    // ==========================================

    /**
     * Cập nhật giá sách (Chỉ Admin)
     */
    @PutMapping("/books/{bookId}/price")
    public ResponseEntity<?> updateBookPrice(@PathVariable String bookId,
                                              @RequestBody UpdateBookPriceRequest request) {
        try {
            BookDTO book = bookAdminService.updateBookPrice(bookId, request);
            return ResponseEntity.ok(ApiResponse.success("Cập nhật giá sách thành công", book));
        } catch (Exception e) {
            log.error("Error updating book price: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Lỗi cập nhật giá", e.getMessage()));
        }
    }

    /**
     * Bật Flash Sale cho sách (Chỉ Admin)
     */
    @PostMapping("/books/{bookId}/flash-sale")
    public ResponseEntity<?> enableFlashSale(@PathVariable String bookId,
                                              @RequestBody FlashSaleRequest request) {
        try {
            BookDTO book = bookAdminService.enableFlashSale(bookId, request);
            return ResponseEntity.ok(ApiResponse.success("Bật flash sale thành công", book));
        } catch (Exception e) {
            log.error("Error enabling flash sale: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Lỗi bật flash sale", e.getMessage()));
        }
    }

    /**
     * Tắt Flash Sale cho sách (Chỉ Admin)
     */
    @DeleteMapping("/books/{bookId}/flash-sale")
    public ResponseEntity<?> disableFlashSale(@PathVariable String bookId) {
        try {
            BookDTO book = bookAdminService.disableFlashSale(bookId);
            return ResponseEntity.ok(ApiResponse.success("Tắt flash sale thành công", book));
        } catch (Exception e) {
            log.error("Error disabling flash sale: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Lỗi tắt flash sale", e.getMessage()));
        }
    }

    /**
     * Lấy danh sách sách đang flash sale (Chỉ Admin)
     */
    @GetMapping("/books/flash-sale")
    public ResponseEntity<?> getActiveFlashSaleBooks() {
        try {
            java.util.List<BookDTO> books = bookAdminService.getActiveFlashSaleBooks();
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách flash sale thành công", books));
        } catch (Exception e) {
            log.error("Error getting flash sale books: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi tải flash sale", e.getMessage()));
        }
    }

    // ==========================================
    // DASHBOARD ENDPOINTS
    // ==========================================

    @GetMapping("/dashboard/stats")
    public ResponseEntity<?> getDashboardStats() {
        try {
            long totalUsers = userRepository.count();
            long totalBooks = bookRepository.count();
            long totalOrders = orderRepository.count();
            java.math.BigDecimal revenue = orderRepository.calculateTotalRevenue();
            if (revenue == null) revenue = java.math.BigDecimal.ZERO;

            java.util.Map<String, Object> stats = new java.util.HashMap<>();
            stats.put("totalUsers", totalUsers);
            stats.put("totalBooks", totalBooks);
            stats.put("totalOrders", totalOrders);
            stats.put("totalRevenue", revenue);

            return ResponseEntity.ok(ApiResponse.success("Stats retrieved", stats));
        } catch (Exception e) {
            log.error("Error retrieving dashboard stats: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error retrieving stats", e.getMessage()));
        }
    }

    // ==========================================
    // USERS ENDPOINTS
    // ==========================================

    /**
     * Lấy danh sách tất cả người dùng (Chỉ Admin)
     */
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(Pageable pageable) {
        try {
            Page<UserDTO> users = userService.getAllUsers(pageable);
            return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", users));
        } catch (Exception e) {
            log.error("Error retrieving users: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error retrieving users", e.getMessage()));
        }
    }

    /**
     * Lấy thông tin chi tiết một người dùng (Chỉ Admin)
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        try {
            UserDTO user = userService.getUserById(userId);
            return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", user));
        } catch (Exception e) {
            log.error("Error retrieving user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Error retrieving user", e.getMessage()));
        }
    }

    /**
     * Cập nhật thông tin người dùng (Chỉ Admin)
     */
    @PutMapping("/users/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Long userId, @RequestBody UserDTO userDTO) {
        try {
            UserDTO updatedUser = userService.updateUser(userId, userDTO);
            return ResponseEntity.ok(ApiResponse.success("User updated successfully", updatedUser));
        } catch (Exception e) {
            log.error("Error updating user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error updating user", e.getMessage()));
        }
    }

    /**
     * Gán role cho người dùng (Chỉ Admin)
     * 
     * @param userId   ID của người dùng
     * @param roleName Tên role (ADMIN, USER, MODERATOR, SELLER)
     */
    @PostMapping("/users/{userId}/assign-role")
    public ResponseEntity<?> assignRole(@PathVariable Long userId, @RequestParam String roleName) {
        try {
            userService.assignRole(userId, roleName);
            return ResponseEntity.ok(ApiResponse.success("Role assigned successfully", null));
        } catch (Exception e) {
            log.error("Error assigning role: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error assigning role", e.getMessage()));
        }
    }

    /**
     * Xóa role của người dùng (Chỉ Admin)
     */
    @PostMapping("/users/{userId}/remove-role")
    public ResponseEntity<?> removeRole(@PathVariable Long userId, @RequestParam String roleName) {
        try {
            userService.removeRole(userId, roleName);
            return ResponseEntity.ok(ApiResponse.success("Role removed successfully", null));
        } catch (Exception e) {
            log.error("Error removing role: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error removing role", e.getMessage()));
        }
    }

    /**
     * Vô hiệu hóa tài khoản người dùng (Chỉ Admin)
     */
    @PostMapping("/users/{userId}/deactivate")
    public ResponseEntity<?> deactivateUser(@PathVariable Long userId) {
        try {
            userService.deactivateUser(userId);
            return ResponseEntity.ok(ApiResponse.success("User deactivated successfully", null));
        } catch (Exception e) {
            log.error("Error deactivating user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error deactivating user", e.getMessage()));
        }
    }

    /**
     * Kích hoạt lại tài khoản người dùng (Chỉ Admin)
     */
    @PostMapping("/users/{userId}/activate")
    public ResponseEntity<?> activateUser(@PathVariable Long userId) {
        try {
            userService.activateUser(userId);
            return ResponseEntity.ok(ApiResponse.success("User activated successfully", null));
        } catch (Exception e) {
            log.error("Error activating user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error activating user", e.getMessage()));
        }
    }

    /**
     * Xóa người dùng (Chỉ Admin)
     */
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
        } catch (Exception e) {
            log.error("Error deleting user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error deleting user", e.getMessage()));
        }
    }

    // ==========================================
    // ORDERS ENDPOINTS
    // ==========================================

    /**
     * Lấy toàn bộ danh sách đơn hàng (Chỉ Admin)
     */
    @GetMapping("/orders")
    public ResponseEntity<?> getAllOrders(Pageable pageable) {
        try {
            Page<OrderDTO> orders = orderService.getAllOrders(pageable);
            return ResponseEntity.ok(ApiResponse.success("Orders retrieved successfully", orders));
        } catch (Exception e) {
            log.error("Error retrieving orders: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error retrieving orders", e.getMessage()));
        }
    }

    /**
     * Cập nhật trạng thái đơn hàng (Chỉ Admin)
     */
    @PutMapping("/orders/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long orderId, @RequestParam String status) {
        try {
            OrderDTO order = orderService.updateOrderStatus(orderId, status);
            return ResponseEntity.ok(ApiResponse.success("Order status updated successfully", order));
        } catch (Exception e) {
            log.error("Error updating order status: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error updating order status", e.getMessage()));
        }
    }

    // ==========================================
    // REVIEWS ENDPOINTS
    // ==========================================

    /**
     * Lấy toàn bộ đánh giá (Chỉ Admin)
     */
    @GetMapping("/reviews")
    public ResponseEntity<?> getAllReviews(Pageable pageable) {
        try {
            Page<ReviewDTO> reviews = reviewService.getAllReviews(pageable);
            return ResponseEntity.ok(ApiResponse.success("Reviews retrieved successfully", reviews));
        } catch (Exception e) {
            log.error("Error retrieving reviews: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error retrieving reviews", e.getMessage()));
        }
    }

    /**
     * Lấy tổng hợp review theo sách (Chỉ Admin)
     */
    @GetMapping("/reviews/summary")
    public ResponseEntity<?> getReviewSummaries(Pageable pageable) {
        try {
            Page<com.bookstore.dto.BookReviewSummaryDTO> summaries = reviewService.getReviewSummaries(pageable);
            return ResponseEntity.ok(ApiResponse.success("Review summaries retrieved successfully", summaries));
        } catch (Exception e) {
            log.error("Error retrieving review summaries: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error retrieving review summaries", e.getMessage()));
        }
    }

    /**
     * Xóa đánh giá (Chỉ Admin)
     */
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<?> deleteReviewByAdmin(@PathVariable Long reviewId) {
        try {
            reviewService.deleteReviewByAdmin(reviewId);
            return ResponseEntity.ok(ApiResponse.success("Review deleted successfully", null));
        } catch (Exception e) {
            log.error("Error deleting review: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error deleting review", e.getMessage()));
        }
    }
}
