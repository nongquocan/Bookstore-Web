package com.bookstore.repository;

import com.bookstore.entity.Cart;
import com.bookstore.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    List<Cart> findByUser(User user);

    /** Khoá giỏ hàng khi checkout để hai request đồng thời không tạo hai đơn từ cùng một giỏ. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Cart c WHERE c.user = :user ORDER BY c.book.id")
    List<Cart> findByUserForUpdate(@Param("user") User user);

    Optional<Cart> findByUserAndBookId(User user, String bookId);

    @Query("SELECT c FROM Cart c WHERE c.user = :user AND c.book.id = :bookId")
    Optional<Cart> findByUserAndBook(@Param("user") User user, @Param("bookId") String bookId);

    void deleteByUserAndBookId(User user, String bookId);
}
