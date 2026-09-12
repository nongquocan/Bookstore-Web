package com.bookstore.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Image Proxy Controller với Disk Cache
 * - Lần đầu: fetch từ Google Books và lưu vào thư mục cache
 * - Lần sau: đọc thẳng từ disk → nhanh, không bị rate limit 429
 * Endpoint: GET /api/proxy/image?url=<encoded_image_url>
 */
@RestController
@RequestMapping("/api/proxy")
@Slf4j
public class ImageProxyController {

    private static final int  CONNECT_TIMEOUT_MS = 6000;
    private static final int  READ_TIMEOUT_MS    = 10000;
    private static final long CACHE_SECONDS      = 2592000L; // 30 ngày
    private static final int  MIN_IMAGE_SIZE     = 500;      // bytes
    // [BUG-8 FIX] Giới hạn kích thước ảnh tối đa để tránh OOM khi đọc vào RAM
    private static final int  MAX_IMAGE_SIZE     = 10 * 1024 * 1024; // 10 MB

    // Thư mục lưu cache ảnh trên disk
    private static final String CACHE_DIR =
        System.getProperty("user.home") + "/.bookstore-image-cache";

    // Chỉ cho phép proxy từ các domain tin cậy
    private static final String[] ALLOWED_HOSTS = {
        "books.google.com",
        "books.googleusercontent.com",
        "lh3.googleusercontent.com",
        "covers.openlibrary.org",
        "images-na.ssl-images-amazon.com",
        "m.media-amazon.com"
    };

    // [BUG-9 FIX] Ngăn race condition: nhiều request cùng URL đồng thời
    // cùng fetch & ghi cache → file bị ghi đè nhiều lần / tốn băng thông.
    // Dùng ConcurrentHashMap làm in-memory lock per URL.
    private final ConcurrentHashMap<String, Object> fetchLocks = new ConcurrentHashMap<>();

    @GetMapping("/image")
    public ResponseEntity<byte[]> proxyImage(@RequestParam("url") String encodedUrl) {

        // Kiểm tra null/blank
        if (encodedUrl == null || encodedUrl.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        // [BUG-10 FIX] Spring tự decode query param 1 lần khi nhận request.
        // Nếu frontend gọi: /api/proxy/image?url=http%3A%2F%2F...
        // Spring đã decode thành: url=http://...
        // Ta KHÔNG nên decode thêm lần nữa vì sẽ bị double-decode.
        // Chỉ decode khi URL vẫn còn chứa % sau khi Spring xử lý.
        String imageUrl = encodedUrl;
        if (encodedUrl.contains("%")) {
            try {
                imageUrl = URLDecoder.decode(encodedUrl, StandardCharsets.UTF_8);
            } catch (IllegalArgumentException e) {
                log.warn("Invalid URL encoding: {}", encodedUrl);
                return ResponseEntity.badRequest().build();
            }
        }

        // Kiểm tra domain được phép TRƯỚC khi xử lý
        if (!isAllowedHost(imageUrl)) {
            log.warn("Blocked proxy to disallowed host: {}", imageUrl);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Đảm bảo HTTPS (tính cache key SAU khi normalize)
        imageUrl = imageUrl.replace("http://", "https://");

        // [BUG-11 FIX] Normalize URL: loại bỏ fragment (#...) và trailing spaces
        // để tránh tạo cache key khác nhau cho cùng 1 ảnh
        imageUrl = imageUrl.trim().split("#")[0];

        String cacheKey  = md5(imageUrl);
        Path   cachePath = Path.of(CACHE_DIR, cacheKey + ".jpg");

        // Thử đọc từ cache disk
        if (Files.exists(cachePath)) {
            try {
                long size = Files.size(cachePath);
                if (size > MIN_IMAGE_SIZE) {
                    byte[] cached = Files.readAllBytes(cachePath);
                    log.debug("Cache HIT: {}", cacheKey);
                    return buildResponse(cached, size);
                } else {
                    log.warn("Corrupt cache file ({}b), re-fetching: {}", size, cacheKey);
                    Files.deleteIfExists(cachePath);
                }
            } catch (IOException e) {
                log.warn("Cannot read cache {}: {}", cachePath, e.getMessage());
            }
        }

        // [BUG-9] Dùng lock per-URL để tránh race condition
        Object lock = fetchLocks.computeIfAbsent(cacheKey, k -> new Object());
        synchronized (lock) {
            // Double-check: có thể thread khác đã fetch xong trong khi ta đang chờ lock
            if (Files.exists(cachePath)) {
                try {
                    long size = Files.size(cachePath);
                    if (size > MIN_IMAGE_SIZE) {
                        byte[] cached = Files.readAllBytes(cachePath);
                        return buildResponse(cached, size);
                    }
                } catch (IOException e) {
                    log.warn("Cache read after lock failed: {}", e.getMessage());
                }
            }

            return fetchAndCache(imageUrl, cachePath, cacheKey);
        }
    }

    private ResponseEntity<byte[]> fetchAndCache(String imageUrl, Path cachePath, String cacheKey) {
        HttpURLConnection conn = null;
        try {
            conn = openConnection(imageUrl);
            int status = conn.getResponseCode();

            if (status != 200) {
                log.debug("Remote returned {} for {}", status, imageUrl);
                int clientStatus = (status >= 400 && status < 500)
                    ? HttpStatus.NOT_FOUND.value()
                    : HttpStatus.BAD_GATEWAY.value();
                return ResponseEntity.status(clientStatus).build();
            }

            String contentType = conn.getContentType();
            if (contentType == null || !contentType.contains("image/")) {
                log.debug("Non-image content-type '{}' from: {}", contentType, imageUrl);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            // [BUG-8 FIX] Kiểm tra Content-Length trước khi đọc để tránh OOM
            long contentLength = conn.getContentLengthLong();
            if (contentLength > MAX_IMAGE_SIZE) {
                log.warn("Image too large ({}b), skipping: {}", contentLength, imageUrl);
                return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).build();
            }

            try (InputStream is = conn.getInputStream()) {
                // [BUG-8] Đọc có giới hạn thay vì readAllBytes() không giới hạn
                byte[] bytes = is.readNBytes(MAX_IMAGE_SIZE + 1);

                if (bytes.length > MAX_IMAGE_SIZE) {
                    log.warn("Image exceeded max size, discarding: {}", imageUrl);
                    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).build();
                }

                if (bytes.length <= MIN_IMAGE_SIZE) {
                    log.debug("Skipping tiny image ({}b): {}", bytes.length, imageUrl);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                }

                saveCache(cachePath, bytes);
                log.debug("Cache MISS → saved {}", cacheKey);
                return buildResponse(bytes, bytes.length);
            }

        } catch (IOException e) {
            log.warn("Failed to fetch {}: {}", imageUrl, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private HttpURLConnection openConnection(String imageUrl) throws IOException {
        // Dùng URI → URL constructor thay vì URI.toURL() (deprecated Java 20+)
        HttpURLConnection conn =
            (HttpURLConnection) new java.net.URL(URI.create(imageUrl).toString()).openConnection();
        conn.setConnectTimeout(CONNECT_TIMEOUT_MS);
        conn.setReadTimeout(READ_TIMEOUT_MS);
        conn.setRequestProperty("User-Agent",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
            + "(KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36");
        conn.setRequestProperty("Accept", "image/webp,image/apng,image/*,*/*;q=0.8");
        conn.setRequestProperty("Accept-Language", "en-US,en;q=0.9");
        conn.setRequestProperty("Referer", "https://books.google.com/");
        conn.setInstanceFollowRedirects(true);
        return conn;
    }

    // [BUG-13 FIX] buildResponse cũ luôn set Content-Type là IMAGE_JPEG
    // dù ảnh thực tế có thể là PNG/WEBP → browser render sai.
    // Thêm tham số để truyền Content-Length chính xác.
    private ResponseEntity<byte[]> buildResponse(byte[] bytes, long contentLength) {
        HttpHeaders headers = new HttpHeaders();
        // Detect loại ảnh từ magic bytes
        headers.setContentType(detectMediaType(bytes));
        headers.setContentLength(contentLength);
        headers.setCacheControl(
            CacheControl.maxAge(java.time.Duration.ofSeconds(CACHE_SECONDS)));
        headers.set("X-Content-Type-Options", "nosniff");
        headers.set("Access-Control-Allow-Origin", "*");
        return ResponseEntity.ok().headers(headers).body(bytes);
    }

    /** Detect media type từ magic bytes thay vì hardcode IMAGE_JPEG */
    private MediaType detectMediaType(byte[] bytes) {
        if (bytes.length >= 4) {
            // PNG: 89 50 4E 47
            if (bytes[0] == (byte)0x89 && bytes[1] == 0x50
                    && bytes[2] == 0x4E && bytes[3] == 0x47) {
                return MediaType.IMAGE_PNG;
            }
            // JPEG: FF D8 FF
            if (bytes[0] == (byte)0xFF && bytes[1] == (byte)0xD8
                    && bytes[2] == (byte)0xFF) {
                return MediaType.IMAGE_JPEG;
            }
            // GIF: 47 49 46
            if (bytes[0] == 0x47 && bytes[1] == 0x49 && bytes[2] == 0x46) {
                return MediaType.IMAGE_GIF;
            }
            // WEBP: 52 49 46 46 ... 57 45 42 50
            if (bytes[0] == 0x52 && bytes[1] == 0x49
                    && bytes[2] == 0x46 && bytes[3] == 0x46) {
                return MediaType.parseMediaType("image/webp");
            }
        }
        return MediaType.IMAGE_JPEG; // default fallback
    }

    private void saveCache(Path path, byte[] data) {
        try {
            Files.createDirectories(path.getParent());
            // [BUG-14 FIX] Ghi vào temp file trước, rồi atomic move để tránh
            // file bị corrupt nếu JVM crash giữa chừng khi đang ghi
            Path tmp = path.resolveSibling(path.getFileName() + ".tmp");
            Files.write(tmp, data,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            Files.move(tmp, path, StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            log.warn("Cannot write cache {}: {}", path, e.getMessage());
        }
    }

    private String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            return String.format("%08x", Math.abs(input.hashCode()));
        }
    }

    private boolean isAllowedHost(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) return false;
        try {
            String host = URI.create(imageUrl).getHost();
            if (host == null) return false;
            host = host.toLowerCase();
            for (String allowed : ALLOWED_HOSTS) {
                if (host.equals(allowed) || host.endsWith("." + allowed)) return true;
            }
        } catch (IllegalArgumentException ignored) { }
        return false;
    }
}
