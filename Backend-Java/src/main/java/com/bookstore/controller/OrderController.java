package com.bookstore.controller;

import com.bookstore.dto.ApiResponse;
import com.bookstore.dto.Createorderrequest;
import com.bookstore.dto.OrderDTO;
import com.bookstore.entity.User;
import com.bookstore.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired private OrderService orderService;
  

    // Lấy danh sách đơn hàng
    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderDTO>>> getUserOrders(
            @AuthenticationPrincipal User user) {

        List<OrderDTO> orders = orderService.getUserOrders(user);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách đơn hàng thành công", orders));
    }

    //  Chi tiết một đơn hàng
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderDTO>> getOrderDetail(
            @AuthenticationPrincipal User user,
            @PathVariable Long orderId) {

        OrderDTO order = orderService.getOrderDetail(user, orderId);
        return ResponseEntity.ok(ApiResponse.success("Lấy chi tiết đơn hàng thành công", order));
    }

    // Tạo đơn hàng
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<?>> createOrder(
            @AuthenticationPrincipal User user,
            @RequestBody Createorderrequest request,
            HttpServletRequest httpRequest) {


        String paymentMethod = request.getPaymentMethod();
        if (paymentMethod == null || paymentMethod.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Vui lòng chọn phương thức thanh toán"));
        }

        return switch (paymentMethod.toLowerCase()) {


            case "cod" -> {
                OrderDTO order = orderService.createOrderFromCart(user, request, "PENDING");
                yield ResponseEntity.ok(ApiResponse.success(
                        "Đặt hàng thành công",
                        Map.of(
                                "orderId",       order.getId(),
                                "orderCode",     order.getOrderCode(),
                                "totalAmount",   order.getTotalAmount(),
                                "paymentMethod", "cod",
                                "status",        order.getStatus()
                        )
                ));
            }

            // ── SePay ──
            case "sepay" -> {
                OrderDTO order = orderService.createOrderFromCart(user, request, "PENDING_PAYMENT");
                yield ResponseEntity.ok(ApiResponse.success(
                        "Đơn hàng đã tạo, vui lòng thanh toán qua SePay",
                        Map.of(
                                "orderId",      order.getId(),
                                "orderCode",    order.getOrderCode(),
                                "totalAmount",  order.getTotalAmount(),
                                "paymentMethod","sepay"
                        )
                ));
            }

            default -> ResponseEntity.badRequest()
                    .body(ApiResponse.error("Phương thức thanh toán không hợp lệ: " + paymentMethod));
        };
    }

    // ─── Admin cập nhật trạng thái đơn hàng ─────────────────────────────
    @PutMapping("/{orderId}/status")
    public ResponseEntity<ApiResponse<OrderDTO>> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String status) {

        OrderDTO order = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật trạng thái thành công", order));
    }

    // ───  Huỷ đơn hàng  ─────────────────────────
    @DeleteMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse<OrderDTO>> cancelOrder(
            @AuthenticationPrincipal User user,
            @PathVariable Long orderId) {

        OrderDTO order = orderService.cancelOrder(user, orderId);
        return ResponseEntity.ok(ApiResponse.success("Huỷ đơn hàng thành công", order));
    }

    // ─── Helper: Lấy IP thực của client (qua proxy/nginx) ────────────────────
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isBlank() && !"unknown".equalsIgnoreCase(ip)) {
            return ip.split(",")[0].trim();
        }
        ip = request.getHeader("X-Real-IP");
        if (ip != null && !ip.isBlank()) return ip;
        return request.getRemoteAddr();
    }
}