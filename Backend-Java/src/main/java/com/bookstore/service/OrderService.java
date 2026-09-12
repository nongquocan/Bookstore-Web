package com.bookstore.service;

import com.bookstore.dto.Createorderrequest;
import com.bookstore.dto.OrderDTO;
import com.bookstore.dto.OrderItemDTO;
import com.bookstore.entity.*;
import com.bookstore.repository.CartRepository;
import com.bookstore.repository.OrderItemRepository;
import com.bookstore.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private VoucherService voucherService;

    // ─── Lấy danh sách đơn hàng của user ─────────────────────────────────────
    public List<OrderDTO> getUserOrders(User user) {
        List<Order> orders = orderRepository.findByUserOrderByCreatedAtDesc(user);
        return orders.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // ─── Admin lấy toàn bộ danh sách đơn hàng có phân trang ──────────────────
    public Page<OrderDTO> getAllOrders(Pageable pageable) {
        Page<Order> orders = orderRepository.findAll(pageable);
        return orders.map(this::convertToDTO);
    }

    // ─── Chi tiết một đơn hàng (kiểm tra quyền sở hữu) ───────────────────────
    public OrderDTO getOrderDetail(User user, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng: " + orderId));

        // User chỉ được xem đơn của mình
        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Bạn không có quyền xem đơn hàng này");
        }
        return convertToDTO(order);
    }

    // ─── Tạo đơn hàng từ giỏ hàng ────────────────────────────────────────────
    // Giữ method gốc để không ảnh hưởng code cũ
    @Transactional
    public OrderDTO createOrderFromCart(User user) {
        return createOrderFromCart(user, new Createorderrequest(), "PENDING");
    }

    // ─── Tạo đơn hàng từ giỏ hàng (có request + trạng thái thanh toán) ───────
    @Transactional
    public OrderDTO createOrderFromCart(User user, Createorderrequest request, String initialStatus) {
        List<Cart> cartItems = cartRepository.findByUser(user);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống");
        }

        BigDecimal shippingFee = calculateShippingFee(request.getShippingMethod());

        Order order = new Order();
        order.setUser(user);
        order.setStatus(initialStatus);
        order.setPaymentMethod(request.getPaymentMethod());
        order.setShippingMethod(request.getShippingMethod());
        order.setShippingFee(shippingFee);

        // Gán thông tin người nhận và địa chỉ từ request
        order.setRecipientName(request.getRecipientName());
        order.setRecipientPhone(request.getRecipientPhone());
        order.setRecipientEmail(request.getRecipientEmail());
        order.setShippingAddress(request.getShippingAddress());
        order.setStreet(request.getStreet());
        order.setDistrict(request.getDistrict());
        order.setCity(request.getCity());
        order.setUnitNumber(request.getUnitNumber());
        order.setDeliveryNote(request.getDeliveryNote());

        order.setTotalAmount(BigDecimal.ZERO);
        Order savedOrder = orderRepository.save(order);

        List<OrderItem> itemsList = new ArrayList<>(); // Danh sách tạm để gán ngược lại
        BigDecimal subtotal = BigDecimal.ZERO;

        for (Cart cart : cartItems) {
            OrderItem item = new OrderItem();
            item.setOrder(savedOrder);
            item.setBook(cart.getBook());
            item.setQuantity(cart.getQuantity());

            BigDecimal price = BigDecimal.valueOf(cart.getBook().getListPrice());
            item.setPrice(price);
            item.setSubtotal(price.multiply(BigDecimal.valueOf(cart.getQuantity())));

            orderItemRepository.save(item);
            itemsList.add(item); // Thêm vào danh sách items
            subtotal = subtotal.add(item.getSubtotal());
        }

        // Cập nhật lại đối tượng savedOrder đầy đủ thông tin
        savedOrder.setOrderItems(itemsList);
        
        // Tính tổng tiền sau khi cộng phí ship
        BigDecimal finalTotal = subtotal.add(shippingFee);
        
        // Áp dụng mã giảm giá nếu có
        if (request.getVoucherCode() != null && !request.getVoucherCode().isBlank()) {
            try {
                Voucher voucher = voucherService.validateVoucher(request.getVoucherCode(), finalTotal.doubleValue());
                BigDecimal discount = BigDecimal.ZERO;
                if (voucher.getDiscountPercent() > 0) {
                    discount = finalTotal.multiply(BigDecimal.valueOf(voucher.getDiscountPercent())).divide(BigDecimal.valueOf(100));
                }
                if (voucher.getMaxDiscountAmount() != null) {
                    BigDecimal maxDiscount = BigDecimal.valueOf(voucher.getMaxDiscountAmount());
                    if (discount.compareTo(maxDiscount) > 0) {
                        discount = maxDiscount;
                    }
                }
                finalTotal = finalTotal.subtract(discount);
                if (finalTotal.compareTo(BigDecimal.ZERO) < 0) {
                    finalTotal = BigDecimal.ZERO;
                }
                savedOrder.setDiscountAmount(discount);
                savedOrder.setVoucherCode(voucher.getCode());
                
                // Tăng số lượt sử dụng voucher
                voucherService.incrementUsedCount(voucher.getCode());
            } catch (Exception e) {
                System.out.println("Lỗi áp dụng voucher: " + e.getMessage());
            }
        }

        savedOrder.setTotalAmount(finalTotal);
        orderRepository.save(savedOrder);

        cartRepository.deleteAll(cartItems);

        return convertToDTO(savedOrder);
    }

    // ─── Cập nhật trạng thái đơn hàng (admin) ────────────────────────────────
    public OrderDTO updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng: " + orderId));
        order.setStatus(status);
        orderRepository.save(order);
        return convertToDTO(order);
    }

    // ─── Huỷ đơn hàng (user, chỉ khi PENDING hoặc PENDING_PAYMENT) ───────────
    @Transactional
    public OrderDTO cancelOrder(User user, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng: " + orderId));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Bạn không có quyền huỷ đơn hàng này");
        }

        if (!List.of("PENDING", "PENDING_PAYMENT").contains(order.getStatus())) {
            throw new RuntimeException("Không thể huỷ đơn hàng ở trạng thái: " + order.getStatus());
        }

        order.setStatus("CANCELLED");
        order.setPaymentStatus("CANCELLED");
        orderRepository.save(order);
        return convertToDTO(order);
    }

    // =========================================================================
    // 3 METHOD MỚI — dùng bởi PaymentCallbackController
    // =========================================================================

    /**
     * Cập nhật trạng thái thanh toán sau khi nhận callback từ VNPay / MoMo.
     * - PAID → đơn hàng chuyển sang CONFIRMED
     * - PAYMENT_FAILED → đơn hàng chuyển sang CANCELLED
     */
    @Transactional
    public void updatePaymentStatus(Long orderId, String paymentStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng: " + orderId));

        order.setPaymentStatus(paymentStatus);

        switch (paymentStatus) {
            case "PAID" -> order.setStatus("CONFIRMED");
            case "PAYMENT_FAILED" -> order.setStatus("CANCELLED");
            // Các trạng thái khác (REFUNDED, v.v.) không đổi status đơn hàng
        }

        orderRepository.save(order);
    }

    /**
     * Xử lý webhook SePay
     */
    @Transactional
    public void processSepayWebhook(String content, BigDecimal transferAmount) {
        if (content == null || content.isBlank())
            return;

        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("BS-?(\\d{8})-?([A-Z0-9]{6})",
                java.util.regex.Pattern.CASE_INSENSITIVE);
        java.util.regex.Matcher matcher = pattern.matcher(content);

        if (matcher.find()) {
            String datePart = matcher.group(1);
            String randomPart = matcher.group(2).toUpperCase();
            String orderCode = "BS-" + datePart + "-" + randomPart;

            orderRepository.findByOrderCode(orderCode).ifPresent(order -> {
                if (!"PAID".equals(order.getPaymentStatus())) {
                    // ✅ Kiểm tra sai khác <= 1000 VNĐ
                    BigDecimal difference = transferAmount.subtract(order.getTotalAmount()).abs();
                    BigDecimal tolerance = BigDecimal.valueOf(1000);

                    if (difference.compareTo(tolerance) <= 0) {
                        order.setPaymentStatus("PAID");
                        order.setStatus("CONFIRMED");
                        orderRepository.save(order);
                    } else {
                        System.out.println("⚠️ Amount mismatch - Expected: " +
                                order.getTotalAmount() + ", Received: " + transferAmount);
                    }
                }
            });
        }
    }

    /**
     * Kiểm tra đơn hàng có tồn tại không.
     * Dùng trong VNPay IPN để trả về mã lỗi 01 nếu không tìm thấy.
     */
    public boolean existsOrder(Long orderId) {
        return orderRepository.existsById(orderId);
    }

    /**
     * Kiểm tra đơn hàng đã được thanh toán chưa
     */
    public boolean isOrderAlreadyPaid(Long orderId) {
        return orderRepository.findById(orderId)
                .map(order -> "PAID".equals(order.getPaymentStatus()))
                .orElse(false);
    }

    /**
     * Lấy số tiền đơn hàng để đối chiếu với số tiền VNPay gửi về.
     * Giúp phát hiện gian lận (sửa số tiền trước khi gửi lên VNPay).
     */
    public long getOrderAmount(Long orderId) {
        return orderRepository.findById(orderId)
                .map(order -> order.getTotalAmount().longValue())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng: " + orderId));
    }

    // =========================================================================
    // HELPER
    // =========================================================================

    private BigDecimal calculateShippingFee(String shippingMethod) {
        if (shippingMethod == null)
            return BigDecimal.valueOf(30_000);
        return switch (shippingMethod.toLowerCase()) {
            case "express" -> BigDecimal.valueOf(60_000);
            case "sameday" -> BigDecimal.valueOf(120_000);
            default -> BigDecimal.valueOf(30_000); // standard
        };
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderCode(order.getOrderCode());
        dto.setStatus(order.getStatus());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setShippingMethod(order.getShippingMethod());
        dto.setShippingFee(order.getShippingFee());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setDiscountAmount(order.getDiscountAmount());
        dto.setVoucherCode(order.getVoucherCode());
        dto.setRecipientName(order.getRecipientName());
        dto.setRecipientPhone(order.getRecipientPhone());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setDeliveryNote(order.getDeliveryNote());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        dto.setItems(order.getOrderItems().stream()
                .map(this::convertItemToDTO)
                .collect(Collectors.toList()));
        return dto;
    }

    private OrderItemDTO convertItemToDTO(OrderItem item) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(item.getId());
        dto.setBookId(item.getBook().getId());
        dto.setBookTitle(item.getBook().getTitle());
        dto.setBookThumbnail(item.getBook().getThumbnail());
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getPrice());
        dto.setSubtotal(item.getSubtotal());
        return dto;
    }
}