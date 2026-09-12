# CHƯƠNG II. KIẾN THỨC ÁP DỤNG

## 2.1. PHÂN TÍCH VÀ THIẾT KẾ HỆ THỐNG

### 2.1.1 Biểu đồ Phân cấp Chức năng (BFD - Business Function Decomposition)

#### A. Khái niệm Biểu đồ Phân cấp Chức năng

Biểu đồ Phân cấp Chức năng (BFD) là một công cụ phân tích hệ thống được sử dụng để mô tả chi tiết các chức năng của một hệ thống thông tin từ cấp độ tổng quát (Level 0) đến cấp độ chi tiết (Level n). Trong ngữ cảnh phát triển hệ thống thương mại điện tử, BFD giúp phân rã yêu cầu kinh doanh thành các chức năng cụ thể, từ đó hỗ trợ việc thiết kế kiến trúc ứng dụng và phân bổ công việc phát triển.

BFD tuân theo nguyên tắc phân cấp từ trên xuống (Top-Down):
- **Level 0 (Context)**: Hệ thống được xem như một hộp đen duy nhất thực hiện một chức năng lớn
- **Level 1 (Overview)**: Phân rã thành các chức năng chính của hệ thống
- **Level 2+**: Tiếp tục phân rã chi tiết các chức năng con

#### B. Biểu đồ Phân cấp Chức năng cho Hệ thống BookStore

**Level 0 - Context: Hệ thống Quản lý Bán sách Trực tuyến**
```
┌─────────────────────────────────────────┐
│   Hệ thống BookStore TMĐT              │
│   (Website Bán Sách Trực tuyến)        │
│                                        │
│   Quản lý & Bán hàng Online           │
└─────────────────────────────────────────┘
        ↓  Input: Dữ liệu người dùng
        ↑  Output: Đơn hàng, Báo cáo
```

**Level 1 - Main Functions (Chức năng Chính)**

Hệ thống BookStore được chia thành **5 chức năng chính**:

```
                    BOOKSTORE SYSTEM
                          │
        ┌─────────────────┼─────────────────┐
        │                 │                 │
    ┌────────────┐  ┌──────────────┐  ┌─────────────┐
    │   QUẢN LÝ  │  │   QUẢN LÝ    │  │   QUẢN LÝ   │
    │  NGƯỜI     │  │    SẢN       │  │   THANH     │
    │   DÙNG     │  │   PHẨM       │  │    TOÁN     │
    └────────────┘  └──────────────┘  └─────────────┘
        │                 │                 │
        ↓                 ↓                 ↓
    - Đăng ký        - Tìm kiếm       - Giỏ hàng
    - Đăng nhập      - Xem chi tiết   - Thanh toán
    - Quản lý info   - Phân loại      - COD
    - Phân quyền     - Xếp hạng       - Chuyển khoản

    
    ┌──────────────┐  ┌──────────────────────┐
    │  QUẢN LÝ     │  │   QUẢN LÝ NỘI DUNG   │
    │  ĐƠN HÀNG    │  │   & TƯƠNG TÁC        │
    └──────────────┘  └──────────────────────┘
        │                 │
        ↓                 ↓
    - Xem đơn hàng   - Bình luận
    - Theo dõi       - Wishlist
    - Hủy đơn        - Chat AI
    - Lịch sử        - Giới thiệu
```

**Level 2 - Chức năng Chi tiết**

#### **F1. QUẢN LÝ NGƯỜI DÙNG (User Management)**
Mục đích: Quản lý tài khoản khách hàng và phân quyền truy cập

| Chức năng | Mô tả Chi tiết |
|-----------|----------------|
| **F1.1 Xác thực Người dùng** | - Đăng ký tài khoản mới với email, mật khẩu, thông tin cá nhân<br>- Xác minh email qua OTP (One-Time Password)<br>- Đăng nhập bằng email & mật khẩu<br>- Khôi phục mật khẩu quên |
| **F1.2 Quản lý Hồ sơ Cá nhân** | - Xem & chỉnh sửa thông tin: tên, SĐT, địa chỉ<br>- Tải lên ảnh đại diện<br>- Quản lý địa chỉ giao hàng mặc định<br>- Thay đổi mật khẩu |
| **F1.3 Quản lý Phân quyền** | - Phân quyền Admin/User<br>- Quản lý quyền truy cập tính năng admin<br>- Quản lý danh sách người dùng (Admin) |
| **F1.4 Bảo mật Phiên đăng nhập** | - Sinh token JWT khi đăng nhập<br>- Xác thực JWT trong mỗi request<br>- Kiểm tra quyền hạn dựa trên roles<br>- Logout & xóa phiên |

#### **F2. QUẢN LÝ SẢN PHẨM (Product Management)**
Mục đích: Quản lý danh mục, sản phẩm và thông tin liên quan

| Chức năng | Mô tả Chi tiết |
|-----------|----------------|
| **F2.1 Tìm kiếm & Lọc Sách** | - Tìm kiếm theo từ khóa (tiêu đề, tác giả)<br>- Lọc theo danh mục, giá, đánh giá<br>- Sắp xếp theo giá, mới nhất, phổ biến<br>- Phân trang kết quả |
| **F2.2 Xem Chi tiết Sách** | - Hiển thị thông tin sách: tên, tác giả, nhà xuất bản<br>- Hiển thị giá, số lượng tồn kho<br>- Lấy bìa sách từ Google Books API<br>- Hiển thị mô tả, năm xuất bản<br>- Hiển thị đánh giá & bình luận |
| **F2.3 Quản lý Danh mục** | - Xem danh sách danh mục<br>- Tạo/Sửa/Xóa danh mục (Admin)<br>- Gán sách vào danh mục<br>- Tính số sách trong mỗi danh mục |
| **F2.4 Quản lý Banner & HomeSection** | - Tạo/Sửa/Xóa banner quảng cáo<br>- Quản lý vị trí hiển thị trên trang chủ<br>- Quản lý sách nổi bật (Featured products) |
| **F2.5 Tích hợp Google Books API** | - Tìm kiếm sách từ Google Books<br>- Lấy thông tin sách (metadata)<br>- Lấy hình ảnh bìa sách<br>- Cập nhật giá từ nguồn ngoài |

#### **F3. QUẢN LÝ THANH TOÁN (Payment Management)**
Mục đích: Xử lý các phương thức thanh toán và quản lý giao dịch

| Chức năng | Mô tả Chi tiết |
|-----------|----------------|
| **F3.1 Quản lý Giỏ hàng** | - Thêm/Xóa sách vào giỏ<br>- Thay đổi số lượng sách<br>- Tính tổng giá<br>- Lưu giỏ hàng tạm thời<br>- Xóa toàn bộ giỏ |
| **F3.2 Áp dụng Mã khuyến mãi** | - Xác thực mã voucher<br>- Tính toán giảm giá<br>- Kiểm tra điều kiện sử dụng<br>- Lưu lịch sử sử dụng voucher |
| **F3.3 Phương thức Thanh toán** | - **COD** (Cash on Delivery): Thanh toán khi nhận hàng<br>- **Thanh toán trực tuyến**: Tích hợp cổng thanh toán<br>- **SePay Webhook**: Nhận thông báo thanh toán<br>- Cập nhật trạng thái thanh toán |
| **F3.4 Xử lý Đơn hàng Thanh toán** | - Tạo hóa đơn/mã đơn hàng<br>- Tính tiền ship, phí xử lý<br>- Cập nhật trạng thái thanh toán<br>- Lưu lịch sử giao dịch |

#### **F4. QUẢN LÝ ĐƠN HÀNG (Order Management)**
Mục đích: Theo dõi vòng đời đơn hàng từ tạo đến giao

| Chức năng | Mô tả Chi tiết |
|-----------|----------------|
| **F4.1 Tạo Đơn Hàng** | - Xác nhận thông tin khách hàng<br>- Chọn địa chỉ giao hàng<br>- Kiểm tra tồn kho<br>- Tạo mã đơn hàng duy nhất<br>- Ghi lưu đơn hàng vào cơ sở dữ liệu |
| **F4.2 Xem & Theo dõi Đơn Hàng** | - Xem danh sách đơn hàng của user<br>- Xem chi tiết từng đơn hàng<br>- Theo dõi trạng thái: chờ duyệt → đã duyệt → đang giao → đã giao → hủy<br>- Xem lịch sử thay đổi trạng thái |
| **F4.3 Quản lý Đơn hàng (Admin)** | - Duyệt/Từ chối đơn hàng<br>- Cập nhật trạng thái đơn hàng<br>- Xem báo cáo doanh số<br>- Xem các đơn hàng chưa giao |
| **F4.4 Hủy & Hoàn Lại** | - Hủy đơn hàng nếu chưa duyệt<br>- Xử lý hoàn trả tiền<br>- Cập nhật lại tồn kho sách |
| **F4.5 Lịch sử Đơn Hàng** | - Lưu lại tất cả đơn hàng quá khứ<br>- Xem thống kê chi tiêu theo tháng<br>- Xuất báo cáo đơn hàng |

#### **F5. QUẢN LÝ NỘI DUNG & TƯƠNG TÁC (Content & Interaction Management)**
Mục đích: Tạo cộng đồng và tăng tương tác người dùng

| Chức năng | Mô tả Chi tiết |
|-----------|----------------|
| **F5.1 Đánh giá & Bình luận** | - Để lại đánh giá sao (1-5 sao)<br>- Viết bình luận về sách<br>- Xem bình luận từ người dùng khác<br>- Admin duyệt/xóa bình luận không phù hợp |
| **F5.2 Wishlist (Danh sách yêu thích)** | - Thêm sách vào wishlist<br>- Xóa sách khỏi wishlist<br>- Xem danh sách wishlist cá nhân<br>- Chuyển sách từ wishlist vào giỏ hàng |
| **F5.3 Chatbot AI Tư vấn** | - Trả lời câu hỏi về sách<br>- Đề xuất sách dựa trên sở thích<br>- Hỗ trợ khách hàng 24/7<br>- Sử dụng Google Gemini API để xử lý ngôn ngữ tự nhiên |
| **F5.4 Hệ thống Giới thiệu** | - Chia sẻ sách cho bạn bè<br>- Tạo liên kết giới thiệu<br>- Theo dõi người được giới thiệu<br>- Tính reward/điểm giới thiệu |

---

### 2.1.2 Biểu đồ Luồng Dữ liệu (DFD - Data Flow Diagram)

#### A. Khái niệm Biểu đồ Luồng Dữ liệu

Biểu đồ Luồng Dữ liệu (DFD) là một công cụ để mô tả cách dữ liệu chuyển động qua một hệ thống thông tin. DFD mô tả:
- **Entities (Thực thể)**: Người dùng, hệ thống bên ngoài
- **Processes (Quy trình)**: Các hoạt động xử lý dữ liệu
- **Data Stores (Kho dữ liệu)**: Cơ sở dữ liệu, file
- **Data Flows (Luồng dữ liệu)**: Chuyển động dữ liệu giữa các phần tử

Trong hệ thống BookStore, DFD được sử dụng để mô tả luồng dữ liệu từ lúc khách hàng truy cập website cho đến khi hoàn thành thanh toán.

#### B. DFD Mức Ngữ cảnh (Context Level - Level 0)

**Mô tả**: Hệ thống BookStore được xem như một hộp đen duy nhất nhận input từ khách hàng và admin, xuất output là đơn hàng, báo cáo, và thông tin sách.

```
┌──────────────┐                                    ┌──────────────┐
│   KHÁCH      │                                    │    ADMIN     │
│   HÀNG       │                                    │              │
└────────┬─────┘                                    └──────┬───────┘
         │                                                 │
         │ Yêu cầu tìm kiếm,                             │ Dữ liệu quản lý
         │ Thêm giỏ hàng,                                │ (Sản phẩm,
         │ Thanh toán,                                   │  Voucher,
         │ Xem đơn hàng                                  │  Banner)
         │                                                │
         │        ┌──────────────────────────┐           │
         │        │   BOOKSTORE SYSTEM       │           │
         ├────────│   (Hệ thống Website)     │◄──────────┤
         │        │                          │           │
         │        │   - Xử lý đơn hàng      │           │
         │        │   - Tìm kiếm sách      │           │
         │        │   - Xác thực user      │           │
         │        │   - Quản lý thanh toán │           │
         │        └────────────┬───────────┘            │
         │                     │                         │
         │                     │ Danh sách sách,        │
         │                     │ Đơn hàng,              │
         │                     │ Trạng thái thanh toán, │
         │                     │ Báo cáo                │
         │                     │                         │
         ▼                     ▼                         ▼
    ┌──────────────────────────────────────────────────────────┐
    │              CƠSỞ DỮ LIỆU MYSQL (Database)               │
    │  - Bảng Users, Books, Orders, OrderItems, Cart...       │
    └──────────────────────────────────────────────────────────┘
    
    Ngoài ra:
    - Tích hợp Google Books API để lấy dữ liệu sách
    - Tích hợp Google Gemini API cho Chatbot AI
    - Tích hợp SePay Webhook để nhận thông báo thanh toán
```

#### C. DFD Mức 1 - Tổng Quan Các Quy trình Chính

**Mô tả chi tiết luồng dữ liệu cho các quy trình chính**:

```
                          KHÁCH HÀNG
                              │
                              ▼
                    ┌──────────────────┐
                    │  P1: XÁC THỰC    │
                    │   NGƯỜI DÙNG     │
                    │ (Authentication) │
                    └────────┬─────────┘
                             │ JWT Token
                             │ (Nếu hợp lệ)
                             ▼
    ─────────────────────────────────────────────────────────
    │                                                       │
    ▼                                                       ▼
┌──────────────────┐                            ┌───────────────────┐
│  P2: QUẢN LÝ     │                            │  P3: QUẢN LÝ      │
│  SẢN PHẨM        │                            │  ĐƠN HÀNG         │
│                  │                            │                   │
│ - Tìm kiếm       │                            │ - Tạo đơn hàng    │
│ - Xem chi tiết   │                            │ - Xem tình trạng  │
│ - Lọc/Sắp xếp    │                            │ - Hủy đơn hàng    │
└────────┬─────────┘                            └────────┬──────────┘
         │                                               │
         │                                               │
         ▼                                               ▼
    ┌──────────────┐                            ┌──────────────┐
    │  P4: THANH   │                            │  D1: TBL      │
    │  TOÁN & GIỎ  │                            │  ORDERS       │
    │  HÀNG        │                            │  (Đơn hàng)   │
    │              │                            │               │
    │ - Thêm giỏ   │                            │ ID, User,     │
    │ - Xóa giỏ    │                            │ Total, Status │
    │ - Áp Voucher │                            │ PaymentStatus │
    │ - Thanh toán │────────────────────────────│               │
    └────────┬─────┘                            └───────────────┘
             │
             │ Payment Method
             │ (COD, Online)
             ▼
    ┌──────────────────┐
    │ P5: WEBHOOK      │  ← (Khi dùng thanh toán online)
    │ THANH TOÁN       │
    │ (SePay Webhook)  │
    │                  │
    │ - Nhận thông báo │
    │ - Cập nhật status│
    └────────┬─────────┘
             │
             ▼
    ┌──────────────┐
    │ D2: TBL CART │
    │ (Giỏ hàng)   │
    │              │
    │ UserID,      │
    │ BookID,      │
    │ Quantity     │
    └──────────────┘
    
    Ngoài ra:
    D3: Users (Người dùng)
    D4: Books (Sách)
    D5: Vouchers (Mã giảm giá)
    D6: Reviews (Đánh giá)
    D7: Wishlist (Danh sách yêu thích)
```

#### D. DFD Mức 2 - Chi tiết Quy trình P3 (Quản lý Đơn hàng)

Để minh họa chi tiết hơn, chúng ta mô tả chi tiết quy trình tạo và xử lý đơn hàng:

```
KHÁCH HÀNG
    │
    ├─ Thông tin giao hàng ──────┐
    ├─ Danh sách sách cần mua ─┐ │
    └─ Phương thức thanh toán ──┤─┤
                               │ │
                               ▼ ▼
                        ┌────────────────────┐
                        │ P3.1: TẠO ĐƠN      │
                        │ (Create Order)     │
                        └────────┬───────────┘
                                 │
         ────────────────────────┼─────────────────────────
         │                       │                         │
         ▼                       ▼                         ▼
    ┌───────────────┐   ┌───────────────┐     ┌──────────────────┐
    │ P3.1.1:       │   │ P3.1.2:       │     │ P3.1.3:          │
    │ KIỂM TRA      │   │ TÍNH TIỀN     │     │ SINH MÃ ĐƠN      │
    │ TỒN KHO       │   │ (Calculate)   │     │ (Generate Code)  │
    │               │   │               │     │                  │
    │ - Lấy số lượng│   │ - Giá gốc    │     │ - UUID/Timestamp │
    │   tồn kho     │   │ - Giảm giá    │     │ - Unique code    │
    │ - So sánh     │   │ - Tiền ship   │     │                  │
    │ - Bị từ chối  │   │ - Tổng cộng   │     │                  │
    │   nếu không   │   │               │     │                  │
    │   đủ          │   │               │     │                  │
    └───────┬───────┘   └───────┬───────┘     └────────┬─────────┘
            │                   │                      │
            └─────────────┬─────────────────────────────┘
                          │
                          ▼
                    ┌─────────────────────┐
                    │ P3.1.4: LƯU ĐƠN     │
                    │ (Save Order)        │
                    │                     │
                    │ INSERT INTO Orders  │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────────┐
                    │ D1: TBL ORDERS          │
                    │                         │
                    │ OrderID, OrderCode,     │
                    │ UserID, OrderDate,      │
                    │ TotalAmount, Status,    │
                    │ PaymentStatus,          │
                    │ PaymentMethod,          │
                    │ ShippingAddress,        │
                    │ CreatedAt, UpdatedAt    │
                    └────────────┬────────────┘
                                 │
                                 ▼
                            [ĐƠN HÀY ĐƯỢC
                             TẠO THÀNH CÔNG]
```

#### E. DFD Mức 2 - Chi tiết Quy trình P1 (Xác thực Người dùng)

```
NGƯỜI DÙNG
    │
    ├─ Email ─────────────┐
    └─ Mật khẩu──────────┤
                         │
                         ▼
                ┌──────────────────────┐
                │ P1.1: XÁC NHẬN       │
                │ THÔNG TIN ĐĂNG NHẬP  │
                │ (Validate Credentials)
                └────────┬─────────────┘
                         │
      ───────────────────┼──────────────────
      │                  │                  │
      ▼                  ▼                  ▼
  ┌─────────┐      ┌──────────┐     ┌──────────────┐
  │ P1.1.1: │      │ P1.1.2:  │     │ P1.1.3:      │
  │ KIỂM TRA│      │ KIỂM TRA │     │ SO SÁNH MẬT  │
  │ EMAIL   │      │ TRẠNG    │     │ KHẨU (Hash)  │
  │ TỒN TẠI │      │ THÁI TÀI │     │              │
  │         │      │ KHOẢN    │     │ - Lấy hash   │
  │ - Query │      │          │     │   từ DB      │
  │ D3      │      │ - Active?│     │ - So sánh    │
  │ - Tìm   │      │ - Banned?│     │   bcrypt     │
  │ có/không│      │ - Verify?│     │              │
  └────┬────┘      └────┬─────┘     └──────┬───────┘
       │                │                  │
       └────────────────┼──────────────────┘
                        │
                   (Tất cả OK?)
                        │
         ───────────────┼──────────────────
         │              │                  │
        KHÔNG          CÓ                  │
         │              │                  │
         ▼              ▼                  │
    [LỖI: EMAIL    ┌─────────────┐        │
     KHÔNG TỒN      │ P1.2: SINH  │        │
     TẠI / MẬT      │ JWT TOKEN   │        │
     KHẨU SAI /     │             │        │
     TÀI KHOẢN      │ - Payload:  │        │
     KHÔNG HỢP      │   Email,    │        │
     LỆ]            │   Roles,    │        │
                    │   Exp       │        │
                    │ - Secret:   │        │
                    │   Lấy từ    │        │
                    │   Config    │        │
                    │ - Ký JWT    │        │
                    └──────┬──────┘        │
                           │              │
                           ▼              │
                    ┌──────────────┐      │
                    │ D3: TBL USERS│      │
                    │              │      │
                    │ ID, Email,   │      │
                    │ PasswordHash,│      │
                    │ Roles,       │      │
                    │ IsActive     │      │
                    └──────────────┘      │
                                         │
                    [TRẢ TOKEN CHO CLIENT]
```

#### F. DFD Mức 2 - Chi tiết Quy trình P2 (Tìm kiếm & Lọc Sách)

```
KHÁCH HÀNG
    │
    ├─ Từ khóa tìm kiếm ─────────┐
    ├─ Bộ lọc (Category, Price) ─┤
    └─ Sắp xếp (Sort) ───────────┤
                                 │
                                 ▼
                        ┌──────────────────────┐
                        │ P2.1: PHÂN TÍCH      │
                        │ YÊU CẦU TÌM KIẾM     │
                        │ (Parse Search Query) │
                        └────────┬─────────────┘
                                 │
                                 ▼
                        ┌──────────────────────┐
                        │ P2.2: TRUY VẤN       │
                        │ CSDL (Query DB)      │
                        │                      │
                        │ SELECT * FROM Books  │
                        │ WHERE title LIKE?    │
                        │ AND category_id = ?  │
                        │ AND price BETWEEN ?  │
                        │ ORDER BY ?           │
                        │ LIMIT OFFSET         │
                        └────────┬─────────────┘
                                 │
                                 ▼
                        ┌──────────────────────┐
                        │ D4: TBL BOOKS        │
                        │                      │
                        │ BookID, Title,       │
                        │ Authors, Publisher,  │
                        │ Price, Category,     │
                        │ Description,         │
                        │ CoverImageUrl,       │
                        │ Rating               │
                        └────────┬─────────────┘
                                 │
                                 ▼
                        ┌──────────────────────┐
                        │ P2.3: ĐỊNH DẠNG      │
                        │ KẾT QUẢ              │
                        │ (Format Results)     │
                        │                      │
                        │ - Làm sạch dữ liệu   │
                        │ - Thêm rating        │
                        │ - Phân trang (Paging)│
                        │ - JSON Response      │
                        └────────┬─────────────┘
                                 │
                                 ▼
                        [TRẢ DANH SÁCH SÁCH
                         CHO CLIENT]
```

#### G. Tóm tắt Cấu trúc DFD BookStore

**Các thực thể (Entities):**
- Khách hàng
- Admin
- Google Books API (hệ thống bên ngoài)
- Google Gemini API (hệ thống bên ngoài)
- SePay Payment Gateway (hệ thống bên ngoài)

**Các quy trình chính (Processes):**
- P1: Xác thực người dùng
- P2: Tìm kiếm & quản lý sản phẩm
- P3: Tạo & quản lý đơn hàng
- P4: Thanh toán & giỏ hàng
- P5: Webhook xử lý thanh toán

**Các kho dữ liệu (Data Stores):**
- D1: Bảng Users
- D2: Bảng Books
- D3: Bảng Orders
- D4: Bảng OrderItems
- D5: Bảng Cart
- D6: Bảng Vouchers
- D7: Bảng Reviews
- D8: Bảng Wishlist
- D9: Bảng Categories
- D10: Bảng Roles

---

## 2.2. QUẢN TRỊ HỆ THỐNG (System Administration)

### 2.2.1 Khái niệm Quản trị Hệ thống

Quản trị hệ thống trong ngữ cảnh ứng dụng web TMĐT bao gồm việc quản lý tài khoản người dùng, phân quyền truy cập, bảo mật phiên đăng nhập, và kiểm soát quyền hạn cho các chức năng nhạy cảm.

### 2.2.2 Phân quyền (Authorization & Access Control)

#### A. Vai trò (Roles) và Quyền hạn (Permissions)

Hệ thống BookStore sử dụng mô hình **Role-Based Access Control (RBAC)** để quản lý quyền hạn:

**Bảng Vai trò (Roles):**

| Vai trò | Mô tả | Quyền hạn |
|---------|-------|-----------|
| **ADMIN** | Quản trị viên hệ thống | - Quản lý toàn bộ sản phẩm, danh mục<br>- Quản lý tất cả đơn hàng<br>- Quản lý người dùng<br>- Quản lý vouchers, banners<br>- Xem báo cáo doanh số<br>- Quản lý HomeSection |
| **USER** | Khách hàng thông thường | - Xem sách, giỏ hàng<br>- Tạo đơn hàng<br>- Xem đơn hàng của mình<br>- Đánh giá, bình luận sách<br>- Quản lý wishlist<br>- Chat với AI chatbot |

#### B. Cấu trúc Bảng Phân quyền (Roles Table)

```java
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String name;  // "ADMIN", "USER"
    
    @Column
    private String description;
    
    @Column
    private LocalDateTime createdAt;
}
```

#### C. Mối quan hệ giữa User và Role

```
┌────────────────────┐          ┌──────────────────┐
│     Users          │          │      Roles       │
├────────────────────┤          ├──────────────────┤
│ ID (PK)            │          │ ID (PK)          │
│ Email              │          │ Name             │
│ PasswordHash       │          │ Description      │
│ FirstName          │          │ CreatedAt        │
│ LastName           │          └──────────────────┘
│ IsActive           │                    ▲
│ CreatedAt          │                    │
│ UpdatedAt          │          ┌─────────┴────────┐
└────────────────────┘          │ ManyToMany (JPA) │
        │                       │ users_roles      │
        │                       │ (Bảng trung gian)│
        └───────────────────────┴──────────────────┘
                User có thể có nhiều Roles
                Role có thể được gán cho nhiều Users
```

#### D. Cấp quyền hạn dựa trên Endpoint (Method-level Security)

Hệ thống sử dụng Spring Security với annotation `@PreAuthorize` để bảo vệ các endpoint:

**Ví dụ cấu hình bảo mật:**

```java
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
    // Chỉ ADMIN mới có thể tạo danh mục
    @PostMapping("/categories")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createCategory(@RequestBody CategoryDTO dto) {
        // ...
    }
    
    // Chỉ ADMIN mới có thể cập nhật voucher
    @PutMapping("/vouchers/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateVoucher(@PathVariable Long id, 
                                          @RequestBody VoucherDTO dto) {
        // ...
    }
    
    // Các user đã xác thực mới có thể tạo đơn hàng
    @PostMapping("/orders/create")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createOrder(@RequestBody OrderDTO dto) {
        // ...
    }
}
```

**Cấu hình bảo mật tổng thể (SecurityConfig):**

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                // Public endpoints - cho phép tất cả truy cập
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/books/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                
                // Admin endpoints - chỉ ADMIN
                .requestMatchers(HttpMethod.POST, "/api/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/admin/**").hasRole("ADMIN")
                
                // User endpoints - yêu cầu xác thực
                .requestMatchers("/api/orders/create").authenticated()
                
                // Các yêu cầu khác cần xác thực
                .anyRequest().authenticated()
            );
        return http.build();
    }
}
```

### 2.2.3 Bảo mật Phiên đăng nhập (Session & JWT)

#### A. Khái niệm JWT (JSON Web Token)

JWT là một chuẩn công khai (RFC 7519) để tạo các token được ký để truyền tải thông tin một cách an toàn giữa các bên. JWT được sử dụng thay thế session truyền thống trong kiến trúc REST API.

**Cấu trúc JWT:**
```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.
eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.
SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c

Phần 1 (Header): eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9
  - Mã hóa Base64URL của:
  - {
      "alg": "HS256",  // Thuật toán ký
      "typ": "JWT"     // Loại token
    }

Phần 2 (Payload): eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ
  - Mã hóa Base64URL của:
  - {
      "email": "user@example.com",  // Claim: Email
      "roles": ["USER", "ADMIN"],   // Claim: Roles
      "exp": 1704067200,            // Claim: Hết hạn (Unix timestamp)
      "iat": 1704066600             // Claim: Thời gian tạo
    }

Phần 3 (Signature): SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
  - HMAC-SHA256(
      Base64URL(header) + "." + Base64URL(payload),
      secret_key
    )
```

#### B. Luồng xác thực JWT trong BookStore

```
┌──────────────┐
│ CLIENT       │
│ (Frontend)   │
└───────┬──────┘
        │
        │ 1. POST /api/auth/login
        │    { email, password }
        │
        ▼
┌──────────────────────────────┐
│ AuthController.login()       │
│                              │
│ 1. Kiểm tra email & password │
│ 2. Lấy roles từ DB          │
│ 3. Tạo JWT Token            │
└────────┬─────────────────────┘
         │
         │ 2. Trả về JWT Token
         │    { token, user }
         │
         ▼
┌──────────────┐
│ CLIENT       │
│ (Frontend)   │  Lưu token vào localStorage
└───────┬──────┘
        │
        │ 3. GET /api/books (Header: Authorization: Bearer <JWT>)
        │
        ▼
┌────────────────────────────────────┐
│ JwtAuthenticationFilter            │
│                                    │
│ 1. Lấy JWT từ header               │
│ 2. Xác thực chữ ký JWT            │
│ 3. Kiểm tra hết hạn (exp)         │
│ 4. Lấy email từ JWT payload       │
│ 5. Tìm user & roles từ DB         │
│ 6. Tạo UsernamePasswordAuthToken  │
│ 7. Lưu vào SecurityContext        │
└────────┬─────────────────────────────┘
         │
         │ Nếu hợp lệ: Cho phép truy cập
         │ Nếu không: Trả về 401 Unauthorized
         │
         ▼
┌──────────────────────┐
│ Controller/Endpoint  │
│ (Xử lý request)      │
└──────────────────────┘
```

#### C. Cấu hình JWT trong application.properties

```properties
# JWT Configuration
app.jwtSecret=bookstore-secret-key-for-jwt-token-generation-must-be-at-least-32-characters-long
app.jwtExpirationMs=86400000  # 24 giờ (tính bằng milliseconds)
```

**Lưu ý về bảo mật:**
- Secret key phải có độ dài tối thiểu **32 ký tự** cho HS256
- Secret key không được lưu trong source code, phải sử dụng **environment variables** hoặc **secrets management**
- JWT token không bao giờ được lưu trong **localStorage** (dễ bị XSS), nên dùng **HttpOnly cookies**
- Token nên có thời hạn hết hạn ngắn (24 giờ hoặc ít hơn)

#### D. JwtTokenProvider - Tạo và Xác thực Token

```java
@Component
public class JwtTokenProvider {
    
    @Value("${app.jwtSecret:...}")
    private String jwtSecret;
    
    @Value("${app.jwtExpirationMs:86400000}")
    private int jwtExpirationMs;
    
    // Tạo JWT token từ Authentication object
    public String generateToken(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);
        
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        
        return Jwts.builder()
            .subject(user.getEmail())
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }
    
    // Xác thực JWT token
    public boolean validateToken(String authToken) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(authToken);
            return true;
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty: {}", ex.getMessage());
        }
        return false;
    }
    
    // Lấy email từ JWT token
    public String getEmailFromJWT(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        
        Claims claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
            
        return claims.getSubject();
    }
}
```

#### E. JwtAuthenticationFilter - Xác thực JWT cho mỗi Request

```java
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response,
                                   FilterChain filterChain) 
            throws ServletException, IOException {
        try {
            // 1. Lấy JWT từ header
            String jwt = getJwtFromRequest(request);
            
            // 2. Xác thực JWT
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                // 3. Lấy email từ JWT
                String email = tokenProvider.getEmailFromJWT(jwt);
                
                // 4. Tìm user từ database
                User user = userRepository.findByEmail(email).orElse(null);
                
                if (user != null && user.getIsActive()) {
                    // 5. Xây dựng authorities từ roles
                    var authorities = user.getRoles()
                        .stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                        .collect(Collectors.toList());
                    
                    // 6. Tạo authentication token
                    UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(
                            user, null, authorities
                        );
                    authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    
                    // 7. Lưu vào SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception ex) {
            log.error("Could not set user authentication", ex);
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);  // Lấy phần sau "Bearer "
        }
        return null;
    }
}
```

### 2.2.4 Bảo mật CORS (Cross-Origin Resource Sharing)

Để cho phép Frontend (chạy trên port khác) gọi Backend API, hệ thống cấu hình CORS:

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .cors(cors -> cors.configurationSource(request -> {
            var config = new CorsConfiguration();
            
            // Cho phép các origin này
            config.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",      // React dev server
                "http://localhost:5500",      // Live Server
                "http://localhost:8000",      // Python server
                "http://localhost:63342",     // IntelliJ IDE
                "http://127.0.0.1:3000",
                "http://127.0.0.1:5500"
            ));
            
            // Cho phép các HTTP methods
            config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
            
            // Cho phép tất cả headers
            config.setAllowedHeaders(Arrays.asList("*"));
            
            // Cho phép gửi credentials (cookies, Authorization headers)
            config.setAllowCredentials(true);
            
            return config;
        }));
    
    return http.build();
}
```

### 2.2.5 Xử lý Lỗi Xác thực và Phân quyền

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.exceptionHandling(exception -> exception
        // Khi xác thực thất bại (Unauthorized)
        .authenticationEntryPoint((request, response, authException) -> {
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{" +
                "\"error\": \"Unauthorized\"," +
                "\"message\": \"" + authException.getMessage() + "\"" +
            "}");
        })
        // Khi người dùng không có quyền truy cập (Forbidden)
        .accessDeniedHandler((request, response, accessDeniedException) -> {
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("{" +
                "\"error\": \"Forbidden\"," +
                "\"message\": \"Access denied\"" +
            "}");
        })
    );
    
    return http.build();
}
```

---

## 2.3. CƠ SỞ DỮ LIỆU (Database)

### 2.3.1 Lựa chọn Hệ quản trị CSDL

#### A. Tại sao chọn MySQL?

**MySQL** là một hệ quản trị cơ sở dữ liệu quan hệ (RDBMS) mã nguồn mở được lựa chọn cho dự án BookStore vì những lý do sau:

| Tiêu chí | MySQL | Giải thích |
|----------|-------|-----------|
| **Chi phí** | Miễn phí | Mã nguồn mở, không cần mua license |
| **Độ tin cậy** | Cao | Được sử dụng rộng rãi, có cộng đồng lớn |
| **Hiệu suất** | Tốt | Xử lý tốt cho ứng dụng vừa và nhỏ |
| **Dễ sử dụng** | Đơn giản | Cài đặt dễ, cấu hình dễ |
| **Hỗ trợ Transactions** | Có (InnoDB) | Đảm bảo tính ACID cho giao dịch |
| **Khả năng mở rộng** | Tốt | Có thể mở rộng quy mô |
| **Hỗ trợ Indexes** | Có | Tối ưu hóa truy vấn |
| **Tích hợp Spring** | Tuyệt vời | Spring Data JPA hỗ trợ tốt MySQL |

### 2.3.2 Cấu trúc Schema (Sơ đồ CSDL)

**Entity Relationship Diagram (ERD):**

```
┌─────────────────────────┐
│       users             │
├─────────────────────────┤
│ id (PK)                 │
│ email (UNIQUE)          │
│ password_hash           │ ◄─────┐
│ phone                   │       │
│ first_name              │       │
│ last_name               │       │
│ avatar_url              │       │
│ is_active               │       │
│ created_at              │       │
│ updated_at              │       │
└─────────────────────────┘       │ 1:N
        │                         │
        │ N:M                     │
        │ (users_roles)           │
        │                         │
        ▼                         ▼
┌──────────────────────────┐  ┌──────────────────────────┐
│      roles               │  │       orders             │
├──────────────────────────┤  ├──────────────────────────┤
│ id (PK)                  │  │ id (PK)                  │
│ name (UNIQUE)            │  │ order_code (UNIQUE)      │
│ description              │  │ user_id (FK)             │
│ created_at               │  │ status                   │
└──────────────────────────┘  │ payment_method           │
                               │ payment_status           │
                               │ total_amount             │
                               │ shipping_address         │
                               │ created_at               │
                               │ updated_at               │
                               └────────┬─────────────────┘
                                        │
                                        │ 1:N
                                        │
                                        ▼
                               ┌──────────────────────┐
                               │   order_items        │
                               ├──────────────────────┤
                               │ id (PK)              │
                               │ order_id (FK)        │
                               │ book_id (FK)         │
                               │ quantity             │
                               │ price_per_unit       │
                               │ subtotal             │
                               └────────┬─────────────┘
                                        │
                                        │ N:1
                                        │
                                        ▼
                               ┌──────────────────────────┐
                               │       books              │
                               ├──────────────────────────┤
                               │ id (PK)                  │
                               │ title                    │
                               │ subtitle                 │
                               │ authors                  │
                               │ publisher                │
                               │ published_date           │
                               │ description              │
                               │ category_id (FK)         │
                               │ price                    │
                               │ stock_quantity           │
                               │ cover_image_url          │
                               │ average_rating           │
                               │ created_at               │
                               │ updated_at               │
                               └────────┬─────────────────┘
                                        │
                                        │ N:1
                                        │
                                        ▼
                               ┌──────────────────────┐
                               │    categories        │
                               ├──────────────────────┤
                               │ id (PK)              │
                               │ name                 │
                               │ description          │
                               │ created_at           │
                               └──────────────────────┘

┌──────────────────────┐         ┌──────────────────────────┐
│       cart           │         │      wishlist            │
├──────────────────────┤         ├──────────────────────────┤
│ id (PK)              │         │ id (PK)                  │
│ user_id (FK)         │         │ user_id (FK)             │
│ book_id (FK)         │         │ book_id (FK)             │
│ quantity             │         │ added_date               │
│ created_at           │         │ created_at               │
│ updated_at           │         └──────────────────────────┘
└──────────────────────┘

┌──────────────────────┐         ┌──────────────────────────┐
│      reviews         │         │     vouchers             │
├──────────────────────┤         ├──────────────────────────┤
│ id (PK)              │         │ id (PK)                  │
│ book_id (FK)         │         │ code (UNIQUE)            │
│ user_id (FK)         │         │ discount_percent         │
│ rating               │         │ discount_amount          │
│ comment              │         │ min_order_amount         │
│ created_at           │         │ max_usage                │
│ updated_at           │         │ used_count               │
└──────────────────────┘         │ expiry_date              │
                                 │ is_active                │
                                 │ created_at               │
                                 └──────────────────────────┘

┌────────────────────────────┐
│   home_sections            │
├────────────────────────────┤
│ id (PK)                    │
│ title                      │
│ section_type               │
│ display_order              │
│ is_active                  │
│ created_at                 │
└────────────────────────────┘
```

### 2.3.3 Chi tiết các Bảng Chính

#### **Bảng: users**
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    avatar_url VARCHAR(500),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_email (email),
    INDEX idx_phone (phone)
);
```

**Mục đích**: Lưu trữ thông tin khách hàng, admin
**Số lượng dòng dự kiến**: 1,000 - 10,000 users
**Tính chất**: Thay đổi thường xuyên khi có đăng ký, cập nhật thông tin

#### **Bảng: books**
```sql
CREATE TABLE books (
    id VARCHAR(50) PRIMARY KEY,  -- Google Books ID
    title VARCHAR(500),
    subtitle VARCHAR(500),
    authors VARCHAR(300),
    publisher VARCHAR(300),
    published_date VARCHAR(50),
    description TEXT,
    category_id BIGINT,
    price DECIMAL(10, 2),
    stock_quantity INT,
    cover_image_url VARCHAR(500),
    average_rating DECIMAL(3, 2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (category_id) REFERENCES categories(id),
    INDEX idx_title (title),
    INDEX idx_category (category_id)
);
```

**Mục đích**: Lưu trữ thông tin sách
**Số lượng dòng dự kiến**: 5,000 - 50,000 sách
**Tính chất**: Dữ liệu chủ yếu, ít thay đổi (chỉ update giá, stock)

#### **Bảng: orders**
```sql
CREATE TABLE orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_code VARCHAR(50) UNIQUE NOT NULL,
    user_id BIGINT NOT NULL,
    status VARCHAR(50),  -- pending, approved, shipped, delivered, cancelled
    payment_method VARCHAR(50),  -- cod, vnpay, momo
    payment_status VARCHAR(50),  -- unpaid, paid, payment_failed, refunded
    total_amount DECIMAL(10, 2),
    shipping_address VARCHAR(500),
    phone_number VARCHAR(20),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_user_id (user_id),
    INDEX idx_order_code (order_code),
    INDEX idx_status (status)
);
```

**Mục đích**: Lưu trữ đơn hàng
**Số lượng dòng dự kiến**: 10,000 - 100,000 đơn hàng
**Tính chất**: Thêm thường xuyên, ít xóa, hay update status

#### **Bảng: order_items**
```sql
CREATE TABLE order_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    book_id VARCHAR(50) NOT NULL,
    quantity INT,
    price_per_unit DECIMAL(10, 2),
    subtotal DECIMAL(10, 2),
    
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (book_id) REFERENCES books(id),
    INDEX idx_order_id (order_id)
);
```

**Mục đích**: Lưu trữ chi tiết sách trong mỗi đơn hàng
**Tính chất**: Dữ liệu liên kết, thêm khi tạo đơn

#### **Bảng: cart**
```sql
CREATE TABLE cart (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    book_id VARCHAR(50) NOT NULL,
    quantity INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (book_id) REFERENCES books(id),
    UNIQUE KEY unique_user_book (user_id, book_id)
);
```

**Mục đích**: Lưu trữ giỏ hàng tạm thời
**Tính chất**: Dữ liệu tạm, hay thay đổi, không yêu cầu lưu lâu dài

#### **Bảng: vouchers**
```sql
CREATE TABLE vouchers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(50) UNIQUE NOT NULL,
    discount_percent DECIMAL(5, 2),
    discount_amount DECIMAL(10, 2),
    min_order_amount DECIMAL(10, 2),
    max_usage INT,
    used_count INT DEFAULT 0,
    expiry_date TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_code (code)
);
```

**Mục đích**: Lưu trữ mã khuyến mãi/giảm giá
**Tính chất**: Dữ liệu quản lý, update thường xuyên

#### **Bảng: reviews**
```sql
CREATE TABLE reviews (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    book_id VARCHAR(50) NOT NULL,
    user_id BIGINT NOT NULL,
    rating INT,  -- 1-5 sao
    comment TEXT,
    is_approved BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (book_id) REFERENCES books(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_book_id (book_id)
);
```

**Mục đích**: Lưu trữ đánh giá & bình luận từ khách hàng
**Tính chất**: Tăng thường xuyên khi có người đánh giá

#### **Bảng: wishlist**
```sql
CREATE TABLE wishlist (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    book_id VARCHAR(50) NOT NULL,
    added_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (book_id) REFERENCES books(id),
    UNIQUE KEY unique_user_book (user_id, book_id)
);
```

**Mục đích**: Lưu trữ danh sách yêu thích
**Tính chất**: Cá nhân hóa, hay thay đổi

### 2.3.4 Tối ưu hóa CSDL

#### A. Indexes (Chỉ mục)
```sql
-- Tìm kiếm nhanh theo email
CREATE INDEX idx_email ON users(email);

-- Tìm kiếm sách theo tiêu đề
CREATE INDEX idx_title ON books(title);

-- Tìm đơn hàng của user
CREATE INDEX idx_user_id ON orders(user_id);

-- Tìm chi tiết đơn hàng
CREATE INDEX idx_order_id ON order_items(order_id);

-- Tìm bình luận của sách
CREATE INDEX idx_book_review ON reviews(book_id);
```

#### B. Thiết lập khóa ngoại (Foreign Keys)
Bảo đảm tính toàn vẹn dữ liệu bằng các khóa ngoại:
```sql
ALTER TABLE orders ADD CONSTRAINT fk_order_user 
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE order_items ADD CONSTRAINT fk_item_order 
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE;

ALTER TABLE order_items ADD CONSTRAINT fk_item_book 
    FOREIGN KEY (book_id) REFERENCES books(id);
```

#### C. Query Optimization
Một số truy vấn thường xuyên được tối ưu:

```sql
-- Tìm 10 sách hot nhất
SELECT id, title, average_rating, price FROM books 
WHERE average_rating >= 4.0 AND stock_quantity > 0
ORDER BY average_rating DESC, created_at DESC
LIMIT 10;

-- Tổng doanh số của user
SELECT user_id, COUNT(*) as order_count, SUM(total_amount) as total_spent
FROM orders
WHERE status = 'delivered'
GROUP BY user_id;

-- Tìm sách theo danh mục và giá
SELECT id, title, price, average_rating FROM books
WHERE category_id = ? AND price BETWEEN ? AND ?
ORDER BY average_rating DESC
LIMIT 20 OFFSET ?;
```

---

## 2.4. NGÔN NGỮ LẬP TRÌNH VÀ FRAMEWORK

### 2.4.1 Backend: Java Spring Boot

#### A. Tại sao chọn Java Spring Boot?

| Tiêu chí | Java Spring Boot | Lợi ích |
|----------|------------------|---------|
| **Ngôn ngữ** | Java 21 | Hỗ trợ các feature hiện đại, cải thiện hiệu suất |
| **Framework** | Spring Boot 3.2.3 | Convention over Configuration, giảm boilerplate |
| **Productivity** | Cao | Starter POMs, auto-configuration |
| **Scalability** | Tốt | Xử lý concurrency tốt, dễ mở rộng |
| **Security** | Tuyệt vời | Spring Security, JWT, encryption built-in |
| **Database** | Spring Data JPA | ORM tuyệt vời, giảm code SQL |
| **Testing** | Mạnh | JUnit, MockMvc, testcontainers |
| **Community** | Lớn nhất | Hỗ trợ tốt, thư viện phong phú |
| **DevOps** | Sẵn sàng | Docker, Kubernetes support |

### 2.4.2 Kiến trúc Backend - Layered Architecture

Hệ thống BookStore sử dụng **kiến trúc phân lớp** (Layered Architecture):

```
┌──────────────────────────────────────────────────────┐
│              PRESENTATION LAYER                      │
│              (REST Controllers)                      │
│  - AuthController, BookController, OrderController  │
└─────────────────────────┬──────────────────────────┘
                          │
┌─────────────────────────▼──────────────────────────┐
│              SERVICE LAYER                          │
│         (Business Logic - Business Rules)          │
│  - UserService, BookService, OrderService          │
│  - PaymentService, CartService, VoucherService     │
└─────────────────────────┬──────────────────────────┘
                          │
┌─────────────────────────▼──────────────────────────┐
│            DATA ACCESS LAYER (Repository)           │
│         (CRUD Operations - Database Interaction)    │
│  - UserRepository, BookRepository, OrderRepository │
└─────────────────────────┬──────────────────────────┘
                          │
┌─────────────────────────▼──────────────────────────┐
│         DATABASE LAYER (MySQL + JPA/Hibernate)     │
│              (Persistent Data Storage)             │
│         - users, books, orders, etc.               │
└──────────────────────────────────────────────────┘
```

#### B. Chi tiết các Layer

**1. Presentation Layer (REST Controllers)**
```java
@RestController
@RequestMapping("/api/books")
@CrossOrigin
public class BookController {
    
    @Autowired
    private BookService bookService;
    
    @GetMapping
    public ResponseEntity<?> getAllBooks(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(bookService.getBooks(page, size));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getBookById(@PathVariable String id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }
}
```

**2. Service Layer (Business Logic)**
```java
@Service
@Transactional
public class BookService {
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    public Page<BookDTO> getBooks(int page, int size) {
        // Logic: Tìm kiếm, lọc, sắp xếp sách
        Page<Book> books = bookRepository.findAll(
            PageRequest.of(page, size, Sort.by("createdAt").descending())
        );
        return books.map(BookDTO::fromEntity);
    }
    
    public BookDTO getBookById(String id) {
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found"));
        return BookDTO.fromEntity(book);
    }
}
```

**3. Repository Layer (Data Access)**
```java
@Repository
public interface BookRepository extends JpaRepository<Book, String> {
    
    // Custom queries
    Page<Book> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    
    Page<Book> findByCategory(Category category, Pageable pageable);
    
    Page<Book> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    
    @Query("SELECT b FROM Book b WHERE " +
           "LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(b.authors) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Book> searchBooks(@Param("keyword") String keyword, Pageable pageable);
}
```

**4. Entity Layer (ORM Mapping)**
```java
@Entity
@Table(name = "books", indexes = {
    @Index(name = "idx_title", columnList = "title"),
    @Index(name = "idx_category", columnList = "category_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    @Id
    private String id;
    
    @Column(nullable = false, length = 500)
    private String title;
    
    @Column(length = 500)
    private String subtitle;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column
    private Integer stockQuantity;
    
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private Set<Review> reviews = new HashSet<>();
}
```

### 2.4.3 Frontend: HTML5, CSS3, JavaScript (Vanilla)

#### A. Công nghệ Frontend

| Công nghệ | Phiên bản | Mục đích |
|-----------|-----------|---------|
| **HTML5** | HTML5 | Cấu trúc trang web |
| **CSS3** | CSS3 | Thiết kế, styling responsive |
| **JavaScript** | ES6+ | Interactivity, AJAX calls |
| **Bootstrap** | (implicitly used) | Responsive grid, components |

#### B. Cấu trúc Frontend

```
Frontend/
├── index.html                 # Trang chủ
├── pages/
│   ├── login.html             # Đăng nhập
│   ├── products.html          # Danh sách sách
│   ├── product-detail.html    # Chi tiết sách
│   ├── cart.html              # Giỏ hàng
│   ├── checkout.html          # Thanh toán
│   ├── orders.html            # Lịch sử đơn hàng
│   ├── account.html           # Tài khoản cá nhân
│   └── wishlist.html          # Danh sách yêu thích
├── admin/
│   └── dashboard.html         # Admin dashboard
├── assets/
│   ├── css/
│   │   ├── style.css          # Styles chính
│   │   ├── admin.css          # Styles admin
│   │   ├── cart.css           # Styles giỏ hàng
│   │   └── ...
│   ├── js/
│   │   ├── api.js             # API calls (fetch)
│   │   ├── main.js            # Main logic
│   │   ├── admin.js           # Admin functions
│   │   ├── ai-chat.js         # Chatbot logic
│   │   └── config.js          # Configuration
│   └── images/
│       ├── banner*.png        # Banner quảng cáo
│       └── covers/            # Bìa sách
```

#### C. Tích hợp API (Vanilla JavaScript Fetch)

```javascript
// config.js - Cấu hình API
const API_BASE_URL = 'http://localhost:8080/api';
const JWT_TOKEN = localStorage.getItem('token');

// api.js - Hàm gọi API
async function getBooks(page = 0, size = 12) {
    try {
        const response = await fetch(`${API_BASE_URL}/books?page=${page}&size=${size}`);
        if (!response.ok) throw new Error('Failed to fetch books');
        return await response.json();
    } catch (error) {
        console.error('Error fetching books:', error);
        return null;
    }
}

async function createOrder(orderData) {
    try {
        const response = await fetch(`${API_BASE_URL}/orders/create`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${JWT_TOKEN}`
            },
            body: JSON.stringify(orderData)
        });
        
        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message);
        }
        
        return await response.json();
    } catch (error) {
        console.error('Error creating order:', error);
        throw error;
    }
}

async function login(email, password) {
    try {
        const response = await fetch(`${API_BASE_URL}/auth/login`, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({email, password})
        });
        
        const data = await response.json();
        
        if (data.token) {
            localStorage.setItem('token', data.token);
            localStorage.setItem('user', JSON.stringify(data.user));
            return true;
        }
        return false;
    } catch (error) {
        console.error('Login failed:', error);
        return false;
    }
}
```

### 2.4.4 Các Dependencies quan trọng (Maven)

```xml
<!-- Spring Boot Web (REST API) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- Spring Data JPA (ORM) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- MySQL JDBC Driver -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
</dependency>

<!-- Spring Security (Authentication & Authorization) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- JWT Token (JSON Web Token) -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>

<!-- Lombok (Reduce Boilerplate) -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
</dependency>

<!-- Validation -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

<!-- Spring Mail (Email Notifications) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>

<!-- GSON (JSON Processing) -->
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
</dependency>

<!-- HTTP Client (External APIs) -->
<dependency>
    <groupId>org.apache.httpcomponents.client5</groupId>
    <artifactId>httpclient5</artifactId>
</dependency>
```

### 2.4.5 Lợi ích của Stack công nghệ được lựa chọn

| Khía cạnh | Lợi ích |
|-----------|---------|
| **Khả năng bảo mật** | Spring Security + JWT cung cấp xác thực & phân quyền mạnh mẽ |
| **Hiệu suất** | Java 21 + Spring Boot tối ưu hóa tốt, thread-safe |
| **Dễ bảo trì** | Code rõ ràng, kiến trúc modular, dễ test |
| **Khả năng mở rộng** | Dễ thêm features mới (payment gateways, notification systems) |
| **Chi phí** | Mã nguồn mở hoàn toàn, không cần license |
| **Thời gian phát triển** | Spring Boot giảm boilerplate, tăng tốc độ phát triển |
| **DevOps-friendly** | Docker, CI/CD support tốt |
| **API Integration** | Dễ tích hợp Google Books API, Google Gemini API, SePay |

---

## KẾT LUẬN CHƯƠNG II

Chương II đã trình bày chi tiết các kiến thức lý thuyết áp dụng trong dự án BookStore:

1. **Phân tích & Thiết kế**: BFD và DFD cung cấp cái nhìn toàn diện về luồng chức năng và dữ liệu
2. **Quản trị hệ thống**: JWT + Spring Security đảm bảo an ninh, phân quyền rõ ràng
3. **CSDL**: MySQL cung cấp lưu trữ tin cậy với schema tối ưu hóa
4. **Công nghệ**: Java Spring Boot + Frontend JS tạo nên ứng dụng web hiện đại, scalable

Những nội dung này sẽ là cơ sở cho các chương tiếp theo về triển khai, kiểm thử và vận hành hệ thống.

