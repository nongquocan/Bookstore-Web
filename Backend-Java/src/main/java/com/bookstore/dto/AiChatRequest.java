package com.bookstore.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiChatRequest {

    /** Tin nhắn mới nhất của user */
    private String message;

    /** Lịch sử hội thoại (tối đa 20 lượt, để Gemini có context) */
    private List<ChatTurn> history;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatTurn {
        private String role;   // "user" hoặc "model"
        private String text;
    }
}
