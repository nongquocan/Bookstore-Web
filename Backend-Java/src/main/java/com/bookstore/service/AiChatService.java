package com.bookstore.service;

import com.bookstore.dto.AiChatRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bookstore.repository.BookRepository;
import com.bookstore.entity.Book;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class AiChatService {

    // Cache trên RAM để lưu các câu trả lời thường gặp (đặc biệt là các Quick
    // Actions)
    private final Map<String, String> responseCache = new ConcurrentHashMap<>();

    @Autowired
    private BookRepository bookRepository;

    private String booksContextCache = null;
    private long lastCacheTime = 0;

    private synchronized String getBooksContext() {
        if (booksContextCache != null && (System.currentTimeMillis() - lastCacheTime < 3600000)) {
            return booksContextCache;
        }
        try {
            List<Book> books = bookRepository.findAll();
            StringBuilder sb = new StringBuilder(
                    "\n\nDanh sách sách hiện có trong kho (dùng để trả lời nếu khách hỏi sách nào có sẵn):\n");
            int count = 0;
            for (Book b : books) {
                if (count++ > 150)
                    break; // Limit to 200 books to save prompt tokens
                sb.append("- ").append(b.getTitle())
                        .append(" (Tác giả: ").append(b.getAuthors() != null ? b.getAuthors() : "Đang cập nhật")
                        .append(", Thể loại: ").append(b.getSearchCategory() != null ? b.getSearchCategory() : "")
                        .append(")\n");
            }
            booksContextCache = sb.toString();
            lastCacheTime = System.currentTimeMillis();
            return booksContextCache;
        } catch (Exception e) {
            log.error("Failed to load books context", e);
            return "";
        }
    }

    private static final String GEMINI_BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=";

    private static final String SYSTEM_PROMPT = "Bạn là trợ lý AI thông minh của BookStore — cửa hàng sách trực tuyến hàng đầu Việt Nam.\n"
            +
            "Nhiệm vụ của bạn:\n" +
            "1. Tư vấn sách phù hợp với nhu cầu của khách hàng\n" +
            "2. Tóm tắt nội dung sách khi được hỏi\n" +
            "3. Gợi ý sách theo thể loại, sở thích, hoặc mục đích đọc\n" +
            "4. Giải thích lợi ích của từng cuốn sách\n\n" +
            "Các thể loại sách tại BookStore:\n" +
            "- Kinh doanh & Tài chính (Quản lý, Khởi nghiệp, Đầu tư)\n" +
            "- Lịch sử & Văn hóa\n" +
            "- Tự lực & Phát triển (Kỹ năng sống, Tâm lý học)\n" +
            "- Giáo dục & Tham khảo\n" +
            "- Công nghệ & IT (Lập trình, AI/ML)\n" +
            "- Văn học (Tiểu thuyết, Thơ ca, Truyện ngắn)\n\n" +
            "Luôn trả lời bằng tiếng Việt, thân thiện, ngắn gọn và hữu ích.\n" +
            "Nếu được hỏi về sách cụ thể, hãy tóm tắt nội dung, điểm nổi bật và đối tượng độc giả phù hợp.\n" +
            "Kết thúc mỗi câu trả lời bằng một gợi ý hành động (ví dụ: tìm kiếm sách, xem thể loại...).";

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Gửi tin nhắn đến Gemini API và trả về phản hồi.
     *
     * @param request Chứa message mới và lịch sử hội thoại
     * @return Nội dung phản hồi từ AI
     */
    public String chat(AiChatRequest request) throws Exception {
        String userMsg = request.getMessage().trim();
        List<AiChatRequest.ChatTurn> history = request.getHistory();

        // 1. TỐI ƯU 1: CACHE CÁC CÂU HỎI THƯỜNG GẶP (Tiết kiệm API)
        // Nếu đây là câu đầu tiên (history rỗng) thì kiểm tra Cache
        boolean isFirstQuestion = (history == null || history.isEmpty());
        String cacheKey = userMsg.toLowerCase();

        if (isFirstQuestion && responseCache.containsKey(cacheKey)) {
            log.debug("[AiChat] Tiết kiệm API: Trả về kết quả từ Cache cho: {}", cacheKey);
            return responseCache.get(cacheKey);
        }

        String url = GEMINI_BASE_URL + geminiApiKey;

        // Xây dựng body JSON theo Gemini API format
        ObjectNode body = objectMapper.createObjectNode();

        // System instruction
        ObjectNode systemInstruction = objectMapper.createObjectNode();
        ArrayNode sysParts = objectMapper.createArrayNode();
        ObjectNode sysPart = objectMapper.createObjectNode();
        String currentSystemPrompt = SYSTEM_PROMPT + getBooksContext();
        sysPart.put("text", currentSystemPrompt);
        sysParts.add(sysPart);
        systemInstruction.set("parts", sysParts);
        body.set("systemInstruction", systemInstruction);

        // Contents (lịch sử + tin mới)
        ArrayNode contents = objectMapper.createArrayNode();

        // Thêm lịch sử hội thoại
        if (history != null) {
            for (AiChatRequest.ChatTurn turn : history) {
                ObjectNode contentNode = objectMapper.createObjectNode();
                contentNode.put("role", turn.getRole());
                ArrayNode parts = objectMapper.createArrayNode();
                ObjectNode part = objectMapper.createObjectNode();
                part.put("text", turn.getText());
                parts.add(part);
                contentNode.set("parts", parts);
                contents.add(contentNode);
            }
        }

        // Thêm tin nhắn mới nhất của user
        ObjectNode userContent = objectMapper.createObjectNode();
        userContent.put("role", "user");
        ArrayNode userParts = objectMapper.createArrayNode();
        ObjectNode userPart = objectMapper.createObjectNode();
        userPart.put("text", request.getMessage());
        userParts.add(userPart);
        userContent.set("parts", userParts);
        contents.add(userContent);

        body.set("contents", contents);

        // Generation config
        ObjectNode genConfig = objectMapper.createObjectNode();
        genConfig.put("temperature", 0.8);
        genConfig.put("topP", 0.9);
        genConfig.put("maxOutputTokens", 1024);
        body.set("generationConfig", genConfig);

        String requestBody = objectMapper.writeValueAsString(body);
        log.debug("[AiChat] Sending request to Gemini, message length: {}", request.getMessage().length());

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .timeout(Duration.ofSeconds(30))
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            log.error("[AiChat] Gemini API error {}: {}", response.statusCode(), response.body());
            throw new RuntimeException("Gemini API error: HTTP " + response.statusCode());
        }

        JsonNode responseJson = objectMapper.readTree(response.body());
        String reply = responseJson
                .path("candidates").get(0)
                .path("content")
                .path("parts").get(0)
                .path("text").asText("Xin lỗi, tôi không thể trả lời ngay lúc này.");

        // Lưu vào Cache nếu đây là câu hỏi đầu tiên
        if (isFirstQuestion && reply != null && reply.length() > 20) {
            // Giữ cho cache không quá lớn (tối đa 1000 câu)
            if (responseCache.size() > 1000)
                responseCache.clear();
            responseCache.put(cacheKey, reply);
        }

        log.debug("[AiChat] Gemini replied with {} chars", reply.length());
        return reply;
    }
}
