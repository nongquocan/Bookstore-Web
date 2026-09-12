# CHƯƠNG III. PHÂN TÍCH THIẾT KẾ HỆ THỐNG

## 3.1 PHÂN TÍCH THIẾT KẾ CƠ SỞ DỮ LIỆU

### 3.1.1 Khái niệm thiết kế CSDL

Thiết kế cơ sở dữ liệu (Database Design) là quá trình xác định cấu trúc logic và vật lý của CSDL nhằm đáp ứng các yêu cầu của hệ thống thông tin. Đối với hệ thống BookStore, thiết kế CSDL tuân theo mô hình quan hệ (Relational Model) với các nguyên tắc chuẩn hóa dữ liệu (Database Normalization) để đảm bảo tính nhất quán, hiệu suất, và dễ bảo trì.

**Các nguyên tắc thiết kế áp dụng**:
- **Normalization (Chuẩn hóa)**: Áp dụng 3NF (Third Normal Form) để loại bỏ dư thừa dữ liệu
- **Tính toàn vẹn dữ liệu (Data Integrity)**: Sử dụng Primary Key, Foreign Key, Check constraints
- **Hiệu suất (Performance)**: Tạo indexes trên các cột hay được tìm kiếm
- **Tính khả mở rộng (Scalability)**: Thiết kế schema cho phép thêm dữ liệu mà không ảnh hưởng

### 3.1.2 Biểu đồ Entity-Relationship (ERD)

**Biểu đồ ER tổng quát cho hệ thống BookStore:**

```
┌────────────────────────────────────────────────────────────────────────┐
│                     ENTITY-RELATIONSHIP DIAGRAM                         │
├────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  ┌─────────────────────────────┐                                        │
│  │         USERS               │                                        │
│  ├─────────────────────────────┤                                        │
│  │ PK: user_id (BIGINT)        │                                        │
│  │ • email (VARCHAR, UNIQUE)   │                                        │
│  │ • password_hash (VARCHAR)   │           ┌──────────────────┐        │
│  │ • first_name (VARCHAR)      │           │      ROLES       │        │
│  │ • last_name (VARCHAR)       │    M:N    ├──────────────────┤        │
│  │ • phone (VARCHAR)           │◄────────►│ PK: role_id      │        │
│  │ • is_active (BOOLEAN)       │   JT:    │ • role_name (UQ) │        │
│  │ • created_at (TIMESTAMP)    │users_role│ • description    │        │
│  │ • updated_at (TIMESTAMP)    │          └──────────────────┘        │
│  └──────────────┬──────────────┘                                        │
│                 │                                                        │
│                 │ 1:N                                                    │
│                 │                                                        │
│  ┌──────────────▼──────────────────────────────────────────┐            │
│  │              ORDERS                                     │            │
│  ├──────────────────────────────────────────────────────────┤            │
│  │ PK: order_id (BIGINT)                                  │            │
│  │ FK: user_id → USERS                                    │            │
│  │ • order_code (VARCHAR, UNIQUE)                         │            │
│  │ • status (ENUM: pending, approved, shipped, delivered) │            │
│  │ • payment_method (VARCHAR: cod, sepay, vnpay)          │            │
│  │ • payment_status (ENUM: unpaid, paid, failed)          │            │
│  │ • total_amount (DECIMAL)                               │            │
│  │ • shipping_address (TEXT)                              │            │
│  │ • created_at, updated_at (TIMESTAMP)                   │            │
│  └──────────────┬──────────────────────────────────────────┘            │
│                 │                                                        │
│                 │ 1:N                                                    │
│                 │                                                        │
│  ┌──────────────▼────────────────────────────────────┐                  │
│  │         ORDER_ITEMS                               │                  │
│  ├─────────────────────────────────────────────────────┤                  │
│  │ PK: order_item_id (BIGINT)                        │                  │
│  │ FK: order_id → ORDERS                             │                  │
│  │ FK: book_id → BOOKS                               │                  │
│  │ • quantity (INT)                                  │                  │
│  │ • price_per_unit (DECIMAL)                        │                  │
│  │ • subtotal (DECIMAL)                              │                  │
│  └────────────────────────────────────────────────────┘                  │
│                                                        │                  │
│  ┌──────────────────────────────────────────────────┐ │                  │
│  │ CART                                             │ │                  │
│  ├──────────────────────────────────────────────────┤ │                  │
│  │ PK: cart_id (BIGINT)                             │ │                  │
│  │ FK: user_id → USERS                              │ │                  │
│  │ FK: book_id → BOOKS                              │◄┼──────┐           │
│  │ • quantity (INT)                                 │ │      │           │
│  │ • created_at, updated_at (TIMESTAMP)             │ │      │           │
│  └──────────────────────────────────────────────────┘ │      │           │
│                                                        │      │           │
│  ┌──────────────────────────────────────────────────────────────┐        │
│  │         BOOKS (Sản phẩm chính)                    │      │N:1│        │
│  ├──────────────────────────────────────────────────────────────┤        │
│  │ PK: book_id (VARCHAR)                             │           │       │
│  │ FK: category_id → CATEGORIES                      │           │       │
│  │ • title (VARCHAR)                                 │           │       │
│  │ • subtitle (VARCHAR)                              │           │       │
│  │ • authors (VARCHAR)                               │           │       │
│  │ • publisher (VARCHAR)                             │           │       │
│  │ • published_date (VARCHAR)                        │           │       │
│  │ • description (TEXT)                              │           │       │
│  │ • price (DECIMAL)                                 │           │       │
│  │ • stock_quantity (INT)                            │           │       │
│  │ • cover_image_url (VARCHAR)                       │           │       │
│  │ • average_rating (DECIMAL)                        │           │       │
│  │ • created_at, updated_at (TIMESTAMP)              │           │       │
│  └──────────────┬───────────────────────────────────┘           │       │
│                 │                                                │       │
│                 │ N:1 (lọc theo danh mục)                        │       │
│                 │                                                │       │
│  ┌──────────────▼──────────────┐       ┌─────────────────────────────┐  │
│  │    CATEGORIES               │       │ WISHLIST                    │  │
│  ├─────────────────────────────┤       ├─────────────────────────────┤  │
│  │ PK: category_id (BIGINT)    │       │ PK: wishlist_id (BIGINT)    │  │
│  │ • name (VARCHAR, UNIQUE)    │       │ FK: user_id → USERS         │  │
│  │ • description (TEXT)        │       │ FK: book_id → BOOKS         │  │
│  │ • created_at (TIMESTAMP)    │       │ • added_date (TIMESTAMP)    │  │
│  └─────────────────────────────┘       └─────────────────────────────┘  │
│                                                                          │
│  ┌─────────────────────────────────┐   ┌──────────────────────────┐    │
│  │   REVIEWS                       │   │   VOUCHERS               │    │
│  ├─────────────────────────────────┤   ├──────────────────────────┤    │
│  │ PK: review_id (BIGINT)          │   │ PK: voucher_id (BIGINT)  │    │
│  │ FK: book_id → BOOKS             │   │ • code (VARCHAR, UQ)     │    │
│  │ FK: user_id → USERS             │   │ • discount_percent (DEC) │    │
│  │ • rating (INT 1-5)              │   │ • discount_amount (DEC)  │    │
│  │ • comment (TEXT)                │   │ • min_order_amount (DEC) │    │
│  │ • is_approved (BOOLEAN)         │   │ • max_usage (INT)        │    │
│  │ • created_at, updated_at        │   │ • used_count (INT)       │    │
│  └─────────────────────────────────┘   │ • expiry_date (DATE)     │    │
│                                         │ • is_active (BOOLEAN)    │    │
│                                         └──────────────────────────┘    │
│                                                                          │
│  ┌────────────────────────────────────┐                                  │
│  │  HOME_SECTIONS (Quản lý trang chủ)│                                  │
│  ├────────────────────────────────────┤                                  │
│  │ PK: section_id (BIGINT)            │                                  │
│  │ • title (VARCHAR)                  │                                  │
│  │ • section_type (VARCHAR)           │                                  │
│  │ • display_order (INT)              │                                  │
│  │ • is_active (BOOLEAN)              │                                  │
│  │ • created_at (TIMESTAMP)           │                                  │
│  └────────────────────────────────────┘                                  │
│                                                                          │
│  ┌────────────────────────────────────┐                                  │
│  │  AI_CHAT_HISTORY (Lịch sử chat)   │                                  │
│  ├────────────────────────────────────┤                                  │
│  │ PK: chat_id (BIGINT)               │                                  │
│  │ FK: user_id → USERS (nullable)     │                                  │
│  │ • user_message (TEXT)              │                                  │
│  │ • bot_response (TEXT)              │                                  │
│  │ • created_at (TIMESTAMP)           │                                  │
│  └────────────────────────────────────┘                                  │
└────────────────────────────────────────────────────────────────────────┘

Chú thích:
  PK = Primary Key (Khóa chính)
  FK = Foreign Key (Khóa ngoại)
  1:N = Một-Nhiều (One-to-Many)
  N:M = Nhiều-Nhiều (Many-to-Many)
  M:N = Nhiều-Nhiều (Many-to-Many)
  UQ = Unique Constraint
  JT = Join Table (Bảng trung gian)
```

### 3.1.3 Chi tiết các bảng dữ liệu cốt lõi

#### **Bảng 1: USERS (Người dùng)**

**Mục đích**: Lưu trữ thông tin khách hàng và quản trị viên

**Cấu trúc**:
```sql
CREATE TABLE users (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    phone VARCHAR(20),
    avatar_url VARCHAR(500),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_email (email),
    INDEX idx_phone (phone),
    CHECK (email LIKE '%@%.%'),
    CHECK (LENGTH(password_hash) >= 60) -- BCrypt output
);
```

**Giải thích các trường**:
- `user_id`: Khóa chính, tự động tăng
- `email`: Duy nhất, dùng để đăng nhập, validate email format
- `password_hash`: Lưu hash mật khẩu (BCrypt), độ dài tối thiểu 60 ký tự
- `first_name, last_name`: Tên khách hàng
- `phone`: Số điện thoại, dùng liên hệ
- `avatar_url`: URL ảnh đại diện (CloudStorage)
- `is_active`: Trạng thái kích hoạt tài khoản (soft delete)
- `created_at, updated_at`: Audit trail

**Ràng buộc**:
- Email phải unique, định dạng hợp lệ
- Password hash có độ dài tối thiểu (BCrypt)
- Index trên email, phone để tăng tốc độ tìm kiếm

---

#### **Bảng 2: ROLES (Vai trò)**

**Mục đích**: Lưu trữ các vai trò trong hệ thống (Admin, User)

**Cấu trúc**:
```sql
CREATE TABLE roles (
    role_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_role_name (role_name)
);

-- Insert default roles
INSERT INTO roles (role_name, description) VALUES
    ('ADMIN', 'Quản trị viên hệ thống'),
    ('USER', 'Khách hàng thông thường');
```

**Mối quan hệ**: 
- Bảng trung gian `users_roles` để thực hiện mối quan hệ N:M

**Bảng trung gian users_roles**:
```sql
CREATE TABLE users_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE RESTRICT
);
```

---

#### **Bảng 3: CATEGORIES (Danh mục sách)**

**Mục đích**: Phân loại sách thành các danh mục (Lịch sử, Khoa học, Tâm lý học, etc.)

**Cấu trúc**:
```sql
CREATE TABLE categories (
    category_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    category_name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    icon_url VARCHAR(255),
    display_order INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_category_name (category_name),
    INDEX idx_display_order (display_order)
);
```

**Giải thích**:
- `display_order`: Thứ tự hiển thị trên trang chủ (0 là ưu tiên cao)
- `is_active`: Ẩn/hiện danh mục
- Index trên name và order để tăng tốc độ

---

#### **Bảng 4: BOOKS (Sách - Sản phẩm chính)**

**Mục đích**: Lưu trữ thông tin chi tiết của mỗi sách

**Cấu trúc**:
```sql
CREATE TABLE books (
    book_id VARCHAR(50) PRIMARY KEY,  -- Google Books ID
    category_id BIGINT,
    title VARCHAR(500) NOT NULL,
    subtitle VARCHAR(500),
    authors VARCHAR(300),
    publisher VARCHAR(300),
    published_date VARCHAR(50),
    description LONGTEXT,
    price DECIMAL(10, 2) NOT NULL,
    stock_quantity INT DEFAULT 0,
    cover_image_url VARCHAR(500),
    average_rating DECIMAL(3, 2) DEFAULT 0.00,
    review_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (category_id) REFERENCES categories(category_id) ON DELETE SET NULL,
    INDEX idx_title (title),
    INDEX idx_category (category_id),
    INDEX idx_rating (average_rating DESC),
    INDEX idx_price (price),
    CHECK (price >= 0),
    CHECK (stock_quantity >= 0),
    CHECK (average_rating >= 0 AND average_rating <= 5)
);
```

**Giải thích**:
- `book_id`: Google Books ID (varchar 50), không auto-increment
- `category_id`: FK tới Categories (NULL nếu không có danh mục)
- `price`: Giá tiền (DECIMAL để tránh floating point errors)
- `stock_quantity`: Số lượng tồn kho
- `average_rating`: Trung bình đánh giá (0-5)
- Indexes trên title, category, rating để tối ưu tìm kiếm & sắp xếp
- CHECK constraints để đảm bảo dữ liệu hợp lệ

---

#### **Bảng 5: CART (Giỏ hàng)**

**Mục đích**: Lưu trữ giỏ hàng tạm thời của mỗi user

**Cấu trúc**:
```sql
CREATE TABLE cart (
    cart_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    book_id VARCHAR(50) NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_book (user_id, book_id),  -- Một user chỉ có một loại sách trong giỏ
    CHECK (quantity > 0),
    INDEX idx_user_id (user_id)
);
```

**Giải thích**:
- Unique constraint (user_id, book_id): Cùng user không thể thêm cùng sách nhiều lần, chỉ cập nhật quantity
- FOREIGN KEY ON DELETE CASCADE: Khi xóa user, xóa giỏ hàng

---

#### **Bảng 6: ORDERS (Đơn hàng)**

**Mục đích**: Lưu trữ các đơn hàng đã tạo

**Cấu trúc**:
```sql
CREATE TABLE orders (
    order_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_code VARCHAR(50) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    status ENUM('pending', 'approved', 'shipped', 'delivered', 'cancelled') 
        DEFAULT 'pending',
    payment_method VARCHAR(50),  -- 'cod', 'sepay', 'vnpay', 'momo'
    payment_status ENUM('unpaid', 'paid', 'payment_failed', 'refunded') 
        DEFAULT 'unpaid',
    total_amount DECIMAL(15, 2) NOT NULL,
    shipping_address TEXT NOT NULL,
    shipping_phone VARCHAR(20),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE RESTRICT,
    UNIQUE KEY unique_order_code (order_code),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at DESC),
    CHECK (total_amount > 0)
);
```

**Giải thích**:
- `order_code`: Mã đơn hàng duy nhất (định dạng: BKST-YYYYMMDD-XXXXX)
- `status`: Trạng thái đơn (ENUM để ràng buộc dữ liệu)
- `payment_method, payment_status`: Thông tin thanh toán
- `total_amount`: Tổng tiền (đã bao gồm discount, ship)
- Indexes trên user_id, status, created_at để query nhanh
- ON DELETE RESTRICT: Không cho xóa user nếu có đơn hàng (bảo vệ audit trail)

---

#### **Bảng 7: ORDER_ITEMS (Chi tiết đơn hàng)**

**Mục đích**: Lưu trữ danh sách sách trong mỗi đơn hàng

**Cấu trúc**:
```sql
CREATE TABLE order_items (
    order_item_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    book_id VARCHAR(50) NOT NULL,
    quantity INT NOT NULL,
    price_per_unit DECIMAL(10, 2) NOT NULL,  -- Giá tại thời điểm mua
    subtotal DECIMAL(15, 2) NOT NULL,        -- quantity * price_per_unit
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE RESTRICT,
    INDEX idx_order_id (order_id),
    CHECK (quantity > 0),
    CHECK (price_per_unit > 0),
    CHECK (subtotal = quantity * price_per_unit)
);
```

**Giải thích**:
- Lưu `price_per_unit` để ghi lại giá tại thời điểm mua (không lấy giá hiện tại)
- Lưu `subtotal` để audit, không tính lại
- ON DELETE CASCADE trên order_id: Xóa đơn → xóa chi tiết
- ON DELETE RESTRICT trên book_id: Không xóa sách nếu có trong đơn (audit trail)

---

#### **Bảng 8: REVIEWS (Đánh giá & Bình luận)**

**Mục đích**: Lưu trữ đánh giá sao và bình luận của khách hàng

**Cấu trúc**:
```sql
CREATE TABLE reviews (
    review_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    book_id VARCHAR(50) NOT NULL,
    user_id BIGINT NOT NULL,
    rating INT NOT NULL,  -- 1-5 sao
    comment TEXT,
    is_approved BOOLEAN DEFAULT FALSE,  -- Admin phải duyệt
    helpful_count INT DEFAULT 0,  -- Số người bấm "hữu ích"
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_book (user_id, book_id),  -- Một user chỉ đánh giá một lần/sách
    INDEX idx_book_id (book_id),
    INDEX idx_is_approved (is_approved),
    CHECK (rating >= 1 AND rating <= 5)
);
```

**Giải thích**:
- Unique (user_id, book_id): Một user chỉ đánh giá một sách một lần
- `is_approved`: Mặc định FALSE, admin phải duyệt trước khi hiển thị
- `helpful_count`: Tracking "hữu ích" cho helpful sorting

---

#### **Bảng 9: WISHLIST (Danh sách yêu thích)**

**Mục đích**: Lưu trữ danh sách sách mà user muốn mua sau

**Cấu trúc**:
```sql
CREATE TABLE wishlist (
    wishlist_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    book_id VARCHAR(50) NOT NULL,
    added_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_book (user_id, book_id),
    INDEX idx_user_id (user_id),
    INDEX idx_added_date (added_date DESC)
);
```

---

#### **Bảng 10: VOUCHERS (Mã khuyến mãi)**

**Mục đích**: Lưu trữ mã giảm giá

**Cấu trúc**:
```sql
CREATE TABLE vouchers (
    voucher_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    voucher_code VARCHAR(50) NOT NULL UNIQUE,
    discount_type ENUM('percentage', 'fixed_amount') DEFAULT 'percentage',
    discount_value DECIMAL(10, 2) NOT NULL,  -- % hoặc số tiền
    min_order_amount DECIMAL(10, 2) DEFAULT 0,  -- Đơn tối thiểu để dùng
    max_usage INT DEFAULT 999,  -- Số lần tối đa
    used_count INT DEFAULT 0,
    expiry_date TIMESTAMP NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    UNIQUE KEY unique_code (voucher_code),
    INDEX idx_is_active (is_active),
    INDEX idx_expiry_date (expiry_date),
    CHECK (discount_value > 0),
    CHECK (min_order_amount >= 0),
    CHECK (used_count <= max_usage)
);
```

---

#### **Bảng 11: HOME_SECTIONS (Quản lý trang chủ)**

**Mục đích**: Quản lý các section (banner, recommended books) trên trang chủ

**Cấu trúc**:
```sql
CREATE TABLE home_sections (
    section_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    section_title VARCHAR(100),
    section_type VARCHAR(50),  -- 'banner', 'featured_books', 'new_arrivals'
    display_order INT NOT NULL,
    banner_image_url VARCHAR(500),
    banner_link_url VARCHAR(500),  -- Click vào banner đi đâu
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    UNIQUE KEY unique_display_order (display_order),
    INDEX idx_is_active (is_active)
);
```

---

#### **Bảng 12: AI_CHAT_HISTORY (Lịch sử chat AI)**

**Mục đích**: Lưu lịch sử hội thoại với chatbot

**Cấu trúc**:
```sql
CREATE TABLE ai_chat_history (
    chat_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,  -- NULL nếu user chưa đăng nhập
    session_id VARCHAR(100),  -- Cho user anonymous
    user_message LONGTEXT NOT NULL,
    bot_response LONGTEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_session_id (session_id),
    INDEX idx_created_at (created_at DESC)
);
```

---

### 3.1.4 Mối quan hệ giữa các bảng

**Phân loại mối quan hệ:**

| Loại | Từ bảng | Đến bảng | Số lượng | Ví dụ |
|------|---------|----------|---------|-------|
| **1:N** (Một-Nhiều) | Users | Orders | 1 user : N orders | Một khách hàng có nhiều đơn hàng |
| **1:N** | Orders | OrderItems | 1 order : N items | Một đơn có nhiều sách |
| **1:N** | Books | OrderItems | 1 book : N items | Một sách có thể ở nhiều đơn |
| **1:N** | Categories | Books | 1 category : N books | Một danh mục có nhiều sách |
| **1:N** | Users | Cart | 1 user : N carts | Một user có nhiều sách trong giỏ |
| **1:N** | Users | Reviews | 1 user : N reviews | Một user viết nhiều đánh giá |
| **1:N** | Books | Reviews | 1 book : N reviews | Một sách có nhiều đánh giá |
| **1:N** | Users | Wishlist | 1 user : N wishlists | Một user có nhiều wishlist items |
| **N:M** (Nhiều-Nhiều) | Users | Roles | N users : M roles | Thông qua bảng users_roles |

**Ràng buộc Toàn vẹn Tham chiếu (Referential Integrity)**:

- **ON DELETE CASCADE**: Xóa cha → xóa con (ví dụ: xóa order → xóa order_items)
- **ON DELETE RESTRICT**: Không xóa cha nếu còn con (ví dụ: không xóa sách nếu còn order_items)
- **ON DELETE SET NULL**: Xóa cha → set con = NULL (ví dụ: xóa category → books.category_id = NULL)

---

### 3.1.5 Tối ưu hóa hiệu suất CSDL

**Indexes được tạo:**

```sql
-- Tìm kiếm người dùng
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_phone ON users(phone);

-- Tìm kiếm sách
CREATE INDEX idx_books_title ON books(title);
CREATE INDEX idx_books_category ON books(category_id);
CREATE INDEX idx_books_rating ON books(average_rating DESC);
CREATE INDEX idx_books_price ON books(price);

-- Lọc đơn hàng
CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_created_at ON orders(created_at DESC);

-- Tìm giỏ hàng
CREATE INDEX idx_cart_user_id ON cart(user_id);

-- Tìm đánh giá
CREATE INDEX idx_reviews_book_id ON reviews(book_id);
CREATE INDEX idx_reviews_is_approved ON reviews(is_approved);

-- Tìm voucher hợp lệ
CREATE INDEX idx_vouchers_active ON vouchers(is_active);
CREATE INDEX idx_vouchers_expiry ON vouchers(expiry_date);
```

**Constraints để đảm bảo dữ liệu hợp lệ:**

```sql
-- Users
CHECK (LENGTH(email) >= 5)
CHECK (LENGTH(password_hash) >= 60)  -- BCrypt length

-- Books
CHECK (price >= 0)
CHECK (stock_quantity >= 0)
CHECK (average_rating BETWEEN 0 AND 5)

-- Orders
CHECK (total_amount > 0)

-- Order Items
CHECK (quantity > 0)
CHECK (price_per_unit > 0)

-- Reviews
CHECK (rating BETWEEN 1 AND 5)

-- Vouchers
CHECK (discount_value > 0)
CHECK (min_order_amount >= 0)
```

**View để hỗ trợ queries phức tạp:**

```sql
-- View: Lấy order với thông tin user
CREATE VIEW order_with_user AS
SELECT 
    o.order_id, o.order_code, o.total_amount, o.status,
    u.user_id, u.email, u.first_name, u.last_name, u.phone
FROM orders o
JOIN users u ON o.user_id = u.user_id;

-- View: Lấy chi tiết đơn hàng
CREATE VIEW order_details_view AS
SELECT 
    o.order_id, o.order_code, 
    oi.order_item_id, b.book_id, b.title, 
    oi.quantity, oi.price_per_unit, oi.subtotal
FROM orders o
JOIN order_items oi ON o.order_id = oi.order_id
JOIN books b ON oi.book_id = b.book_id;
```

---

## 3.2 PHÂN TÍCH THIẾT KẾ CHỨC NĂNG

### 3.2.1 Khái niệm thiết kế chức năng

Thiết kế chức năng (Functional Design) mô tả chi tiết cách hoạt động của từng luồng chức năng chính trong hệ thống. Đối với hệ thống BookStore, có hai luồng chức năng quan trọng nhất:

1. **Luồng Mua hàng & Thanh toán (Purchase & Checkout Flow)**
2. **Luồng Quản lý Sản phẩm của Admin (Product Management Flow)**

Mỗi luồng được mô tả thông qua:
- **Use Case Diagram**: Hiển thị các actor và use cases
- **Sequence Diagram**: Hiển thị tương tác giữa các thành phần
- **State Diagram**: Hiển thị các trạng thái của đối tượng
- **Activity Diagram**: Hiển thị các hoạt động và quyết định

---

### 3.2.2 Luồng Mua hàng & Thanh toán (Purchase & Checkout Flow)

#### **A. Use Case Diagram**

```
┌─────────────────────────────────────────────────────────────────────┐
│                        PURCHASE & CHECKOUT SYSTEM                    │
├─────────────────────────────────────────────────────────────────────┤
│                                                                       │
│   ┌─────────────┐                    ┌──────────────────────────┐   │
│   │             │                    │                          │   │
│   │   CUSTOMER  │                    │     PAYMENT GATEWAY      │   │
│   │  (User)     │                    │   (SePay/COD)            │   │
│   │             │                    │                          │   │
│   └──────┬──────┘                    └──────────────────────────┘   │
│          │                                                            │
│          │ ┌─────────────────────────────────────────────────┐      │
│          │ │  USE CASES (Các tính năng)                      │      │
│          │ │                                                 │      │
│          │ │  1. Browse & Search Books                       │      │
│          │ │     └─ View Book Details                        │      │
│          │ │     └─ Read Reviews & Ratings                  │      │
│          │ │                                                 │      │
│          │ │  2. Add to Cart                                │      │
│          │ │     └─ View Cart                               │      │
│          │ │     └─ Update Quantities                       │      │
│          │ │     └─ Remove Items                            │      │
│          │ │                                                 │      │
│          │ │  3. Apply Voucher                             │      │
│          │ │     └─ Validate Voucher                       │      │
│          │ │     └─ Calculate Discount                     │      │
│          │ │                                                 │      │
│          │ │  4. Checkout                                  │      │
│          │ │     └─ Enter Shipping Address                │      │
│          │ │     └─ Select Payment Method (COD/SePay)     │      │
│          │ │                                                 │      │
│          │ │  5. Make Payment                              │      │
│          │ │     ├─ COD Payment (Cash on Delivery)         │      │
│          │ │     │  └─ Confirm Order                       │      │
│          │ │     └─ Online Payment (SePay)                 │      │
│          │ │        └─ Redirect to SePay                   │      │
│          │ │        └─ Handle Payment Response             │      │
│          │ │                                                 │      │
│          │ │  6. Order Confirmation                        │      │
│          │ │     └─ Send Confirmation Email                │      │
│          │ │     └─ View Order History                     │      │
│          │ │                                                 │      │
│          └─────────────────────────────────────────────────┘      │
│          │                                │                        │
│          └────────────────────────────────┘                        │
│                                                                       │
│  ┌───────────────────────────────────────────────────────────┐      │
│  │                 BOOKSTORE SYSTEM (Hệ thống)              │      │
│  │                                                           │      │
│  │  • Product Catalog (Danh mục sách)                       │      │
│  │  • Shopping Cart Service (Dịch vụ giỏ hàng)             │      │
│  │  • Order Service (Dịch vụ đơn hàng)                     │      │
│  │  • Payment Service (Dịch vụ thanh toán)                 │      │
│  │  • Voucher Service (Dịch vụ voucher)                    │      │
│  │  • Email Notification Service (Gửi email)              │      │
│  │                                                           │      │
│  └───────────────────────────────────────────────────────────┘      │
│                                                                       │
└─────────────────────────────────────────────────────────────────────┘
```

#### **B. Sequence Diagram cho Luồng COD (Thanh toán khi nhận hàng)**

```
Customer          Browser              Backend                Database      Email Service
    │                │                    │                      │               │
    │ 1. Search/Browse Books               │                      │               │
    ├───────────────────────────────────►  │                      │               │
    │                │ GET /api/books      │                      │               │
    │                ├─────────────────────────────────────────►  │               │
    │                │                     │ Query books          │               │
    │                │                     ├─────────────────────►│               │
    │                │                     │◄─────────────────────┤               │
    │                │◄─────────────────────────────────────────┤               │
    │◄───────────────────────────────────────────────────────────┤               │
    │                │                     │                      │               │
    │ 2. Click "Add to Cart"                │                      │               │
    ├───────────────────────────────────►  │                      │               │
    │                │ POST /api/cart/add   │                      │               │
    │                ├─────────────────────────────────────────►  │               │
    │                │ {book_id, quantity} │ Add to cart          │               │
    │                │                     ├─────────────────────►│               │
    │                │                     │ INSERT cart record   │               │
    │                │                     │◄─────────────────────┤               │
    │                │◄─────────────────────────────────────────┤               │
    │◄───────────────────────────────────────────────────────────┤               │
    │                │                     │                      │               │
    │ 3. Go to Checkout                     │                      │               │
    ├───────────────────────────────────►  │                      │               │
    │                │ GET /api/cart       │                      │               │
    │                ├─────────────────────────────────────────►  │               │
    │                │                     │ Query cart items     │               │
    │                │                     ├─────────────────────►│               │
    │                │                     │◄─────────────────────┤               │
    │                │◄─────────────────────────────────────────┤               │
    │◄───────────────────────────────────────────────────────────┤               │
    │                │ Display Cart Summary                         │               │
    │                │                     │                      │               │
    │ 4. Enter Voucher Code                 │                      │               │
    ├───────────────────────────────────►  │                      │               │
    │                │ POST /api/vouchers/validate               │               │
    │                ├─────────────────────────────────────────►  │               │
    │                │ {voucher_code}      │ Validate voucher    │               │
    │                │                     ├─────────────────────►│               │
    │                │                     │ Check expiry, usage  │               │
    │                │                     │◄─────────────────────┤               │
    │                │◄─────────────────────────────────────────┤               │
    │◄──────────────────────{discount_amount}─────────────────────┤               │
    │                │ Update Cart Total                           │               │
    │                │                     │                      │               │
    │ 5. Select Shipping Address            │                      │               │
    ├───────────────────────────────────►  │                      │               │
    │                │ (enter address)      │                      │               │
    │                │                     │                      │               │
    │ 6. Select "Payment on Delivery (COD)"│                      │               │
    ├───────────────────────────────────►  │                      │               │
    │                │ (select payment)     │                      │               │
    │                │                     │                      │               │
    │ 7. Click "Place Order"                │                      │               │
    ├───────────────────────────────────►  │                      │               │
    │                │ POST /api/orders/create                     │               │
    │                ├─────────────────────────────────────────►  │               │
    │                │ {cart, address,     │ 7a. Validate stock   │               │
    │                │  payment_method}    ├─────────────────────►│               │
    │                │                     │ Check stock qty      │               │
    │                │                     │◄─────────────────────┤               │
    │                │                     │                      │               │
    │                │                     │ 7b. Create order     │               │
    │                │                     ├─────────────────────►│               │
    │                │                     │ INSERT orders        │               │
    │                │                     │ INSERT order_items   │               │
    │                │                     │ UPDATE stock         │               │
    │                │                     │ DELETE cart items    │               │
    │                │                     │◄─────────────────────┤               │
    │                │                     │                      │               │
    │                │                     │ 7c. Send confirmation│──────────────►│
    │                │                     │                      │ Send email:   │
    │                │                     │                      │ - Order code  │
    │                │                     │                      │ - Items       │
    │                │                     │                      │ - Total       │
    │                │                     │                      │◄──────────────┤
    │                │                     │                      │               │
    │                │◄─────────────────────{order_code}─────────┤               │
    │◄───────────────────────────────────────────────────────────┤               │
    │                │ Display Success Page                        │               │
    │                │ Order Code: BKST-2026-06-06-001            │               │
    │                │ Status: PENDING (chờ duyệt)                 │               │
    │                │ Payment: UNPAID                              │               │
    │                │                     │                      │               │
```

#### **C. Sequence Diagram cho Luồng SePay (Thanh toán online)**

```
Customer          Browser              Backend          SePay Gateway      Database
    │                │                    │                  │                │
    │ (... tương tự bước 1-6 như COD ...)                    │                │
    │                │                     │                  │                │
    │ 7. Click "Pay Now (SePay)"           │                  │                │
    ├───────────────────────────────────►  │                  │                │
    │                │ POST /api/payment/sepay/initiate       │                │
    │                ├─────────────────────────────────────────────────────────► │
    │                │ {order_id, amount}  │ Create payment    │                │
    │                │                     │ request           │                │
    │                │                     ├─────────────────────────────────► │
    │                │                     │ (InsertQuery: order status = pending) 
    │                │                     │                  │                │
    │                │                     │ 7b. Call SePay    │                │
    │                │                     │     Create Payment│                │
    │                │                     ├──────────────────►                │
    │                │                     │ {transaction_id,  │                │
    │                │                     │  amount, callback}│                │
    │                │                     │                  │                │
    │                │                     │◄──────────────────┤                │
    │                │◄─────────────────────{payment_url}─────┤                │
    │◄───────────────────────────────────────────────────────────────────────────┤
    │                │ Redirect to SePay page                  │                │
    │                │                     │                  │                │
    │                │ ==================== (User on SePay Payment Page) =========│
    │                │                     │                  │                │
    │ (on SePay)     │ 8. Enter Card/Bank  │                  │                │
    │ Enter payment details                │                  │                │
    │ Click "Pay"    ────────────────────► (SePay processes)──┤                │
    │                │                     │                  │                │
    │                │ ==================== (SePay confirms payment or fails) ==│
    │                │                     │                  │                │
    │                │                     │ 9. Webhook notification:           │
    │                │                     │◄──────────────────{success/failed}│
    │                │                     │ POST /payment/sepay-callback      │
    │                │                     │ {transaction_id, status,          │
    │                │                     │  amount, timestamp}               │
    │                │                     │                  │                │
    │                │                     │ 9a. Verify webhook (signature)   │
    │                │                     │                  │                │
    │                │                     │ 9b. Find order   │                │
    │                │                     ├─────────────────────────────────► │
    │                │                     │                  │                │
    │                │                     │ 9c. Update order status           │
    │                │                     │ If success:       │                │
    │                │                     │ - payment_status = PAID           │
    │                │                     │ - order_status = APPROVED         │
    │                │                     ├─────────────────────────────────► │
    │                │                     │ UPDATE orders    │                │
    │                │                     │◄─────────────────────────────────┤
    │                │                     │                  │                │
    │ (Meanwhile, user waits or             │ 10. Redirect user back to        │
    │  is redirected back)                  │     checkout success page        │
    │◄────────────{success page}────────────┤                  │                │
    │                │                     │                  │                │
    │                │ Display Success     │                  │                │
    │                │ Order paid!         │                  │                │
    │                │ Status: APPROVED    │                  │                │
    │                │                     │                  │                │
```

#### **D. State Diagram cho Order (Trạng thái đơn hàng)**

```
                    ┌─────────────────┐
                    │   NEW ORDER     │
                    │ (Tạo từ cart)   │
                    └────────┬────────┘
                             │
                ┌────────────┴────────────┐
                │                         │
                ▼                         ▼
        ┌───────────────┐         ┌──────────────┐
        │   PENDING     │         │ PAYMENT_FAIL │
        │ (Chờ duyệt)   │         │ (Thanh toán  │
        └───────┬───────┘         │  thất bại)   │
                │                 └──────────────┘
                │ Admin duyệt                  ▲
                │ (payment = PAID)             │
                ▼                              │
        ┌──────────────┐                       │
        │  APPROVED    │                       │
        │(Đã duyệt)    │                       │
        └───────┬──────┘                       │
                │ Admin cập nhật               │
                │ status = SHIPPED             │
                ▼                              │
        ┌───────────────┐                      │
        │   SHIPPED     │◄─────────────────────┤
        │ (Đang giao)   │ Retry payment
        └───────┬───────┘
                │ Delivery confirmed
                │
                ▼
        ┌──────────────┐
        │  DELIVERED   │
        │ (Đã giao)    │
        └──────────────┘

        Hoặc:
        
        PENDING ──(Cancel)──► CANCELLED
                               (Bị hủy)
```

#### **E. Activity Diagram cho Luồng Thanh toán**

```
(start)
  │
  ▼
┌─────────────────────────────┐
│ Customer tạo Order          │
│ - Select books              │
│ - Enter shipping address    │
│ - Apply voucher (optional)  │
└──────────┬──────────────────┘
           │
           ▼
┌─────────────────────────────┐
│ Choose Payment Method       │
└──────────┬──────────────────┘
           │
    ┌──────┴──────┐
    │             │
 COD│             │SePay
    ▼             ▼
┌─────────────┐  ┌──────────────────┐
│ COD Payment │  │ Create Payment   │
│ Process     │  │ Request at SePay │
└──────┬──────┘  └────────┬─────────┘
       │                  │
       ▼                  ▼
┌──────────────┐  ┌───────────────────┐
│ Create Order │  │ Redirect user to  │
│ Status=      │  │ SePay payment page│
│  PENDING     │  └─────────┬─────────┘
└──────┬───────┘            │
       │                    ▼
       │            ┌───────────────────┐
       │            │ User enters       │
       │            │ Card/Bank details │
       │            └─────────┬─────────┘
       │                      │
       │                      ▼
       │            ┌───────────────────┐
       │            │ SePay processes   │
       │            │ payment           │
       │            └────┬──────────┬───┘
       │                 │          │
       │           Success│         │Failed
       │                 ▼          ▼
       │        ┌────────────┐  ┌─────────────┐
       │        │ SePay sends│  │ Retry or    │
       │        │ webhook    │  │ Cancel order│
       │        │ (success)  │  └─────────────┘
       │        └────┬───────┘
       │             │
       └─────┬───────┘
             ▼
     ┌──────────────────┐
     │ Update Order     │
     │ Status=APPROVED  │
     │ Payment=PAID     │
     └────────┬─────────┘
              │
              ▼
     ┌──────────────────┐
     │ Send Confirmation│
     │ Email            │
     └────────┬─────────┘
              │
              ▼
     ┌──────────────────┐
     │ Return success   │
     │ Response         │
     └────────┬─────────┘
              │
              ▼
           (end)
```

---

### 3.2.3 Luồng Quản lý Sản phẩm của Admin (Product Management Flow)

#### **A. Use Case Diagram**

```
┌────────────────────────────────────────────────────────────┐
│         ADMIN PRODUCT MANAGEMENT SYSTEM                    │
├────────────────────────────────────────────────────────────┤
│                                                             │
│   ┌──────────┐              ┌────────────────────────┐    │
│   │  ADMIN   │              │ USE CASES              │    │
│   │ (User)   │              │                        │    │
│   └────┬─────┘              │ 1. Login              │    │
│        │                    │    └─ Authenticate    │    │
│        │                    │                        │    │
│        │                    │ 2. Manage Categories   │    │
│        │                    │    ├─ Create Category │    │
│        │                    │    ├─ Update Category │    │
│        │                    │    └─ Delete Category │    │
│        │                    │                        │    │
│        │                    │ 3. Manage Products     │    │
│        │                    │    ├─ Add Product     │    │
│        │                    │    ├─ Edit Product    │    │
│        │                    │    ├─ Upload Cover    │    │
│        │                    │    ├─ Update Price    │    │
│        │                    │    ├─ Update Stock    │    │
│        │                    │    ├─ View Details    │    │
│        │                    │    └─ Delete Product  │    │
│        │                    │                        │    │
│        │                    │ 4. Manage Orders       │    │
│        │                    │    ├─ View Orders     │    │
│        │                    │    ├─ Approve/Reject  │    │
│        │                    │    ├─ Update Status   │    │
│        │                    │    ├─ Mark as Shipped │    │
│        │                    │    └─ View Details    │    │
│        │                    │                        │    │
│        │◄───────────────────┤ 5. Manage Vouchers     │    │
│        │                    │    ├─ Create Voucher  │    │
│        │                    │    ├─ Edit Voucher    │    │
│        │                    │    └─ Deactivate      │    │
│        │                    │                        │    │
│        │                    │ 6. View Dashboard      │    │
│        │                    │    ├─ Sales Stats     │    │
│        │                    │    ├─ Order Count     │    │
│        │                    │    └─ Revenue         │    │
│        │                    │                        │    │
│        └────────────────────┤ 7. Manage Reviews      │    │
│                             │    ├─ View Reviews    │    │
│                             │    └─ Approve/Reject  │    │
│                             │                        │    │
│                             └────────────────────────┘    │
│                                                             │
│  ┌───────────────────────────────────────────────┐        │
│  │     BOOKSTORE ADMIN SYSTEM                    │        │
│  │                                               │        │
│  │  • Product Service (Quản lý sách)            │        │
│  │  • Category Service (Quản lý danh mục)       │        │
│  │  • Order Service (Quản lý đơn hàng)          │        │
│  │  • Voucher Service (Quản lý mã giảm giá)    │        │
│  │  • Review Service (Quản lý đánh giá)         │        │
│  │  • Dashboard Service (Thống kê)              │        │
│  │                                               │        │
│  └───────────────────────────────────────────────┘        │
│                                                             │
└────────────────────────────────────────────────────────────┘
```

#### **B. Sequence Diagram cho Thêm Sách Mới**

```
Admin          Browser              Backend              Database        File Storage
  │                │                    │                    │                │
  │ 1. Login       │                    │                    │                │
  ├──────────────►│ POST /api/auth/login                  │                │
  │                ├───────────────────────────────────────►│                │
  │                │                    │ Verify JWT token   │                │
  │                │◄────────────────────{JWT_token}────────┤                │
  │                │                    │                    │                │
  │ 2. Go to Admin Dashboard           │                    │                │
  ├──────────────►│ GET /api/admin/dashboard               │                │
  │                ├───────────────────────────────────────►│                │
  │                │                    │ Query stats        │                │
  │                │                    ├───────────────────►│                │
  │                │                    │◄───────────────────┤                │
  │                │◄──────────{dashboard_data}──────────────┤                │
  │                │ Display Dashboard                       │                │
  │                │                    │                    │                │
  │ 3. Click "Add New Product"          │                    │                │
  ├──────────────►│ GET /api/admin/products/form            │                │
  │                ├───────────────────────────────────────►│                │
  │                │ (Get product form)  │                    │                │
  │                │◄────────────────────────────────────────┤                │
  │                │ Display Form                            │                │
  │                │ - Title, Authors                        │                │
  │                │ - Publisher, Category                   │                │
  │                │ - Price, Stock, Description            │                │
  │                │ - Cover Image Upload                   │                │
  │                │                    │                    │                │
  │ 4. Fill in Form & Upload Cover Image                    │                │
  ├──────────────►│ POST /api/admin/products/create         │                │
  │                ├───────────────────────────────────────►│                │
  │                │ {book_data}        │ 4a. Validate data │                │
  │                │ + cover_image      │    (length, type) │                │
  │                │                    │                    │                │
  │                │                    │ 4b. Upload image  │                │
  │                │                    ├──────────────────────────────────► │
  │                │                    │ (FormData multipart)               │
  │                │                    │ (JPEG/PNG, max 2MB)               │
  │                │                    │◄──────────────────────────────────┤
  │                │                    │ {image_url}                        │
  │                │                    │                    │                │
  │                │                    │ 4c. Create book   │                │
  │                │                    ├───────────────────►│                │
  │                │                    │ INSERT books      │                │
  │                │                    │ (title, authors,  │                │
  │                │                    │  category_id,     │                │
  │                │                    │  price, stock,    │                │
  │                │                    │  cover_image_url) │                │
  │                │                    │◄───────────────────┤                │
  │                │◄────────────────────{book_id, success}──┤                │
  │◄───────────────────────────────────────────────────────────────────────────┤
  │                │ Display Success Message                 │                │
  │                │ "Product added successfully!"          │                │
  │                │                    │                    │                │
```

#### **C. Sequence Diagram cho Cập nhật Trạng thái Đơn hàng**

```
Admin          Browser              Backend              Database        Email Service
  │                │                    │                    │                │
  │ 1. View Orders List                 │                    │                │
  ├──────────────►│ GET /api/admin/orders                   │                │
  │                ├───────────────────────────────────────►│                │
  │                │                    │ Query orders      │                │
  │                │                    ├───────────────────►│                │
  │                │                    │ WHERE status IN   │                │
  │                │                    │  (pending,approved)│                │
  │                │                    │◄───────────────────┤                │
  │                │◄──────────────────{orders_list}────────┤                │
  │                │ Display Orders Table                    │                │
  │                │ (Order Code, Customer, Total, Status)  │                │
  │                │                    │                    │                │
  │ 2. Click Order #BKST-2026-06-06-001 │                    │                │
  ├──────────────►│ GET /api/admin/orders/123               │                │
  │                ├───────────────────────────────────────►│                │
  │                │                    │ Query order       │                │
  │                │                    ├───────────────────►│                │
  │                │                    │ SELECT * FROM     │                │
  │                │                    │  orders WHERE     │                │
  │                │                    │  order_id = 123   │                │
  │                │                    │◄───────────────────┤                │
  │                │                    │                    │                │
  │                │                    │ Query order items │                │
  │                │                    ├───────────────────►│                │
  │                │                    │ SELECT * FROM     │                │
  │                │                    │  order_items      │                │
  │                │                    │  WHERE order_id   │                │
  │                │                    │◄───────────────────┤                │
  │                │◄─────────────{order_details}───────────┤                │
  │                │ Display Order Detail                    │                │
  │                │ - Order Code: BKST-2026-06-06-001      │                │
  │                │ - Customer: John Doe                    │                │
  │                │ - Items: [Book1 x2, Book2 x1]          │                │
  │                │ - Total: 500,000 VND                    │                │
  │                │ - Current Status: PENDING               │                │
  │                │ - Status Buttons: [Approve] [Reject]   │                │
  │                │                    │                    │                │
  │ 3. Click "Approve Order"             │                    │                │
  ├──────────────►│ PUT /api/admin/orders/123/approve        │                │
  │                ├───────────────────────────────────────►│                │
  │                │                    │ 3a. Verify admin  │                │
  │                │                    │     (has ADMIN    │                │
  │                │                    │      role in JWT) │                │
  │                │                    │                    │                │
  │                │                    │ 3b. Update order  │                │
  │                │                    ├───────────────────►│                │
  │                │                    │ UPDATE orders     │                │
  │                │                    │ SET               │                │
  │                │                    │  status =         │                │
  │                │                    │   'approved'      │                │
  │                │                    │  updated_at =     │                │
  │                │                    │   NOW()           │                │
  │                │                    │ WHERE order_id    │                │
  │                │                    │  = 123            │                │
  │                │                    │◄───────────────────┤                │
  │                │                    │                    │                │
  │                │                    │ 3c. Send email    │                │
  │                │                    │     to customer   │                │
  │                │                    ├──────────────────────────────────►│
  │                │                    │                    │ Send email:   │
  │                │                    │                    │ - Status     │
  │                │                    │                    │   changed to  │
  │                │                    │                    │   APPROVED    │
  │                │                    │                    │ - Ready for   │
  │                │                    │                    │   shipment    │
  │                │                    │                    │◄──────────────┤
  │                │◄──────────────{success, status}────────┤                │
  │◄───────────────────────────────────────────────────────────────────────────┤
  │                │ Display Success                         │                │
  │                │ "Order approved!"                       │                │
  │                │ New Status: APPROVED                   │                │
  │                │ Next Action: Mark as Shipped           │                │
  │                │                    │                    │                │
```

#### **D. State Diagram cho Admin Actions**

```
                          ┌────────────────┐
                          │  ORDER PENDING  │
                          │ (Chờ Admin duyệt)
                          └────────┬────────┘
                                   │
                  ┌────────────────┴────────────────┐
                  │                                 │
                  ▼                                 ▼
          ┌──────────────┐                  ┌──────────────┐
          │  APPROVED    │                  │  CANCELLED   │
          │ (Admin duyệt)│                  │ (Admin hủy)  │
          └──────┬───────┘                  └──────────────┘
                 │
                 │ Admin cập nhật
                 │ status = SHIPPED
                 ▼
          ┌──────────────┐
          │   SHIPPED    │
          │(Đang giao)   │
          └──────┬───────┘
                 │
                 │ Delivery confirmed
                 │
                 ▼
          ┌──────────────┐
          │  DELIVERED   │
          │ (Đã giao)    │
          └──────────────┘
```

---

## 3.3 CÁC CHỨC NĂNG CHƯA HOÀN THIỆN

### 3.3.1 Danh sách các chức năng chưa hoàn thành

Dựa trên quá trình phát triển hệ thống BookStore, các chức năng sau vẫn đang trong giai đoạn phát triển hoặc chưa bắt đầu:

| ID | Tên chức năng | % Hoàn thành | Ưu tiên | Trạng thái |
|----|--------------|-------------|--------|-----------|
| **Nhóm Thanh toán** |
| F-1 | Thanh toán SePay (Online) | 50% | Cao | Đang phát triển |
| F-2 | Thanh toán VNPAY | 0% | Cao | Chưa bắt đầu |
| F-3 | Thanh toán Momo | 0% | Cao | Chưa bắt đầu |
| **Nhóm AI & Chatbot** |
| F-4 | AI Chatbot tư vấn sách | 40% | Trung bình | Đang phát triển |
| F-5 | Recommendations Engine | 0% | Trung bình | Chưa bắt đầu |
| **Nhóm Quản lý & Hiển thị** |
| F-6 | Quản lý Banner quảng cáo | 70% | Trung bình | Đang phát triển |
| F-7 | Admin Dashboard hoàn chỉnh | 30% | Cao | Đang phát triển |
| F-8 | Analytics & Reports | 0% | Trung bình | Chưa bắt đầu |
| **Nhóm Quản lý Inventory** |
| F-9 | Low stock alerts | 0% | Thấp | Chưa bắt đầu |
| F-10 | Inventory history tracking | 0% | Thấp | Chưa bắt đầu |
| **Nhóm Khác** |
| F-11 | Notification System (SMS/Push) | 0% | Trung bình | Chưa bắt đầu |
| F-12 | Multi-language support | 0% | Thấp | Chưa bắt đầu |
| F-13 | Performance Optimization (Cache) | 20% | Cao | Đang phát triển |

---

### 3.3.2 Chi tiết các chức năng chưa hoàn thành

#### **A. Thanh toán SePay (F-1) - 50% hoàn thành**

**Mô tả**: Tích hợp cổng thanh toán SePay để khách hàng có thể thanh toán online thông qua ngân hàng, thẻ tín dụng.

**Công việc đã hoàn thành**:
- ✅ Tạo `SepayWebhookController` để nhận webhook
- ✅ Cấu hình SePay credentials trong `application.properties`
- ✅ Tạo endpoint POST `/payment/sepay/initiate`
- ✅ Tạo endpoint POST `/payment/sepay-callback` cho webhook

**Công việc còn lại**:
- ❌ Implement `SepayPaymentService` để gọi SePay API
- ❌ Xây dựng hàm `createPaymentRequest()` với logic sinh request
- ❌ Xây dựng hàm `handleWebhookCallback()` để xử lý response từ SePay
- ❌ Implement webhook signature verification (HMAC-SHA256)
- ❌ Cập nhật Order status sau khi thanh toán thành công
- ❌ Xử lý các trường hợp lỗi (timeout, retry payment)
- ❌ Unit testing & Integration testing với SePay sandbox

**Lý do chưa hoàn thành**:
- **Kỹ thuật**: Cần hiểu chi tiết API của SePay, flow thanh toán webhook
- **Thời gian**: Độ phức tạp cao, cần testing kỹ lưỡng
- **Dependencies**: SePay yêu cầu xác thực tài khoản, cấp API key, không thể test offline

**Ước tính thời gian hoàn thành**: 1-2 tuần

---

#### **B. AI Chatbot tư vấn sách (F-4) - 40% hoàn thành**

**Mô tả**: Chatbot sử dụng Google Generative AI (Gemini) để tư vấn sách, trả lời câu hỏi, gợi ý sách phù hợp.

**Công việc đã hoàn thành**:
- ✅ Tạo `AiChatController` với endpoint POST `/api/ai/chat`
- ✅ Cấu hình Google Gemini API key trong `application.properties`
- ✅ Tạo file `ai-chat.js` trên frontend
- ✅ Endpoint cho phép tất cả users (không cần authenticate)

**Công việc còn lại**:
- ❌ Implement `AiChatService.chat()` để gọi Google Gemini API
- ❌ Xây dựng system prompt tốt (instruction cho bot)
- ❌ Parse response từ Gemini API
- ❌ Implement `AiChatHistory` entity để lưu lịch sử chat
- ❌ Xây dựng context awareness (nhớ cuộc hội thoại trước)
- ❌ Implement sách gợi ý thông minh (sử dụng keyword extraction)
- ❌ Rate limiting (giới hạn số request, ngăn abuse)
- ❌ Frontend UI hoàn chỉnh (chat bubbles, typing indicator)
- ❌ Error handling & fallback responses
- ❌ Testing với user scenarios khác nhau

**Lý do chưa hoàn thành**:
- **Kỹ thuật**: NLP & prompt engineering là kỹ năng mới, cần thử nghiệm nhiều prompts
- **Thời gian**: Cần fine-tuning chatbot để trả lời tốt, không hallucinate
- **Chi phí**: Google Gemini API tính theo token, cần optimize để giảm cost

**Ước tính thời gian hoàn thành**: 1-2 tuần

---

#### **C. Admin Dashboard hoàn chỉnh (F-7) - 30% hoàn thành**

**Mô tả**: Dashboard tổng quát cho Admin với KPIs, charts, quản lý đơn hàng, sản phẩm.

**Công việc đã hoàn thành**:
- ✅ Tạo file `/Frontend/admin/dashboard.html`
- ✅ API endpoints cơ bản để lấy statistics (doanh số, số users, đơn hàng)
- ✅ Layout ban đầu

**Công việc còn lại**:
- ❌ KPI cards (doanh số hôm nay, số user mới, chờ xử lý)
- ❌ Charts visualization (Chart.js):
  - Revenue trend (line chart)
  - Top 10 books (bar chart)
  - Payment method distribution (pie chart)
  - Order status distribution (donut chart)
- ❌ Recent orders table (phân trang, sort, filter)
- ❌ User management section
- ❌ Product management quick actions
- ❌ Real-time updates (WebSocket hoặc polling)
- ❌ Responsive design cho mobile
- ❌ Dark mode
- ❌ Export reports (PDF, Excel)

**Lý do chưa hoàn thành**:
- **UI/UX**: Cần thiết kế đẹp, professional
- **Data visualization**: Cần chọn chart library, học Chart.js
- **Real-time**: Implementcâu WebSocket phức tạp hơn simple REST

**Ước tính thời gian hoàn thành**: 1-2 tuần

---

#### **D. Thanh toán VNPAY (F-2) - 0% hoàn thành**

**Mô tả**: Tích hợp cổng thanh toán VNPAY (phương thức thanh toán phổ biến tại Việt Nam).

**Công việc cần làm**:
- ❌ Đăng ký VNPAY merchant account
- ❌ Lấy API credentials (Merchant ID, Secret Key)
- ❌ Implement `VnpayPaymentService`
- ❌ Xây dựng security signature (HMAC-SHA512)
- ❌ Xây dựng redirect URL
- ❌ Xử lý IPN (Instant Payment Notification) từ VNPAY
- ❌ Frontend redirect & return page
- ❌ Testing với VNPAY sandbox
- ❌ Error handling & transaction reconciliation

**Lý do chưa bắt đầu**:
- **Ưu tiên thấp hơn SePay**: Hiện tại SePay đã cover nhu cầu
- **Chi phí & Thời gian**: Cần merchant account, testing phức tạp
- **Business logic tương tự SePay**: Có thể làm sau khi SePay xong

**Ước tính thời gian hoàn thành**: 5-7 ngày (sau khi hoàn SePay)

---

#### **E. Notification System (F-11) - 0% hoàn thành**

**Mô tả**: Hệ thống gửi thông báo cho khách hàng (email, SMS, in-app notification) khi có sự kiện quan trọng.

**Phạm vi thông báo cần**:
- Xác nhận đơn hàng
- Xác thực email (OTP)
- Đơn hàng được duyệt
- Đơn hàng đang giao
- Đơn hàng đã giao
- Mật khẩu thay đổi
- Mã giảm giá sắp hết hạn

**Công việc cần làm**:
- ❌ Tạo `Notification` entity
- ❌ Tạo `NotificationService`
- ❌ Implement email notifications (đã có Spring Mail setup)
- ❌ Implement SMS notifications (cần Twilio hoặc tương tự)
- ❌ Implement in-app notifications (WebSocket)
- ❌ Notification queue (RabbitMQ hoặc SQS)
- ❌ Notification preferences (user chọn nhận cái gì)
- ❌ Notification history & retry logic

**Lý do chưa bắt đầu**:
- **Phức tạp**: Cần tích hợp nhiều services (Email, SMS, WebSocket)
- **Chi phí**: SMS service (Twilio) tính phí per message
- **Ưu tiên thấp**: Email notifications cơ bản đã có

**Ước tính thời gian hoàn thành**: 2-3 tuần

---

### 3.3.3 Tổng hợp lý do khách quan

**Lý do chính khách quan (Objective Reasons)**:

1. **Thời gian phát triển hạn chế**
   - Dự án là bài tập lớn, có deadline cố định
   - Team nhỏ (1-2 developer)
   - Cần ưu tiên MVP (Minimum Viable Product) trước

2. **Độ phức tạp kỹ thuật cao**
   - SePay, VNPAY, Momo mỗi cái có flow khác nhau
   - AI Chatbot cần fine-tuning, không dễ đạt chất lượng tốt
   - Real-time features (Dashboard, Notifications) cần architecture phức tạp

3. **Phụ thuộc vào external services**
   - SePay, VNPAY, Google API cần đăng ký tài khoản
   - Phải test trên sandbox trước production
   - Không thể develop offline

4. **Yêu cầu kinh phí**
   - Các payment gateway tính phí transaction
   - Google Gemini API tính theo tokens
   - SMS service tính per message

5. **Tính cấp thiết**
   - MVP cần chức năng: tìm kiếm, giỏ hàng, thanh toán COD, đơn hàng
   - Các chức năng khác là enhancement, có thể làm sau

---

### 3.3.4 Roadmap phát triển tiếp theo

**Phase 1 (Hiện tại - MVP)**: 
- Tìm kiếm, danh mục, giỏ hàng
- Thanh toán COD
- Quản lý đơn hàng cơ bản
- **Hoàn thành**: Tháng 7/2026

**Phase 2 (Tháng 8/2026)**:
- ✓ Hoàn thành SePay
- ✓ Hoàn thành Admin Dashboard
- ✓ AI Chatbot cơ bản

**Phase 3 (Tháng 9-10/2026)**:
- ✓ VNPAY/Momo integration
- ✓ Analytics & Reports
- ✓ Notification System

**Phase 4 (Tương lai)**:
- ✓ Mobile app (React Native)
- ✓ Machine Learning recommendations
- ✓ Seller marketplace
- ✓ Social commerce features

---

## KẾT LUẬN CHƯƠNG III

Chương III đã trình bày chi tiết:

1. **Thiết kế CSDL**: 12 bảng chính với mối quan hệ rõ ràng, indexes, constraints để đảm bảo hiệu suất và toàn vẹn dữ liệu

2. **Thiết kế Chức năng**: 
   - Luồng Mua hàng & Thanh toán: Từ duyệt sách → giỏ hàng → checkout → thanh toán (COD/SePay)
   - Luồng Quản lý Sản phẩm: Admin thêm/sửa/xóa sách, quản lý danh mục, orders, vouchers

3. **Chức năng Chưa hoàn thiện**: 
   - SePay (50%), Chatbot (40%), Dashboard (30%) đang phát triển
   - VNPAY, Momo, Notifications, Analytics chưa bắt đầu
   - Lý do rõ ràng: thời gian hạn chế, độ phức tạp, phụ thuộc external services

Các thiết kế này sẽ là cơ sở cho implementation (Chương IV) và testing (Chương V).

