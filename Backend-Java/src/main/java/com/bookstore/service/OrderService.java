package com.bookstore.service;

import com.bookstore.dto.Createorderrequest;
import com.bookstore.dto.OrderDTO;
import com.bookstore.dto.OrderItemDTO;
import com.bookstore.entity.*;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.CartRepository;
import com.bookstore.repository.OrderItemRepository;
import com.bookstore.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private static final Set<String> VALID_STATUSES = Set.of(
            "PENDING", "PENDING_PAYMENT", "CONFIRMED", "SHIPPING", "SHIPPED", "DELIVERED", "CANCELLED");

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private BookRepository bookRepository;
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
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng: " + orderId));

        // User chỉ được xem đơn của mình
        if (!order.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Bạn không có quyền xem đơn hàng này");
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
        // Khoá giỏ hàng (sắp theo book_id để tránh deadlock): request đồng thời thứ hai sẽ thấy giỏ trống
        List<Cart> cartItems = new ArrayList<>(cartRepository.findByUserForUpdate(user));
        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Giỏ hàng trống");
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

        List<OrderItem> itemsList = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (Cart cart : cartItems) {
            Book book = cart.getBook();
            int qty = cart.getQuantity();

            // Trừ kho nguyên tử: thất bại nếu không đủ hàng -> rollback toàn bộ đơn
            if (bookRepository.decreaseStock(book.getId(), qty) == 0) {
                throw new IllegalArgumentException("Sách \"" + book.getTitle() + "\" không đủ tồn kho");
            }

            // Chốt giá tại thời điểm đặt (có tính flash sale)
            BigDecimal price = toMoney(book.resolveEffectivePrice());

            OrderItem item = new OrderItem();
            item.setOrder(savedOrder);
            item.setBook(book);
            item.setQuantity(qty);
            item.setPrice(price);
            item.setSubtotal(price.multiply(BigDecimal.valueOf(qty)));

            orderItemRepository.save(item);
            itemsList.add(item);
            subtotal = subtotal.add(item.getSubtotal());
        }

        savedOrder.setOrderItems(itemsList);

        // Voucher giảm trên tiền hàng (không tính phí ship); lỗi voucher => đặt hàng thất bại, rollback
        BigDecimal discount = BigDecimal.ZERO;
        if (request.getVoucherCode() != null && !request.getVoucherCode().isBlank()) {
            Voucher voucher = voucherService.redeemVoucher(request.getVoucherCode(), subtotal.doubleValue());
            if (voucher.getDiscountPercent() > 0) {
                discount = subtotal.multiply(BigDecimal.valueOf(voucher.getDiscountPercent()))
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            }
            if (voucher.getMaxDiscountAmount() != null) {
                BigDecimal maxDiscount = BigDecimal.valueOf(voucher.getMaxDiscountAmount());
                if (discount.compareTo(maxDiscount) > 0) {
                    discount = maxDiscount;
                }
            }
            if (discount.compareTo(subtotal) > 0) {
                discount = subtotal;
            }
            savedOrder.setVoucherCode(voucher.getCode());
        }
        savedOrder.setDiscountAmount(discount);
        savedOrder.setTotalAmount(subtotal.subtract(discount).add(shippingFee));
        orderRepository.save(savedOrder);

        cartRepository.deleteAll(cartItems);

        return convertToDTO(savedOrder);
    }

    // ─── Cập nhật trạng thái đơn hàng (admin) ────────────────────────────────
    @Transactional
    public OrderDTO updateOrderStatus(Long orderId, String status) {
        if (status == null || !VALID_STATUSES.contains(status)) {
            throw new IllegalArgumentException("Trạng thái không hợp lệ: " + status);
        }
        Order order = orderRepository.findByIdForUpdate(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng: " + orderId));

        String current = order.getStatus();
        if (current.equals(status)) {
            return convertToDTO(order);
        }
        if ("CANCELLED".equals(current)) {
            throw new IllegalArgumentException("Đơn hàng đã huỷ, không thể đổi trạng thái");
        }
        if ("DELIVERED".equals(current)) {
            throw new IllegalArgumentException("Đơn hàng đã giao, không thể đổi trạng thái");
        }

        if ("CANCELLED".equals(status)) {
            cancelAndRestore(order);
        } else {
            order.setStatus(status);
        }
        orderRepository.save(order);
        return convertToDTO(order);
    }

    // ─── Huỷ đơn hàng (user, chỉ khi PENDING hoặc PENDING_PAYMENT) ───────────
    @Transactional
    public OrderDTO cancelOrder(User user, Long orderId) {
        Order order = orderRepository.findByIdForUpdate(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng: " + orderId));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Bạn không có quyền huỷ đơn hàng này");
        }

        if (!List.of("PENDING", "PENDING_PAYMENT").contains(order.getStatus())) {
            throw new IllegalArgumentException("Không thể huỷ đơn hàng ở trạng thái: " + order.getStatus());
        }

        cancelAndRestore(order);
        order.setPaymentStatus("CANCELLED");
        orderRepository.save(order);
        return convertToDTO(order);
    }

    // =========================================================================
    // THANH TOÁN
    // =========================================================================

    /**
     * Cập nhật trạng thái thanh toán sau khi nhận callback từ cổng thanh toán.
     * - PAID → đơn hàng chuyển sang CONFIRMED
     * - PAYMENT_FAILED → đơn hàng chuyển sang CANCELLED (hoàn kho + voucher)
     */
    @Transactional
    public void updatePaymentStatus(Long orderId, String paymentStatus) {
        Order order = orderRepository.findByIdForUpdate(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng: " + orderId));

        // Đơn đã huỷ thì không cho "sống lại"; callback lặp thì bỏ qua
        if ("CANCELLED".equals(order.getStatus()) || paymentStatus.equals(order.getPaymentStatus())) {
            return;
        }

        order.setPaymentStatus(paymentStatus);

        switch (paymentStatus) {
            case "PAID" -> order.setStatus("CONFIRMED");
            case "PAYMENT_FAILED" -> cancelAndRestore(order);
            // Các trạng thái khác (REFUNDED, v.v.) không đổi status đơn hàng
        }

        orderRepository.save(order);
    }

    /**
     * Xử lý webhook SePay (idempotent: gọi lặp hoặc đồng thời chỉ xác nhận một lần).
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

            orderRepository.findByOrderCodeForUpdate(orderCode).ifPresent(order -> {
                if ("PAID".equals(order.getPaymentStatus())) {
                    return;
                }
                if ("CANCELLED".equals(order.getStatus())) {
                    // Tiền về sau khi đơn đã huỷ: không kích hoạt lại, cần hoàn tiền thủ công
                    log.warn("Nhận tiền cho đơn đã huỷ {} (số tiền {}), cần hoàn tiền thủ công",
                            orderCode, transferAmount);
                    return;
                }
                // Kiểm tra sai khác <= 1000 VNĐ
                BigDecimal difference = transferAmount.subtract(order.getTotalAmount()).abs();
                if (difference.compareTo(BigDecimal.valueOf(1000)) <= 0) {
                    order.setPaymentStatus("PAID");
                    order.setStatus("CONFIRMED");
                    orderRepository.save(order);
                } else {
                    log.warn("Amount mismatch - Expected: {}, Received: {}", order.getTotalAmount(), transferAmount);
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
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng: " + orderId));
    }

    // =========================================================================
    // HELPER
    // =========================================================================

    /** Huỷ đơn: đổi trạng thái, hoàn kho và hoàn lượt voucher. Gọi khi đã khoá dòng đơn. */
    private void cancelAndRestore(Order order) {
        order.setStatus("CANCELLED");
        List<OrderItem> items = new ArrayList<>(order.getOrderItems());
        items.sort(Comparator.comparing(i -> i.getBook().getId()));
        for (OrderItem item : items) {
            bookRepository.increaseStock(item.getBook().getId(), item.getQuantity());
        }
        voucherService.releaseVoucher(order.getVoucherCode());
    }

    private static BigDecimal toMoney(Float value) {
        if (value == null) {
            throw new IllegalArgumentException("Sách chưa có giá bán");
        }
        return new BigDecimal(Float.toString(value)).setScale(2, RoundingMode.HALF_UP);
    }

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
