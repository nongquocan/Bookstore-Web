package com.bookstore.controller;

import com.bookstore.dto.SepayWebhookRequest;
import com.bookstore.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/payment")
public class SepayWebhookController {

    private static final Logger log = LoggerFactory.getLogger(SepayWebhookController.class);

    @Autowired
    private OrderService orderService;

    @org.springframework.beans.factory.annotation.Value("${sepay.webhook-token:}")
    private String sepayWebhookToken;

    @PostMapping("/sepay-webhook")
    public ResponseEntity<?> handleSepayWebhook(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestBody SepayWebhookRequest payload) {
        
        log.info("Received SePay Webhook: {}", payload);

        // Xác thực API Token từ SePay
        if (sepayWebhookToken != null && !sepayWebhookToken.isBlank()) {
            if (authorizationHeader == null || !authorizationHeader.equals("Apikey " + sepayWebhookToken)) {
                log.warn("Unauthorized webhook request. Invalid or missing Authorization header.");
                return ResponseEntity.status(401).body(Map.of("success", false, "message", "Unauthorized"));
            }
        }

        // SePay gửi transferType = "in" cho giao dịch nhận tiền
        if ("in".equalsIgnoreCase(payload.getTransferType()) && payload.getTransferAmount() != null) {
            try {
                orderService.processSepayWebhook(payload.getContent(), payload.getTransferAmount());
                return ResponseEntity.ok(Map.of("success", true, "message", "Webhook processed successfully"));
            } catch (Exception e) {
                log.error("Error processing SePay webhook", e);
                // Vẫn trả về 200 để SePay không retry liên tục nếu lỗi nội bộ
                return ResponseEntity.ok(Map.of("success", false, "message", e.getMessage()));
            }
        }
        
        return ResponseEntity.ok(Map.of("success", true, "message", "Ignored (not an incoming transfer)"));
    }
}
