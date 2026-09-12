# CHƯƠNG II. KIẾN THỨC ÁP DỤNG

## 2.1. PHÂN TÍCH VÀ THIẾT KẾ HỆ THỐNG

### 2.1.1 Biểu đồ Phân cấp Chức năng (BFD - Business Function Decomposition)

#### A. Khái niệm Biểu đồ Phân cấp Chức năng

Biểu đồ Phân cấp Chức năng (BFD) là công cụ phân tích hệ thống được sử dụng để mô tả chi tiết các chức năng của hệ thống thông tin từ cấp độ tổng quát (Level 0) đến cấp độ chi tiết (Level n). Trong ngữ cảnh phát triển hệ thống thương mại điện tử, BFD giúp:
- Phân rã yêu cầu kinh doanh thành các chức năng cụ thể
- Xác định ranh giới của từng module chức năng
- Hỗ trợ việc thiết kế kiến trúc ứng dụng
- Phân bổ công việc phát triển một cách logic

BFD tuân theo **nguyên tắc phân cấp từ trên xuống (Top-Down)**:
- **Level 0 (Context)**: Hệ thống được xem như một hộp đen duy nhất
- **Level 1 (Overview)**: Phân rã thành các chức năng chính
- **Level 2+**: Tiếp tục phân rã chi tiết các chức năng con

#### B. Cấu trúc BFD cho hệ thống BookStore

**Level 0 - Context**: Hệ thống Quản lý Bán sách Trực tuyến nhận đầu vào từ khách hàng (dữ liệu tìm kiếm, thanh toán) và admin (quản lý sản phẩm), xuất ra đơn hàng, báo cáo, và danh sách sách.

**Level 1 - Chức năng Chính**: Hệ thống BookStore được chia thành **5 chức năng chính**:

| Chức năng chính | Mô tả | Vai trò chính |
|---|---|---|
| **F1. Quản lý Người dùng** | Xác thực, đăng ký/đăng nhập, quản lý hồ sơ cá nhân | Bảo mật & nhận dạng |
| **F2. Quản lý Sản phẩm** | Tìm kiếm, lọc, xem chi tiết sách, quản lý danh mục | Hiển thị & quản lý catalog |
| **F3. Quản lý Thanh toán** | Giỏ hàng, áp dụng voucher, xử lý thanh toán | Giao dịch tài chính |
| **F4. Quản lý Đơn hàng** | Tạo đơn, theo dõi trạng thái, hủy/hoàn lại | Quản lý vòng đời đơn hàng |
| **F5. Quản lý Nội dung & Tương tác** | Đánh giá, bình luận, wishlist, chatbot AI | Tạo cộng đồng, gợi ý sản phẩm |

**Level 2 - Chi tiết các Chức năng**:

**F1. Quản lý Người dùng (User Management)**

| Chức năng chi tiết | Hoạt động cụ thể |
|---|---|
| **F1.1 Xác thực Người dùng** | Đăng ký tài khoản mới, xác minh email qua OTP, đăng nhập bằng email & mật khẩu, khôi phục mật khẩu quên |
| **F1.2 Quản lý Hồ sơ Cá nhân** | Xem & chỉnh sửa thông tin (tên, SĐT, địa chỉ), tải lên ảnh đại diện, quản lý địa chỉ giao hàng, thay đổi mật khẩu |
| **F1.3 Quản lý Phân quyền** | Phân quyền Admin/User, quản lý quyền truy cập tính năng admin, quản lý danh sách người dùng |
| **F1.4 Bảo mật Phiên đăng nhập** | Sinh JWT token khi đăng nhập, xác thực JWT trong mỗi request, kiểm tra quyền hạn dựa trên roles |

**F2. Quản lý Sản phẩm (Product Management)**

| Chức năng chi tiết | Hoạt động cụ thể |
|---|---|
| **F2.1 Tìm kiếm & Lọc Sách** | Tìm kiếm theo từ khóa (tiêu đề, tác giả), lọc theo danh mục/giá/đánh giá, sắp xếp, phân trang |
| **F2.2 Xem Chi tiết Sách** | Hiển thị thông tin sách (tên, tác giả, nhà xuất bản, giá, tồn kho), lấy bìa từ Google Books API, hiển thị mô tả & đánh giá |
| **F2.3 Quản lý Danh mục** | Xem danh sách danh mục, tạo/sửa/xóa danh mục (Admin), gán sách vào danh mục |
| **F2.4 Quản lý Banner & HomeSection** | Tạo/sửa/xóa banner quảng cáo, quản lý vị trí hiển thị trang chủ, quản lý sách nổi bật |
| **F2.5 Tích hợp Google Books API** | Tìm kiếm sách từ Google Books, lấy thông tin sách, lấy hình ảnh bìa, cập nhật giá |

**F3. Quản lý Thanh toán (Payment Management)**

| Chức năng chi tiết | Hoạt động cụ thể |
|---|---|
| **F3.1 Quản lý Giỏ hàng** | Thêm/xóa sách vào giỏ, thay đổi số lượng, tính tổng giá, lưu giỏ hàng tạm thời |
| **F3.2 Áp dụng Mã khuyến mãi** | Xác thực mã voucher, tính toán giảm giá, kiểm tra điều kiện sử dụng |
| **F3.3 Phương thức Thanh toán** | Hỗ trợ COD (Thanh toán khi nhận), thanh toán trực tuyến, webhook nhận thông báo thanh toán |
| **F3.4 Xử lý Đơn hàng Thanh toán** | Tạo hóa đơn/mã đơn hàng, tính tiền ship & phí, cập nhật trạng thái thanh toán, lưu lịch sử |

**F4. Quản lý Đơn hàng (Order Management)**

| Chức năng chi tiết | Hoạt động cụ thể |
|---|---|
| **F4.1 Tạo Đơn Hàng** | Xác nhận thông tin khách hàng, chọn địa chỉ giao hàng, kiểm tra tồn kho, tạo mã đơn hàng duy nhất |
| **F4.2 Xem & Theo dõi Đơn Hàng** | Xem danh sách đơn của user, xem chi tiết từng đơn, theo dõi trạng thái (chờ duyệt → đã duyệt → đang giao → đã giao → hủy) |
| **F4.3 Quản lý Đơn hàng (Admin)** | Duyệt/từ chối đơn hàng, cập nhật trạng thái, xem báo cáo doanh số |
| **F4.4 Hủy & Hoàn Lại** | Hủy đơn hàng nếu chưa duyệt, xử lý hoàn trả tiền, cập nhật lại tồn kho |
| **F4.5 Lịch sử Đơn Hàng** | Lưu lại tất cả đơn hàng quá khứ, xem thống kê chi tiêu, xuất báo cáo |

**F5. Quản lý Nội dung & Tương tác (Content & Interaction Management)**

| Chức năng chi tiết | Hoạt động cụ thể |
|---|---|
| **F5.1 Đánh giá & Bình luận** | Để lại đánh giá sao (1-5), viết bình luận về sách, xem bình luận từ người khác, admin duyệt bình luận |
| **F5.2 Wishlist** | Thêm/xóa sách khỏi wishlist, xem danh sách wishlist cá nhân, chuyển sách từ wishlist vào giỏ |
| **F5.3 Chatbot AI Tư vấn** | Trả lời câu hỏi về sách, đề xuất sách dựa trên sở thích, hỗ trợ 24/7 (sử dụng Google Gemini API) |
| **F5.4 Hệ thống Giới thiệu** | Chia sẻ sách cho bạn bè, tạo liên kết giới thiệu, theo dõi người được giới thiệu |

---

### 2.1.2 Biểu đồ Luồng Dữ liệu (DFD - Data Flow Diagram)

#### A. Khái niệm DFD

Biểu đồ Luồng Dữ liệu (DFD) mô tả cách dữ liệu chuyển động qua một hệ thống, bao gồm:
- **Entities (Thực thể)**: Người dùng bên ngoài, hệ thống bên ngoài
- **Processes (Quy trình)**: Các hoạt động xử lý dữ liệu
- **Data Stores (Kho dữ liệu)**: CSDL, file lưu trữ
- **Data Flows (Luồng dữ liệu)**: Chuyển động dữ liệu giữa các phần tử

#### B. DFD Mức Ngữ cảnh (Level 0)

Hệ thống BookStore được xem như một hộp đen duy nhất:

**Đầu vào**:
- Từ Khách hàng: Yêu cầu tìm kiếm, thêm giỏ hàng, thanh toán, xem đơn hàng
- Từ Admin: Dữ liệu quản lý (sản phẩm, voucher, banner, danh mục)

**Đầu ra**:
- Danh sách sách, đơn hàng, trạng thái thanh toán, báo cáo doanh số

**Kho dữ liệu**: CSDL MySQL chứa bảng Users, Books, Orders, OrderItems, Cart, Vouchers, Reviews, Wishlist, Categories, Roles

**Hệ thống bên ngoài tích hợp**:
- Google Books API (lấy dữ liệu sách)
- Google Gemini API (chatbot AI)
- SePay Webhook (thông báo thanh toán)

#### C. DFD Mức 1 - Các Quy trình Chính

| Quy trình | Mô tả | Đầu vào | Đầu ra |
|---|---|---|---|
| **P1: Xác thực Người dùng** | Xác minh thông tin đăng nhập, sinh JWT token | Email, Mật khẩu | JWT Token, Thông tin User |
| **P2: Quản lý Sản phẩm** | Tìm kiếm, lọc, xem chi tiết sách | Từ khóa, Bộ lọc | Danh sách sách, Chi tiết sách |
| **P3: Tạo & Quản lý Đơn hàng** | Tạo đơn, cập nhật trạng thái | Thông tin đơn hàng | Mã đơn hàng, Trạng thái |
| **P4: Thanh toán & Giỏ hàng** | Thêm sách vào giỏ, tính tiền, xử lý thanh toán | Sách, Voucher, Phương thức | Tổng tiền, Trạng thái thanh toán |
| **P5: Webhook Thanh toán** | Nhận thông báo từ cổng thanh toán | Dữ liệu từ SePay | Cập nhật trạng thái |

**Các kho dữ liệu (Data Stores)**:
- D1: Bảng Users (nhân viên, khách hàng)
- D2: Bảng Books (danh mục sách)
- D3: Bảng Orders (đơn hàng)
- D4: Bảng OrderItems (chi tiết đơn hàng)
- D5: Bảng Cart (giỏ hàng tạm)
- D6: Bảng Vouchers (mã khuyến mãi)
- D7: Bảng Reviews (đánh giá sách)
- D8: Bảng Wishlist (danh sách yêu thích)
- D9: Bảng Categories (danh mục sách)

#### D. DFD Mức 2 - Chi tiết các Quy trình Quan trọng

**P1: Quy trình Xác thực Người dùng**

Luồng xác thực gồm các bước:
1. Người dùng nhập email & mật khẩu
2. Hệ thống kiểm tra email tồn tại trong D1 (Users)
3. Xác minh mật khẩu (so sánh bcrypt hash)
4. Kiểm tra trạng thái tài khoản (active/inactive)
5. Nếu hợp lệ: Sinh JWT token, trả về token & thông tin user
6. Nếu không hợp lệ: Trả về lỗi (email không tồn tại, mật khẩu sai, tài khoản bị khóa)

**P2: Quy trình Tìm kiếm & Lọc Sách**

Luồng tìm kiếm gồm:
1. Người dùng nhập từ khóa tìm kiếm, bộ lọc (danh mục, giá), sắp xếp
2. Hệ thống phân tích yêu cầu
3. Truy vấn D2 (Books) với điều kiện LIKE, BETWEEN, ORDER BY
4. Lấy dữ liệu đánh giá từ D7 (Reviews)
5. Định dạng kết quả (làm sạch dữ liệu, thêm rating)
6. Phân trang (LIMIT, OFFSET)
7. Trả về JSON response

**P3: Quy trình Tạo Đơn Hàng**

Luồng tạo đơn gồm:
1. Người dùng xác nhận giỏ hàng (từ P4)
2. Lấy dữ liệu từ D5 (Cart)
3. Kiểm tra tồn kho từ D2 (Books) - nếu không đủ từ chối
4. Tính tiền: giá gốc - khuyến mãi + tiền ship (nếu có voucher từ D6)
5. Sinh mã đơn hàng duy nhất
6. INSERT vào D3 (Orders) và D4 (OrderItems)
7. Xóa giỏ hàng từ D5
8. Trả về mã đơn hàng

**P4: Quy trình Thanh toán & Giỏ Hàng**

Luồng thanh toán gồm:
1. Thêm sách vào D5 (Cart)
2. Tính tổng giá từ D2 (Books)
3. Nếu có voucher từ D6: Xác thực, tính giảm giá
4. Chọn phương thức thanh toán (COD hoặc online)
5. Nếu COD: Tạo đơn ngay, status = pending
6. Nếu online: Chuyển hướng đến cổng thanh toán, chờ webhook từ P5
7. Trả về trạng thái thanh toán

**P5: Webhook Xử lý Thanh toán**

Luồng webhook gồm:
1. Nhận dữ liệu từ SePay (mã đơn hàng, trạng thái thanh toán)
2. Xác minh chữ ký webhook
3. Cập nhật D3 (Orders) - payment_status = paid
4. Nếu thanh toán thành công: Gửi email xác nhận
5. Nếu thất bại: Cập nhật status, gửi thông báo khách

---

## 2.2. QUẢN TRỊ HỆ THỐNG (System Administration)

### 2.2.1 Khái niệm Quản trị Hệ thống

Quản trị hệ thống trong ứng dụng web TMĐT bao gồm:
- **Quản lý tài khoản người dùng**: Tạo, khóa, xóa tài khoản
- **Phân quyền truy cập**: Gán vai trò (roles) cho người dùng
- **Bảo mật phiên đăng nhập**: Sử dụng JWT token thay vì session truyền thống
- **Kiểm soát quyền hạn**: Chỉ cho phép người dùng có quyền truy cập tính năng nhạy cảm

### 2.2.2 Mô hình Phân quyền (RBAC - Role-Based Access Control)

#### A. Vai trò (Roles) trong hệ thống

Hệ thống BookStore sử dụng **mô hình RBAC** với hai vai trò chính:

| Vai trò | Quyền hạn | Chức năng cho phép |
|---|---|---|
| **ADMIN** | Tối cao | Quản lý toàn bộ sản phẩm, danh mục, tất cả đơn hàng, người dùng, vouchers, banners, HomeSection, xem báo cáo doanh số |
| **USER** | Người dùng thông thường | Xem sách, giỏ hàng, tạo đơn hàng, xem đơn hàng của mình, đánh giá/bình luận sách, quản lý wishlist, chat với AI chatbot |

#### B. Cấu trúc Bảng Phân quyền

**Mối quan hệ giữa Users và Roles**:
- 1 User có thể có nhiều Roles
- 1 Role có thể được gán cho nhiều Users
- Mối quan hệ N:M thông qua bảng trung gian `users_roles`

**Bảng roles**: Lưu tên vai trò (ADMIN, USER) và mô tả

**Bảng users_roles**: Liên kết User với Role

#### C. Áp dụng Quyền hạn trên Endpoint

Hệ thống sử dụng **Spring Security** với annotation `@PreAuthorize` để bảo vệ các endpoint:

```java
// Chỉ ADMIN mới có thể tạo danh mục
@PostMapping("/categories")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<?> createCategory(@RequestBody CategoryDTO dto) { ... }

// Chỉ người dùng đã xác thực mới có thể tạo đơn hàng
@PostMapping("/orders/create")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<?> createOrder(@RequestBody OrderDTO dto) { ... }

// Công khai, ai cũng có thể truy cập
@GetMapping("/books")
public ResponseEntity<?> getBooks() { ... }
```

**Cấp quyền hạn tổng thể (SecurityConfig)**:
- Public endpoints: `/api/auth/**`, `/api/books/**`, `/api/categories/**` (GET)
- Admin endpoints: `/api/admin/**` (POST, PUT, DELETE)
- User endpoints: `/api/orders/**`, `/api/cart/**` (yêu cầu authenticated)

#### D. Xử lý Lỗi Xác thực & Phân quyền

- **401 Unauthorized**: Người dùng chưa đăng nhập hoặc token hết hạn
- **403 Forbidden**: Người dùng không có quyền truy cập resource này
- Hệ thống trả về JSON response với thông báo lỗi rõ ràng

---

### 2.2.3 Bảo mật Phiên đăng nhập với JWT

#### A. JWT (JSON Web Token) là gì?

JWT là chuẩn công khai (RFC 7519) để tạo token được ký mật để truyền tải thông tin an toàn. JWT được sử dụng thay vì session truyền thống vì:
- **Stateless**: Server không cần lưu session, giảm tải database
- **Scalable**: Dễ mở rộng quy mô (load balancing, microservices)
- **Mobile-friendly**: Phù hợp cho ứng dụng di động (token lưu localStorage)
- **CORS-friendly**: Dễ tích hợp frontend chạy trên domain khác

#### B. Cấu trúc JWT

JWT gồm 3 phần, ngăn cách bằng dấu chấm (`.`):

1. **Header** (Base64URL encoded):
   - Chứa thuật toán ký (HS256) và loại token (JWT)

2. **Payload** (Base64URL encoded):
   - Chứa các claim (dữ liệu): email, roles, thời gian hết hạn (exp), thời gian tạo (iat)
   - VD: `{"email":"user@example.com","roles":["USER"],"exp":1704067200}`

3. **Signature** (HMAC-SHA256):
   - Ký lên Header + Payload bằng secret key
   - Đảm bảo token không bị giả mạo

#### C. Luồng Xác thực JWT trong BookStore

**Khi Đăng nhập**:
1. Khách hàng POST `/api/auth/login` với email & mật khẩu
2. Server xác minh thông tin, lấy roles của user
3. Server tạo JWT token (chứa email, roles, exp)
4. Server trả về token cho client
5. Client lưu token vào localStorage

**Khi Truy cập API**:
1. Client gửi request với header: `Authorization: Bearer <JWT>`
2. Server nhận JWT từ header
3. Server xác thực chữ ký JWT bằng secret key
4. Server kiểm tra token chưa hết hạn (exp)
5. Nếu hợp lệ: Lấy email từ JWT, tìm user trong DB, thiết lập authentication
6. Nếu không hợp lệ: Trả về 401 Unauthorized

**Cấu hình JWT trong properties**:
```properties
app.jwtSecret=bookstore-secret-key-at-least-32-chars
app.jwtExpirationMs=86400000  # 24 giờ (milliseconds)
```

**Lưu ý bảo mật**:
- Secret key ≥ 32 ký tự (cho HS256)
- Secret key không để trong source code, sử dụng environment variables
- Token nên có thời hạn ngắn (24 giờ hoặc ít hơn)
- Không lưu JWT trong localStorage (dễ bị XSS), nên dùng HttpOnly cookies

#### D. Thực hiện trong Code

**JwtTokenProvider**: Tạo và xác thực JWT token
- `generateToken()`: Tạo JWT từ authentication object
- `validateToken()`: Kiểm tra chữ ký, hết hạn
- `getEmailFromJWT()`: Lấy email từ JWT payload

**JwtAuthenticationFilter**: Xác thực JWT cho mỗi request
- Lấy JWT từ header Authorization
- Xác thực JWT
- Lấy email từ payload
- Tìm user từ database
- Thiết lập SecurityContext với thông tin user & roles

---

### 2.2.4 Bảo mật CORS (Cross-Origin Resource Sharing)

Frontend (chạy trên port khác, VD: 3000) cần tương tác với Backend (port 8080).

**Cấu hình CORS cho phép**:
- Origins: `localhost:3000`, `localhost:5500`, `127.0.0.1:3000` (frontend servers)
- Methods: GET, POST, PUT, DELETE, OPTIONS
- Headers: Tất cả (Authorization, Content-Type, etc.)
- Credentials: Cho phép gửi cookies & authorization headers

---

## 2.3. CƠ SỞ DỮ LIỆU (Database)

### 2.3.1 Lựa chọn Hệ quản trị CSDL

#### A. Tại sao chọn MySQL?

| Tiêu chí | Giải thích |
|---|---|
| **Chi phí** | Mã nguồn mở, miễn phí |
| **Độ tin cậy** | Được sử dụng rộng rãi, cộng đồng lớn, stable |
| **Hiệu suất** | Tốt cho ứng dụng vừa và nhỏ, xử lý concurrent tốt |
| **Dễ sử dụng** | Cài đặt đơn giản, cấu hình dễ |
| **Transactions** | Hỗ trợ ACID (InnoDB) cho giao dịch đáng tin cậy |
| **Tích hợp Spring** | Spring Data JPA hỗ trợ tuyệt vời |
| **Khả năng mở rộng** | Dễ scale, hỗ trợ replication & sharding |

### 2.3.2 Schema CSDL - Các Bảng Chính

#### Sơ đồ Mối quan hệ (ERD)

**Các bảng chính**:

| Bảng | Mục đích | Khóa chính | Quan hệ |
|---|---|---|---|
| **users** | Lưu thông tin người dùng | id | 1:N với orders, carts, reviews, wishlist |
| **roles** | Lưu vai trò (ADMIN, USER) | id | N:M với users (users_roles) |
| **categories** | Danh mục sách | id | 1:N với books |
| **books** | Thông tin sách | id (Google Books ID) | N:1 với categories, 1:N với order_items, reviews, cart, wishlist |
| **orders** | Đơn hàng | id | 1:N với order_items, N:1 với users |
| **order_items** | Chi tiết sách trong đơn hàng | id | N:1 với orders, N:1 với books |
| **cart** | Giỏ hàng tạm | id | N:1 với users, N:1 với books |
| **vouchers** | Mã khuyến mãi | id | Sử dụng khi tạo orders |
| **reviews** | Đánh giá sách | id | N:1 với books, N:1 với users |
| **wishlist** | Danh sách yêu thích | id | N:1 với users, N:1 với books |
| **home_sections** | Banner & section trang chủ | id | Quản lý hiển thị |

#### Chi tiết Bảng Quan trọng

**Bảng users**:
- Lưu email (unique), password hash, phone, tên, avatar, trạng thái (active/inactive)
- Có index trên email để tìm kiếm nhanh
- Thay đổi thường xuyên khi có đăng ký, cập nhật thông tin

**Bảng books**:
- Lưu tiêu đề, tác giả, nhà xuất bản, mô tả, giá, số lượng tồn kho, URL bìa (từ Google Books API)
- Khóa chính là Google Books ID (VD: "Z1hRQAA..." từ Google API)
- Có index trên tiêu đề để tìm kiếm nhanh
- Dữ liệu tương đối ổn định, chỉ update giá & stock

**Bảng orders**:
- Lưu mã đơn hàng (unique), user_id, trạng thái (pending, approved, shipped, delivered, cancelled), phương thức thanh toán, trạng thái thanh toán (unpaid, paid, failed, refunded)
- Có index trên user_id, order_code, status để truy vấn nhanh
- Thêm thường xuyên khi có đơn, ít xóa, hay update status

**Bảng order_items**:
- Lưu chi tiết mỗi sách trong đơn: order_id, book_id, số lượng, giá, tổng tiền
- Khóa ngoại đến orders & books

**Bảng cart**:
- Lưu giỏ hàng tạm của user: user_id, book_id, số lượng
- Dữ liệu tạm, hay thay đổi, xóa sau khi checkout
- Có unique constraint (user_id, book_id) để tránh duplicate

**Bảng vouchers**:
- Lưu mã voucher (code, unique), % hoặc số tiền giảm, điều kiện sử dụng, số lần dùng tối đa
- Dữ liệu quản lý, admin cập nhật thường xuyên

### 2.3.3 Tối ưu hóa CSDL

**Indexes (Chỉ mục)**:
- Index trên email (users) → tìm user nhanh khi login
- Index trên title (books) → tìm sách nhanh khi search
- Index trên user_id (orders) → lấy đơn của user nhanh
- Index trên status (orders) → lọc đơn theo trạng thái nhanh

**Khóa ngoại (Foreign Keys)**:
- Đảm bảo tính toàn vẹn dữ liệu (không thêm order với user không tồn tại)
- Set ON DELETE CASCADE (xóa user → xóa order của user)

**Thiết kế bảng**:
- Chuẩn hóa dữ liệu (3NF) để tránh lặp dữ liệu
- Loại bỏ anomalies (insert, update, delete anomalies)

---

## 2.4. NGÔN NGỮ LẬP TRÌNH VÀ FRAMEWORK

### 2.4.1 Backend: Java & Spring Boot

#### A. Tại sao chọn Java Spring Boot?

| Tiêu chí | Lợi ích |
|---|---|
| **Ngôn ngữ** | Java 21 - hỗ trợ features hiện đại, tối ưu hóa hiệu suất |
| **Framework** | Spring Boot 3.2.3 - "convention over configuration", giảm boilerplate |
| **Productivity** | Starter POMs tự động cấu hình, tăng tốc độ phát triển |
| **Scalability** | Xử lý concurrency tốt, thread-safe, dễ mở rộng quy mô |
| **Security** | Spring Security, JWT, encryption tích hợp sẵn |
| **Database** | Spring Data JPA - ORM tuyệt vời, giảm code SQL |
| **Testing** | JUnit, MockMvc, Testcontainers tích hợp |
| **Community** | Cộng đồng lớn nhất, tài liệu đầy đủ, thư viện phong phú |

#### B. Kiến trúc Backend - Layered Architecture

Hệ thống BookStore sử dụng **kiến trúc phân lớp** (Layered Architecture):

```
┌────────────────────────────────────────┐
│   PRESENTATION LAYER (REST Controllers) │
│   AuthController, BookController, ...  │
└────────────┬─────────────────────────┘
             │
┌────────────▼─────────────────────────┐
│    SERVICE LAYER (Business Logic)    │
│   UserService, BookService, ...      │
└────────────┬─────────────────────────┘
             │
┌────────────▼─────────────────────────┐
│  DATA ACCESS LAYER (JPA Repository)  │
│  UserRepository, BookRepository, ...  │
└────────────┬─────────────────────────┘
             │
┌────────────▼─────────────────────────┐
│     DATABASE LAYER (MySQL)           │
│    users, books, orders, ...          │
└──────────────────────────────────────┘
```

**Lợi ích của kiến trúc này**:
- **Tách biệt concern**: Mỗi layer có trách nhiệm riêng
- **Dễ test**: Có thể test từng layer độc lập
- **Dễ bảo trì**: Thay đổi database không ảnh hưởng presentation
- **Tái sử dụng code**: Service & Repository dùng lại ở nhiều nơi

**Các Layer chi tiết**:

1. **Presentation Layer (Controller)**:
   - Tiếp nhận HTTP request từ client
   - Gọi Service để xử lý business logic
   - Trả về HTTP response (JSON)
   - Không chứa business logic

2. **Service Layer**:
   - Chứa business logic (tính toán, validation, etc.)
   - Gọi Repository để truy cập DB
   - Xử lý transactions (@Transactional)
   - Throw exception cho Controller xử lý

3. **Repository Layer (Data Access)**:
   - Giao tiếp trực tiếp với database
   - Kế thừa từ JpaRepository<T, ID>
   - Cung cấp CRUD methods sẵn (save, findById, delete, etc.)
   - Có thể viết custom queries

4. **Entity Layer (ORM Mapping)**:
   - Đại diện cho bảng trong database
   - Mapping giữa Java object & database row
   - Sử dụng JPA annotations (@Entity, @Table, @Column, etc.)

#### C. Dependencies quan trọng

| Dependency | Mục đích |
|---|---|
| **spring-boot-starter-web** | REST API framework |
| **spring-boot-starter-data-jpa** | ORM, CRUD operations |
| **mysql-connector-java** | MySQL JDBC driver |
| **spring-boot-starter-security** | Authentication & Authorization |
| **jjwt-api** | JWT token generation & validation |
| **lombok** | Giảm boilerplate code (@Data, @Getter, @Setter) |
| **spring-boot-starter-validation** | Data validation |
| **spring-boot-starter-mail** | Gửi email |
| **gson** | JSON processing |
| **httpclient5** | Call external APIs |

### 2.4.2 Frontend: HTML5, CSS3, JavaScript Vanilla

#### A. Công nghệ được chọn

| Công nghệ | Phiên bản | Mục đích |
|---|---|---|
| **HTML5** | HTML5 | Cấu trúc trang web (semantic tags) |
| **CSS3** | CSS3 | Styling, responsive design (Flexbox, Grid) |
| **JavaScript** | ES6+ | Interactivity, AJAX (Fetch API), DOM manipulation |

**Tại sao không dùng Framework (React, Vue, Angular)?**
- Yêu cầu của dự án: Website bán sách vừa phải, không cần SPA complex
- Chi phí học tập & phát triển: Vanilla JS đơn giản hơn
- Performance: Không cần overhead của framework

#### B. Cấu trúc Frontend

```
Frontend/
├── index.html (Trang chủ)
├── pages/
│   ├── login.html (Đăng nhập)
│   ├── products.html (Danh sách sách)
│   ├── product-detail.html (Chi tiết sách)
│   ├── cart.html (Giỏ hàng)
│   ├── checkout.html (Thanh toán)
│   ├── orders.html (Lịch sử đơn hàng)
│   ├── account.html (Tài khoản cá nhân)
│   └── wishlist.html (Danh sách yêu thích)
├── admin/ (Admin pages)
│   └── dashboard.html (Admin dashboard)
└── assets/
    ├── css/ (Stylesheets)
    ├── js/ (JavaScript logic)
    │   ├── api.js (AJAX calls, Fetch API)
    │   ├── main.js (Trang chủ logic)
    │   ├── admin.js (Admin functions)
    │   ├── ai-chat.js (Chatbot logic)
    │   └── config.js (Configuration)
    └── images/ (Hình ảnh, banner)
```

#### C. Tích hợp API - Fetch API

Frontend gọi Backend thông qua **Fetch API**:

```javascript
// Gọi API GET (lấy danh sách sách)
fetch(`${API_BASE_URL}/books?page=0&size=12`)
  .then(response => response.json())
  .then(data => renderBooks(data))
  .catch(error => console.error(error));

// Gọi API POST (tạo đơn hàng)
fetch(`${API_BASE_URL}/orders/create`, {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${JWT_TOKEN}`
  },
  body: JSON.stringify(orderData)
})
.then(response => response.json())
.then(data => handleOrderSuccess(data))
.catch(error => handleOrderError(error));
```

---

## 2.5. TÍCH HỢP CÁC HỆ THỐNG BÊN NGOÀI

### 2.5.1 Google Books API

**Mục đích**: Lấy dữ liệu sách (tiêu đề, tác giả, nhà xuất bản, mô tả, hình bìa) từ Google Books

**Cách tích hợp**:
- Gửi HTTP request đến `https://www.googleapis.com/books/v1/volumes?q=`
- Query parameters: `q` (từ khóa tìm kiếm), `maxResults`, `fields`
- Phân tích JSON response, lấy thông tin cần thiết
- Lưu vào database (bảng books)

**Lợi ích**:
- Có sẵn dữ liệu sách, không cần nhập thủ công
- Cập nhật thông tin từ nguồn uy tín

### 2.5.2 Google Gemini API

**Mục đích**: Chatbot AI tư vấn sách, trả lời câu hỏi

**Cách tích hợp**:
- Gửi prompt (câu hỏi của user) đến Google Gemini API
- Nhận response (trả lời từ AI)
- Hiển thị trên frontend

**Lợi ích**:
- Hỗ trợ khách hàng 24/7
- Tăng engagement & user experience

### 2.5.3 SePay Payment Gateway

**Mục đض**: Cổng thanh toán trực tuyến (thanh toán thẻ, e-wallet)

**Cách tích hợp**:
- Client chuyển hướng đến trang thanh toán SePay
- User nhập thông tin thanh toán
- SePay gửi webhook callback về Backend
- Backend xác minh chữ ký webhook, cập nhật trạng thái đơn hàng
- Backend gửi email xác nhận

**Lợi ích**:
- Hỗ trợ thanh toán trực tuyến an toàn
- Tăng tùy chọn thanh toán cho khách hàng

---

## 2.6. KỸ THUẬT BẢNG MẬT QUAN TRỌNG

### 2.6.1 Mã hóa Mật khẩu (Password Hashing)

- Sử dụng **BCrypt** để mã hóa mật khẩu trước khi lưu
- BCrypt tạo hash không thể giải mã, thêm salt tự động
- Khi xác thực: So sánh mật khẩu nhập với BCrypt hash

### 2.6.2 HTTPS & Secure Headers

- Tất cả communication phải qua HTTPS (SSL/TLS)
- Thêm Secure Headers: HSTS, X-Content-Type-Options, X-Frame-Options

### 2.6.3 Input Validation & SQL Injection Prevention

- Validate tất cả input từ client
- Sử dụng Parameterized Queries (JPA tự động)
- Tránh raw SQL

### 2.6.4 XSS Prevention (Cross-Site Scripting)

- Escape output trước khi hiển thị (HTML entities)
- Sử dụng Content Security Policy (CSP) headers

---

## KẾT LUẬN CHƯƠNG II

Chương II đã trình bày chi tiết các kiến thức lý thuyết áp dụng trong dự án BookStore:

1. **Phân tích & Thiết kế (BFD, DFD)**: Cung cấp cái nhìn toàn diện về chức năng & luồng dữ liệu
2. **Quản trị Hệ thống (RBAC, JWT)**: Đảm bảo an ninh, phân quyền rõ ràng
3. **CSDL (MySQL)**: Lưu trữ dữ liệu tin cậy với schema tối ưu
4. **Công nghệ Stack**: Java Spring Boot + Frontend JS tạo ứng dụng hiện đại, scalable
5. **Tích hợp bên ngoài**: Google Books API, Gemini, SePay mở rộng tính năng

Những kiến thức này sẽ là nền tảng cho các chương tiếp theo về triển khai, kiểm thử và vận hành hệ thống.

