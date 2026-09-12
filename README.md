# Bookstore Web

Website bán sách trực tuyến gồm hai phần tách biệt: backend REST API bằng Spring Boot và frontend tĩnh bằng HTML/CSS/JavaScript thuần.

## Công nghệ sử dụng

**Backend** (`Backend-Java/`)
- Java 21, Spring Boot 3.2.3
- Spring Web, Spring Data JPA, MySQL
- Spring Security + JWT (đăng nhập/phân quyền)
- Spring Mail (gửi OTP xác thực qua email)
- Lombok, Gson, Apache HttpClient5

**Frontend** (`Frontend/`)
- HTML, CSS, JavaScript thuần (không dùng framework)
- Gọi API backend qua `fetch`

## Cấu trúc thư mục

```
Web-site/
├── Backend-Java/           # Spring Boot REST API
│   └── src/main/java/com/bookstore/
│       ├── controller/     # REST endpoints
│       ├── service/        # Business logic
│       ├── repository/     # JPA repositories
│       ├── entity/         # JPA entities
│       ├── dto/            # Request/response objects
│       ├── security/       # JWT filter & provider
│       └── config/         # Security config, khởi tạo dữ liệu
└── Frontend/
    ├── admin/               # Trang quản trị
    ├── pages/               # Các trang người dùng
    └── assets/              # CSS, JS, hình ảnh
```

## Tính năng chính

- Danh mục & tìm kiếm sách, sách nổi bật, sách mới, flash sale
- Đăng ký/đăng nhập bằng JWT, xác thực email qua OTP
- Giỏ hàng, đặt hàng, tra cứu đơn hàng
- Đánh giá & xếp hạng sách
- Wishlist (danh sách yêu thích)
- Voucher giảm giá
- Thanh toán qua webhook SePay
- Chatbot AI hỗ trợ tư vấn (tích hợp Gemini AI)
- Trang quản trị: quản lý người dùng, đơn hàng, đánh giá, flash sale, thống kê dashboard

## Yêu cầu môi trường

- JDK 21
- Maven 3.9+
- MySQL 8

## Cài đặt & chạy backend

1. Tạo database MySQL tên `bookstore_db`.
2. Tạo file cấu hình secrets cho local:
   ```
   cd Backend-Java/src/main/resources
   copy application-local.properties.example application-local.properties
   ```
   Mở `application-local.properties` và điền giá trị thật: mật khẩu MySQL, JWT secret, mật khẩu Gmail (App Password), token webhook SePay, API key Gemini.
3. Chạy ứng dụng:
   ```
   cd Backend-Java
   mvn spring-boot:run
   ```
   Hoặc dùng script có sẵn ở thư mục gốc: `start-backend.bat` (Windows).

Backend mặc định chạy tại `http://localhost:8080`.

## Chạy frontend

Frontend là các file tĩnh, mở trực tiếp `Frontend/index.html` bằng trình duyệt hoặc phục vụ qua một static server (ví dụ Live Server của VS Code). Đảm bảo backend đang chạy để các trang gọi API thành công.

## Lưu ý bảo mật

Không commit `application-local.properties` (đã được gitignore) — file này chứa các secret thật dùng cho môi trường local.
