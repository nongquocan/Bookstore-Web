package com.bookstore.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduler tự động kiểm tra và kết thúc flash sale hết hạn.
 * Chạy mỗi 1 phút để đảm bảo giá trở về bình thường đúng giờ.
 */
@Component
@Slf4j
public class FlashSaleScheduler {

    @Autowired
    private BookAdminService bookAdminService;

    /**
     * Chạy mỗi 60 giây — kiểm tra flash sale hết hạn và reset giá về bình thường.
     */
    @Scheduled(fixedDelay = 60_000)
    public void expireFlashSales() {
        try {
            int count = bookAdminService.expireFlashSales();
            if (count > 0) {
                log.info("[FlashSaleScheduler] Đã kết thúc {} flash sale hết hạn, giá đã về bình thường.", count);
            }
        } catch (Exception e) {
            log.error("[FlashSaleScheduler] Lỗi khi kiểm tra flash sale: {}", e.getMessage(), e);
        }
    }
}
