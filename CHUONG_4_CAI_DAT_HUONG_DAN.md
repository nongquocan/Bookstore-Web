# CHƯƠNG IV. CÀI ĐẶT VÀ HƯỚNG DẪN SỬ DỤNG

## 4.1 CÀI ĐẶT CƠ SỞ DỮ LIỆU

### 4.1.1 Yêu cầu hệ thống

**Phần cứng tối thiểu:**
- CPU: 2 cores 2 GHz trở lên
- RAM: 2 GB (khuyến nghị 4 GB)
- Disk: 20 GB (dành cho OS, MySQL, ứng dụng)
- Network: Kết nối Internet

**Phần mềm yêu cầu:**
- **Hệ điều hành**: Windows 10+, macOS 10.15+, Linux (Ubuntu 20.04+)
- **Database**: MySQL 8.0.x hoặc MariaDB 10.5+
- **Java**: JDK 21 LTS
- **Maven**: 3.6.0+ (để build ứng dụng)
- **Git**: 2.30+ (để clone repository)

**Tools hỗ trợ (Optional)**:
- MySQL Workbench (GUI để quản lý database)
- DBeaver Community (Database IDE)
- Postman (API testing)
- Ngrok (expose local server ra Internet)

---

### 4.1.2 Cài đặt MySQL

#### **A. Cài đặt trên Windows**

**Bước 1: Download MySQL Installer**
```
1. Truy cập https://dev.mysql.com/downloads/mysql/
2. Chọn "MySQL Community Server" → version 8.0.x
3. Chọn "Windows (x86, 64-bit), MSI Installer"
4. Download file: mysql-installer-community-8.0.x-winx64.msi
```

**Bước 2: Chạy MySQL Installer**
```
1. Double-click file msi
2. Chọn "Setup Type" → "Server only" (để cài chỉ database)
3. Chọn "Config Type" → "Development Machine" (1 server)
4. Cấu hình Port: 3306 (default)
5. Cấu hình Authentication: "Use Strong Password" (cơ chế cấp quyền mới)
6. Tạo tài khoản root: 
   - Username: root
   - Password: (set một mật khẩu mạnh)
7. Cấu hình Windows Service: "MySQL80" (khởi động tự động)
8. Chọn "Next" → "Finish"
```

**Bước 3: Xác thực cài đặt**
```bash
# Mở Command Prompt (cmd)
mysql --version
# Output: mysql  Ver 8.0.x for Win64 on x86_64

# Kết nối tới MySQL
mysql -u root -p
# Nhập mật khẩu root
# Output: mysql>
```

---

#### **B. Cài đặt trên macOS**

**Phương pháp 1: Dùng Homebrew (Khuyến nghị)**

```bash
# Cài đặt Homebrew (nếu chưa có)
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Cài đặt MySQL
brew install mysql@8.0

# Khởi động MySQL
brew services start mysql@8.0

# Xác thực
mysql --version
mysql -u root -p

# Lần đầu không có mật khẩu, nhấn Enter để login
```

**Phương pháp 2: Download DMG từ MySQL.com**
```
Tương tự Windows, download .dmg file từ dev.mysql.com
```

---

#### **C. Cài đặt trên Linux (Ubuntu)**

```bash
# Update package manager
sudo apt update
sudo apt upgrade

# Cài đặt MySQL Server
sudo apt install mysql-server -y

# Khởi động MySQL
sudo systemctl start mysql

# Bật tự động khởi động
sudo systemctl enable mysql

# Secure MySQL installation (đặt mật khẩu root, etc.)
sudo mysql_secure_installation
# Trả lời các câu hỏi:
# - Remove anonymous users? → Yes
# - Disable remote root login? → Yes
# - Remove test database? → Yes
# - Reload privilege tables? → Yes

# Xác thực
mysql --version
mysql -u root -p
```

---

### 4.1.3 Tạo Database & User

**Bước 1: Kết nối tới MySQL**

```bash
# Đăng nhập với quyền root
mysql -u root -p
# Nhập mật khẩu root
```

**Bước 2: Tạo Database cho BookStore**

```sql
-- Kiểm tra databases hiện có
SHOW DATABASES;

-- Tạo database bookstore
CREATE DATABASE bookstore_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- Xác thực database đã tạo
SHOW DATABASES;
-- Output: bookstore_db sẽ hiển thị trong danh sách
```

**Giải thích**:
- `CHARACTER SET utf8mb4`: Hỗ trợ ký tự Unicode (tiếng Việt, emoji, etc.)
- `COLLATE utf8mb4_unicode_ci`: Collation cho tìm kiếm không phân biệt chữ hoa/thường

**Bước 3: Tạo User cho ứng dụng**

```sql
-- Tạo user bookstore_user
CREATE USER 'bookstore_user'@'localhost' IDENTIFIED BY 'SecurePassword@123';

-- Cấp quyền toàn bộ cho database bookstore_db
GRANT ALL PRIVILEGES ON bookstore_db.* TO 'bookstore_user'@'localhost';

-- Áp dụng các quyền vừa cấp
FLUSH PRIVILEGES;

-- Xác thực user
SELECT user, host FROM mysql.user WHERE user = 'bookstore_user';
```

**Lưu ý về mật khẩu**:
- Mật khẩu phải đủ mạnh (ít nhất 8 ký tự, chứa số, chữ hoa, ký tự đặc biệt)
- Lưu mật khẩu vào file `application.properties` trong project

---

### 4.1.4 Tạo các bảng dữ liệu

**Bước 1: Chọn database bookstore_db**

```sql
USE bookstore_db;
```

**Bước 2: Chạy script SQL tạo bảng**

**File: `src/main/resources/database/init.sql`**

```sql
-- ============================================
-- ROLES TABLE
-- ============================================
CREATE TABLE roles (
    role_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_role_name (role_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert default roles
INSERT INTO roles (role_name, description) VALUES
    ('ADMIN', 'Quản trị viên hệ thống'),
    ('USER', 'Khách hàng thông thường');

-- ============================================
-- USERS TABLE
-- ============================================
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
    CHECK (LENGTH(password_hash) >= 60)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- USERS_ROLES JOIN TABLE (N:M)
-- ============================================
CREATE TABLE users_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- CATEGORIES TABLE
-- ============================================
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert sample categories
INSERT INTO categories (category_name, description, display_order) VALUES
    ('Lịch sử', 'Sách về lịch sử các nước', 0),
    ('Khoa học', 'Sách về khoa học tự nhiên', 1),
    ('Kỹ thuật', 'Sách về công nghệ và lập trình', 2),
    ('Tâm lý học', 'Sách về tâm lý, phát triển bản thân', 3),
    ('Văn học', 'Tiểu thuyết, thơ, các tác phẩm văn học', 4);

-- ============================================
-- BOOKS TABLE
-- ============================================
CREATE TABLE books (
    book_id VARCHAR(50) PRIMARY KEY,
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- VOUCHERS TABLE
-- ============================================
CREATE TABLE vouchers (
    voucher_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    voucher_code VARCHAR(50) NOT NULL UNIQUE,
    discount_type ENUM('percentage', 'fixed_amount') DEFAULT 'percentage',
    discount_value DECIMAL(10, 2) NOT NULL,
    min_order_amount DECIMAL(10, 2) DEFAULT 0,
    max_usage INT DEFAULT 999,
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- CART TABLE
-- ============================================
CREATE TABLE cart (
    cart_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    book_id VARCHAR(50) NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_book (user_id, book_id),
    CHECK (quantity > 0),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- ORDERS TABLE
-- ============================================
CREATE TABLE orders (
    order_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_code VARCHAR(50) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    status ENUM('pending', 'approved', 'shipped', 'delivered', 'cancelled') 
        DEFAULT 'pending',
    payment_method VARCHAR(50),
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- ORDER_ITEMS TABLE
-- ============================================
CREATE TABLE order_items (
    order_item_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    book_id VARCHAR(50) NOT NULL,
    quantity INT NOT NULL,
    price_per_unit DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(15, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE RESTRICT,
    INDEX idx_order_id (order_id),
    CHECK (quantity > 0),
    CHECK (price_per_unit > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- REVIEWS TABLE
-- ============================================
CREATE TABLE reviews (
    review_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    book_id VARCHAR(50) NOT NULL,
    user_id BIGINT NOT NULL,
    rating INT NOT NULL,
    comment TEXT,
    is_approved BOOLEAN DEFAULT FALSE,
    helpful_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_book (user_id, book_id),
    INDEX idx_book_id (book_id),
    INDEX idx_is_approved (is_approved),
    CHECK (rating >= 1 AND rating <= 5)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- WISHLIST TABLE
-- ============================================
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- HOME_SECTIONS TABLE
-- ============================================
CREATE TABLE home_sections (
    section_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    section_title VARCHAR(100),
    section_type VARCHAR(50),
    display_order INT NOT NULL,
    banner_image_url VARCHAR(500),
    banner_link_url VARCHAR(500),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY unique_display_order (display_order),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- AI_CHAT_HISTORY TABLE
-- ============================================
CREATE TABLE ai_chat_history (
    chat_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    session_id VARCHAR(100),
    user_message LONGTEXT NOT NULL,
    bot_response LONGTEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_session_id (session_id),
    INDEX idx_created_at (created_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Sample Admin User (Password: Admin@123)
-- ============================================
-- Hash generated by BCrypt (bcrypt@123: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36gZvQOm)
INSERT INTO users (email, password_hash, first_name, last_name, is_active)
VALUES ('admin@bookstore.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36gZvQOm', 'Admin', 'BookStore', TRUE);

-- Gán role ADMIN cho admin user (user_id = 1)
INSERT INTO users_roles (user_id, role_id) VALUES (1, 1);

-- ============================================
-- Xác thực
-- ============================================
SELECT 'Database initialization completed!' as status;
```

**Bước 3: Chạy script**

```bash
# Cách 1: Từ command line
mysql -u bookstore_user -p bookstore_db < src/main/resources/database/init.sql

# Cách 2: Từ MySQL shell
mysql -u root -p
USE bookstore_db;
SOURCE src/main/resources/database/init.sql;
```

**Bước 4: Xác thực cài đặt**

```sql
-- Kiểm tra các bảng đã được tạo
USE bookstore_db;
SHOW TABLES;

-- Output:
-- | Tables_in_bookstore_db |
-- |------------------------|
-- | ai_chat_history        |
-- | books                  |
-- | cart                   |
-- | categories             |
-- | home_sections          |
-- | order_items            |
-- | orders                 |
-- | reviews                |
-- | roles                  |
-- | users                  |
-- | users_roles            |
-- | vouchers               |
-- | wishlist               |

-- Kiểm tra dữ liệu sample
SELECT * FROM categories;
SELECT * FROM roles;
SELECT * FROM users;
SELECT COUNT(*) as admin_count FROM users WHERE user_id = 1;
```

---

## 4.2 CÀI ĐẶT VÀ CHẠY ỨNG DỤNG

### 4.2.1 Yêu cầu môi trường phát triển

**Software cần thiết:**

| Phần mềm | Phiên bản | Mục đích |
|---------|----------|---------|
| **Java JDK** | 21 LTS | Chạy ứng dụng Spring Boot |
| **Maven** | 3.6.0+ | Build project, quản lý dependencies |
| **MySQL** | 8.0.x | Database |
| **Git** | 2.30+ | Clone repository |
| **IDE (Optional)** | IntelliJ IDEA / VS Code | Phát triển code |

**Kiểm tra cài đặt:**

```bash
# Kiểm tra Java
java -version
# Output: openjdk version "21" (hoặc cao hơn)

# Kiểm tra Maven
mvn --version
# Output: Apache Maven 3.6.0 (hoặc cao hơn)

# Kiểm tra MySQL
mysql --version
# Output: mysql  Ver 8.0.x
```

---

### 4.2.2 Clone & Chuẩn bị Project

**Bước 1: Clone repository**

```bash
# Clone từ GitHub (nếu project ở trên GitHub)
git clone https://github.com/your-username/bookstore.git
cd bookstore

# Hoặc copy folder project từ USB/Cloud
```

**Bước 2: Kiểm tra cấu trúc project**

```
bookstore/
├── Backend-Java/                    # Backend Spring Boot
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/bookstore/  # Java source code
│   │   │   │   ├── BookStoreApplication.java
│   │   │   │   ├── config/          # Spring configurations
│   │   │   │   ├── controller/      # REST controllers
│   │   │   │   ├── service/         # Business logic
│   │   │   │   ├── repository/      # Data access
│   │   │   │   ├── entity/          # Entity models
│   │   │   │   └── exception/       # Custom exceptions
│   │   │   └── resources/
│   │   │       ├── application.properties  # Configuration file
│   │   │       └── database/
│   │   │           └── init.sql     # Database creation script
│   │   └── test/                    # Unit tests
│   ├── pom.xml                      # Maven configuration
│   └── target/                      # Build output (sau khi build)
├── Frontend/                        # Frontend HTML/CSS/JS
│   ├── index.html                   # Trang chủ
│   ├── pages/                       # Các trang
│   ├── assets/
│   │   ├── css/                     # Stylesheet
│   │   ├── js/                      # JavaScript files
│   │   └── images/                  # Hình ảnh
│   └── admin/                       # Admin dashboard
└── README.md
```

---

### 4.2.3 Cấu hình ứng dụng

**File: `Backend-Java/src/main/resources/application.properties`**

Sửa các cấu hình sau theo môi trường của bạn:

```properties
# ============================================
# Spring Boot Configuration
# ============================================
spring.application.name=BookStore Backend
server.port=8080
spring.devtools.restart.enabled=true

# ============================================
# Database Configuration
# ============================================
spring.datasource.url=jdbc:mysql://localhost:3306/bookstore_db
spring.datasource.username=bookstore_user
spring.datasource.password=SecurePassword@123
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# ============================================
# JPA / Hibernate Configuration
# ============================================
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true

# ============================================
# Jackson Configuration (JSON Serialization)
# ============================================
spring.jackson.serialization.write-dates-as-timestamps=false
spring.jackson.default-property-inclusion=non_null

# ============================================
# JWT Configuration
# ============================================
app.jwtSecret=bookstore-secret-key-for-jwt-token-generation-must-be-at-least-32-characters-long
app.jwtExpirationMs=86400000

# ============================================
# Frontend URL Configuration
# ============================================
app.frontendUrl=http://localhost:63342/Web-site/Frontend/pages

# ============================================
# Mail Configuration (Gmail SMTP)
# ============================================
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true

# ============================================
# Google Books API Configuration
# ============================================
google.books.api.url=https://www.googleapis.com/books/v1

# ============================================
# Google Gemini API Configuration
# ============================================
gemini.api.key=your-gemini-api-key-here

# ============================================
# Logging Configuration
# ============================================
logging.level.root=INFO
logging.level.com.bookstore=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %logger{36} - %msg%n
```

**Lưu ý quan trọng về cấu hình:**

| Tham số | Giải thích | Ví dụ |
|---------|-----------|-------|
| `spring.datasource.url` | Database connection string | `jdbc:mysql://localhost:3306/bookstore_db` |
| `spring.datasource.username` | DB user | `bookstore_user` |
| `spring.datasource.password` | DB password | Mật khẩu đã cấu hình |
| `server.port` | Port ứng dụng chạy | 8080 (có thể thay 8081, 9090, etc.) |
| `app.jwtSecret` | Secret key cho JWT | ≥32 ký tự, phức tạp |
| `spring.mail.username` | Gmail để gửi OTP | Gmail account |
| `spring.mail.password` | App password Gmail | Lấy từ Google Account Security |

---

### 4.2.4 Cài đặt Dependencies & Build

**Bước 1: Cài đặt Maven dependencies**

```bash
cd Backend-Java

# Download tất cả dependencies từ pom.xml
mvn clean install
# hoặc
mvn clean dependency:resolve

# Output:
# [INFO] BUILD SUCCESS
```

**Bước 2: Build ứng dụng (tạo JAR executable)**

```bash
# Tạo file JAR executable
mvn clean package -DskipTests

# Output:
# [INFO] Building jar: ./target/bookstore-api-1.0.0.jar
# [INFO] BUILD SUCCESS

# File JAR sẽ được tạo tại: target/bookstore-api-1.0.0.jar
```

**Bước 3: Xác thực build**

```bash
# Kiểm tra file JAR đã tạo
ls -lh target/bookstore-api-1.0.0.jar
# Output: bookstore-api-1.0.0.jar (khoảng 50-80 MB)
```

---

### 4.2.5 Chạy ứng dụng trên Local (Development)

#### **Phương pháp 1: Chạy từ IDE (IntelliJ IDEA)**

**Bước 1: Mở project**
```
File → Open... → Chọn folder Backend-Java
```

**Bước 2: Chọn JDK**
```
File → Project Structure → Project
SDK → Chọn JDK 21
```

**Bước 3: Run Application**
```
Click phải vào BookStoreApplication.java → Run 'BookStoreApplication.main()'
hoặc
Shift + F10 (Windows) / Ctrl + R (Mac)
```

**Output mong đợi:**
```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_|\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::        (v3.2.3)

2026-06-06 10:30:00 - com.bookstore.BookStoreApplication - Starting BookStoreApplication
2026-06-06 10:30:02 - com.bookstore.BookStoreApplication - Started BookStoreApplication in 2.345 seconds (JVM running for 3.567)
2026-06-06 10:30:03 - org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping - Mapped "GET /api/books" onto public ResponseEntity...
2026-06-06 10:30:03 - org.springframework.boot.web.embedded.tomcat.TomcatWebServer - Tomcat started on port(s): 8080 (http)

Application is ready!
```

**Xác thực ứng dụng chạy:**
```bash
# Mở trình duyệt, truy cập
http://localhost:8080/api/books

# Output: JSON danh sách sách
```

---

#### **Phương pháp 2: Chạy từ Command Line**

```bash
cd Backend-Java

# Cách 1: Chạy trực tiếp từ Maven
mvn spring-boot:run

# Cách 2: Chạy file JAR đã build
java -jar target/bookstore-api-1.0.0.jar

# Cách 3: Chạy JAR với cấu hình custom
java -Dserver.port=9090 \
     -Dspring.datasource.url=jdbc:mysql://localhost:3306/bookstore_db \
     -Dspring.datasource.username=bookstore_user \
     -Dspring.datasource.password=SecurePassword@123 \
     -jar target/bookstore-api-1.0.0.jar
```

---

### 4.2.6 Chạy Frontend trên Local

#### **Phương pháp 1: Dùng Live Server (VS Code Extension)**

```
1. Mở VS Code
2. File → Open Folder → Chọn Frontend
3. Chuột phải vào index.html → "Open with Live Server"
4. Trình duyệt tự mở tại http://localhost:5500
```

#### **Phương pháp 2: Dùng Python HTTP Server**

```bash
# Chuyển tới folder Frontend
cd Frontend

# Chạy HTTP server (Python 3)
python -m http.server 5500
# hoặc (Python 2)
python -m SimpleHTTPServer 5500

# Mở trình duyệt: http://localhost:5500
```

#### **Phương pháp 3: Dùng Node.js http-server**

```bash
# Cài đặt (chỉ cần lần đầu)
npm install -g http-server

# Chạy server
cd Frontend
http-server -p 5500

# Trình duyệt: http://localhost:5500
```

---

### 4.2.7 Deploy trên Tomcat Server

#### **A. Chuẩn bị Tomcat**

**Bước 1: Download Tomcat**
```
Truy cập: https://tomcat.apache.org/download-90.cgi
Download: apache-tomcat-9.0.x.zip hoặc .tar.gz
```

**Bước 2: Cài đặt Tomcat**

```bash
# Windows
unzip apache-tomcat-9.0.x.zip -d C:\tomcat

# macOS/Linux
tar -xzf apache-tomcat-9.0.x.tar.gz
mv apache-tomcat-9.0.x ~/tomcat
```

**Bước 3: Cấu hình Tomcat**

**File: `tomcat/conf/server.xml`** (sửa port nếu cần)

```xml
<Connector port="8080" protocol="HTTP/1.1"
           connectionTimeout="20000"
           redirectPort="8443" />
```

**File: `tomcat/conf/context.xml`** (cấu hình shared resources)

```xml
<Context>
    <!-- Database connection pool -->
    <Resource name="jdbc/bookstore" auth="Container"
              type="javax.sql.DataSource"
              driverClassName="com.mysql.cj.jdbc.Driver"
              url="jdbc:mysql://localhost:3306/bookstore_db"
              username="bookstore_user"
              password="SecurePassword@123"
              maxActive="20"
              maxIdle="10"
              maxWait="30000"/>
</Context>
```

---

#### **B. Deploy ứng dụng Spring Boot trên Tomcat**

**Bước 1: Chuyển đổi Spring Boot thành WAR file**

**File: `Backend-Java/pom.xml`** (thêm packaging)

```xml
<project>
    <!-- ... existing code ... -->
    
    <packaging>war</packaging>
    
    <!-- ... existing code ... -->
    
    <dependencies>
        <!-- Exclude embedded Tomcat từ Spring Boot -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <exclusions>
                <exclusion>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-tomcat</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        
        <!-- Add provided scope Tomcat -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-tomcat</artifactId>
            <scope>provided</scope>
        </dependency>
    </dependencies>
</project>
```

**Bước 2: Tạo main class cấu hình**

**File: `src/main/java/com/bookstore/BookStoreServletInitializer.java`**

```java
package com.bookstore;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

public class BookStoreServletInitializer extends SpringBootServletInitializer {
    
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(BookStoreApplication.class);
    }
}
```

**Bước 3: Build WAR file**

```bash
mvn clean package -DskipTests

# Output:
# [INFO] Building war: ./target/bookstore-api-1.0.0.war
# [INFO] BUILD SUCCESS
```

**Bước 4: Deploy WAR vào Tomcat**

```bash
# Copy WAR file vào Tomcat webapps
cp target/bookstore-api-1.0.0.war ~/tomcat/webapps/

# Tomcat sẽ tự extract WAR file vào folder:
# ~/tomcat/webapps/bookstore-api-1.0.0/
```

**Bước 5: Khởi động Tomcat**

```bash
# macOS/Linux
./tomcat/bin/startup.sh

# Windows
tomcat\bin\startup.bat

# Output log:
# [catalina.out hoặc console window]
# ... Tomcat startup logs ...
# Server startup in ... ms
```

**Bước 6: Xác thực deployment**

```bash
# Truy cập ứng dụng
http://localhost:8080/bookstore-api-1.0.0/api/books

# Xem logs
tail -f ~/tomcat/logs/catalina.out
```

---

### 4.2.8 Deploy trên Cloud (AWS EC2 - Optional)

#### **A. Chuẩn bị EC2 Instance**

```bash
# SSH vào EC2 instance
ssh -i your-key.pem ec2-user@your-instance-ip

# Update system
sudo yum update -y

# Cài Java 21
sudo yum install java-21-amazon-corretto -y

# Cài MySQL client
sudo yum install mysql -y

# Cài Maven (optional)
sudo yum install maven -y
```

#### **B. Upload & chạy JAR**

```bash
# Copy JAR lên EC2
scp -i your-key.pem target/bookstore-api-1.0.0.jar \
    ec2-user@your-instance-ip:/home/ec2-user/

# SSH vào EC2 & run
ssh -i your-key.pem ec2-user@your-instance-ip

# Chạy ứng dụng
java -Dspring.datasource.url=jdbc:mysql://your-db-host:3306/bookstore_db \
     -Dspring.datasource.username=bookstore_user \
     -Dspring.datasource.password=SecurePassword@123 \
     -jar bookstore-api-1.0.0.jar &

# Hoặc chạy với nohup (tiếp tục chạy sau khi logout)
nohup java -jar bookstore-api-1.0.0.jar > app.log 2>&1 &
```

#### **C. Cấu hình Security Group**

```
AWS Console → EC2 → Security Groups
Inbound Rules:
  - Port 8080 (HTTP): 0.0.0.0/0 (allow all)
  - Port 443 (HTTPS): 0.0.0.0/0
  - Port 22 (SSH): Your IP
```

---

### 4.2.9 Kiểm tra & Testing

**Bước 1: Health Check**

```bash
# Kiểm tra ứng dụng sống
curl http://localhost:8080/actuator/health

# Output:
# {"status":"UP","database":"UP","diskSpace":{"status":"UP"}}
```

**Bước 2: Test API endpoints**

```bash
# Lấy danh sách sách
curl http://localhost:8080/api/books

# Đăng nhập
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@bookstore.com","password":"Admin@123"}'

# Lấy categories
curl http://localhost:8080/api/categories
```

**Bước 3: Test Database connection**

```bash
# Kiểm tra kết nối database từ ứng dụng
curl http://localhost:8080/api/admin/stats
# Nếu kết nối database OK, sẽ return JSON stats
```

**Bước 4: Test Frontend**

```
1. Mở trình duyệt: http://localhost:5500
2. Điều hướng qua các trang:
   - http://localhost:5500/index.html (Trang chủ)
   - http://localhost:5500/pages/products.html (Danh sách sách)
   - http://localhost:5500/pages/cart.html (Giỏ hàng)
   - http://localhost:5500/pages/login.html (Đăng nhập)
3. Kiểm tra console (F12 → Console) để xem errors
```

---

### 4.2.10 Cấu hình môi trường đa tầng (Multi-environment)

**Tạo các profile cấu hình khác nhau:**

**File: `application-dev.properties`** (Development)
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/bookstore_db
spring.datasource.username=bookstore_user
spring.datasource.password=SecurePassword@123
server.port=8080
logging.level.root=DEBUG
```

**File: `application-prod.properties`** (Production)
```properties
spring.datasource.url=jdbc:mysql://db-prod.example.com:3306/bookstore_db
spring.datasource.username=prod_user
spring.datasource.password=${DB_PASSWORD}  # Lấy từ environment variable
server.port=8080
logging.level.root=WARN
spring.jpa.hibernate.ddl-auto=validate
```

**File: `application-test.properties`** (Testing)
```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=create-drop
server.port=8081
```

**Chạy với profile cụ thể:**
```bash
# Development
java -Dspring.profiles.active=dev -jar bookstore-api-1.0.0.jar

# Production
java -Dspring.profiles.active=prod -jar bookstore-api-1.0.0.jar

# Test
java -Dspring.profiles.active=test -jar bookstore-api-1.0.0.jar
```

---

## 4.3 TROUBLESHOOTING - CÁC LỖI THƯỜNG GẶP

| Lỗi | Nguyên nhân | Cách khắc phục |
|-----|-----------|--------------|
| **"Connection refused" khi kết nối DB** | MySQL chưa chạy hoặc port sai | Khởi động MySQL: `sudo systemctl start mysql` |
| **"Access denied for user 'bookstore_user'"** | Mật khẩu sai trong application.properties | Kiểm tra mật khẩu trong `application.properties` |
| **"Port 8080 already in use"** | Port 8080 đang được dùng | Thay đổi port: `java -Dserver.port=9090 -jar ...jar` |
| **"Cannot find file init.sql"** | Đường dẫn file SQL sai | Kiểm tra: `src/main/resources/database/init.sql` |
| **"401 Unauthorized" trên API** | JWT token hết hạn hoặc sai | Đăng nhập lại để lấy token mới |
| **"CORS error" trên Frontend** | Frontend origin không được phép | Cấu hình CORS trong `SecurityConfig.java` |
| **"Cannot resolve symbol 'Entity'"** | IDE chưa cập nhật Maven cache | Chạy `mvn clean install` hoặc rebuild IDE |

---

## KẾT LUẬN CHƯƠNG IV

Chương IV đã hướng dẫn chi tiết:

1. **Cài đặt CSDL**:
   - Cài đặt MySQL trên Windows/macOS/Linux
   - Tạo database `bookstore_db` & user `bookstore_user`
   - Chạy script SQL tạo 12 bảng với data sample

2. **Cài đặt ứng dụng**:
   - Cấu hình `application.properties`
   - Build project với Maven
   - Chạy trên Local (IDE hoặc Command Line)
   - Deploy trên Tomcat hoặc AWS EC2

3. **Testing & Troubleshooting**:
   - Health check API
   - Test endpoints với curl
   - Xử lý lỗi thường gặp
   - Cấu hình multi-environment

Người dùng có thể follow các hướng dẫn này để cài đặt và chạy hệ thống BookStore trên bất kỳ máy tính nào.

