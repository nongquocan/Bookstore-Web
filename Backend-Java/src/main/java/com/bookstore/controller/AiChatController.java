package com.bookstore.controller;

import com.bookstore.dto.AiChatRequest;
import com.bookstore.dto.ApiResponse;
import com.bookstore.service.AiChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = {
        "http://localhost:3000",
        "http://localhost:5500",
        "http://localhost:8000",
        "http://localhost:63342",
        "http://127.0.0.1:5500"
})
@RequiredArgsConstructor
@Slf4j
public class AiChatController {

    private final AiChatService aiChatService;

    /**
     * POST /api/ai/chat
     * Body: { "message": "...", "history": [...] }
     *
     * Endpoint public — không yêu cầu đăng nhập
     * (API key Gemini được giữ an toàn ở phía backend)
     */
    @PostMapping("/chat")
    public ResponseEntity<ApiResponse<?>> chat(@RequestBody AiChatRequest request) {
        // Validate
        if (request.getMessage() == null || request.getMessage().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Tin nhắn không được để trống"));
        }
        if (request.getMessage().length() > 2000) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Tin nhắn quá dài (tối đa 2000 ký tự)"));
        }

        try {
            String reply = aiChatService.chat(request);
            return ResponseEntity.ok(ApiResponse.success(Map.of("reply", reply)));
        } catch (Exception e) {
            log.error("[AiChat] Error calling Gemini: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Không thể kết nối AI lúc này. Vui lòng thử lại sau!"));
        }
    }
}
