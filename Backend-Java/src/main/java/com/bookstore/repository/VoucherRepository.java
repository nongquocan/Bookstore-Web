package com.bookstore.repository;

import com.bookstore.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    Optional<Voucher> findByCode(String code);

    /** Tăng lượt dùng nguyên tử: trả 0 nếu voucher đã hết lượt. */
    @Modifying(flushAutomatically = true)
    @Query("UPDATE Voucher v SET v.usedCount = v.usedCount + 1 " +
            "WHERE v.code = :code AND (v.usageLimit <= 0 OR v.usedCount < v.usageLimit)")
    int incrementUsedCountIfAvailable(@Param("code") String code);

    /** Hoàn lượt dùng khi đơn bị huỷ. */
    @Modifying(flushAutomatically = true)
    @Query("UPDATE Voucher v SET v.usedCount = v.usedCount - 1 WHERE v.code = :code AND v.usedCount > 0")
    int decrementUsedCount(@Param("code") String code);
}
