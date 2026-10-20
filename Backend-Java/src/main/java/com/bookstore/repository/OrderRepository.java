package com.bookstore.repository;

import com.bookstore.entity.Order;
import com.bookstore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUser(User user);

    java.util.Optional<Order> findByOrderCode(String orderCode);

    /** Khoá dòng đơn hàng khi đổi trạng thái / thanh toán / huỷ. */
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("SELECT o FROM Order o WHERE o.id = :id")
    java.util.Optional<Order> findByIdForUpdate(@org.springframework.data.repository.query.Param("id") Long id);

    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("SELECT o FROM Order o WHERE o.orderCode = :code")
    java.util.Optional<Order> findByOrderCodeForUpdate(@org.springframework.data.repository.query.Param("code") String code);

    List<Order> findByUserOrderByCreatedAtDesc(User user);

    @org.springframework.data.jpa.repository.Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status = 'DELIVERED'")
    java.math.BigDecimal calculateTotalRevenue();
}
