# CHƯƠNG I. MÔ TẢ, KHẢO SÁT VÀ XÁC ĐỊNH YÊU CẦU BÀI TOÁN

## 1.1 MÔ TẢ BÀI TOÁN

### 1.1.1 Lý do chọn đề tài

Trong bối cảnh chuyển đổi số toàn cầu và sự phát triển mạnh mẽ của thương mại điện tử tại Việt Nam, lĩnh vực bán lẻ sách trực tuyến đang trở thành một cơ hội kinh doanh tiềm năng. Theo báo cáo của Hiệp hội Thương mại Điện tử Việt Nam (VECOM), quy mô thị trường TMĐT Việt Nam năm 2024 đạt khoảng 15 tỷ USD, với tốc độ tăng trưởng hàng năm khoảng 15-20%. Đặc biệt, ngành bán lẻ sách và xuất bản kỹ thuật số là một phân khúc có tiềm năng tăng trưởng cao.

**Lý do chọn đề tài:**

1. **Nhu cầu thị trường rõ rệt**: Với sự phát triển của nền giáo dục kỹ thuật số, nhu cầu tiếp cập sách điện tử, sách in và dịch vụ tư vấn sách ngày càng tăng. Các bạn trẻ, sinh viên, và người đọc chuyên nghiệp cần một nền tảng TMĐT tích hợp để tìm kiếm, so sánh và mua sách một cách tiện lợi.

2. **Khoảng trống thị trường**: Hiện nay, số lượng các website bán sách trực tuyến ở Việt Nam còn hạn chế so với các nước phát triển. Các nền tảng hiện có thường thiếu tính cạnh tranh, có giao diện lỗi thời, hoặc không cung cấp đầy đủ các tính năng như tư vấn AI, tích hợp các API sách quốc tế, hay thanh toán đa chiều.

3. **Mục tiêu học tập**: Dự án này là cơ hội tuyệt vời để áp dụng các kiến thức về phân tích thiết kế hệ thống, phát triển web full-stack, bảo mật ứng dụng, và quản lý dự án phần mềm trong một hệ thống thực tế.

4. **Tính khả thi**: Công nghệ cần thiết để xây dựng hệ thống này (Java Spring Boot, MySQL, JavaScript) là những công nghệ phổ biến, có cộng đồng hỗ trợ lớn, và khả năng mở rộng cao trong tương lai.

### 1.1.2 Bối cảnh thị trường và bài toán kinh doanh

#### A. Bối cảnh thị trường

**Tình hình ngành bán lẻ sách tại Việt Nam:**

- **Thị trường sách**: Theo Tổng cục Thống kê Việt Nam, thị trường sách Việt Nam có quy mô khoảng 1-1.5 triệu tấn sách in được lưu thông hàng năm, với giá trị thị trường ước tính từ 500-800 triệu USD. Tuy nhiên, hình thức bán lẻ truyền thống (cửa hàng bán lẻ, hiệu sách vật lý) đang suy giảm do xu hướng chuyển đổi số.

- **Thương mại điện tử sách**: Tỷ lệ bán sách trực tuyến so với bán lẻ truyền thống hiện chỉ chiếm khoảng 15-20%, nhưng tăng trưởng hàng năm vào khoảng 25-30%. Các nền tảng như Tiki, Shopee, Amazon Việt đã bắt đầu bán sách, nhưng chúng không phải là chuyên biệt cho lĩnh vực xuất bản.

- **Đối tượng khách hàng**: 
  - Sinh viên: Cần tìm sách giáo khoa, sách tham khảo
  - Người đọc lâu đời: Tìm kiếm tác phẩm cụ thể, muốn đọc nội dung giới thiệu trước khi mua
  - Các cơ sở kinh doanh: Mua sách hàng loạt cho thư viện, trường học, công ty
  - Người yêu thích đọc sách: Tìm kiếm sách mới, muốn có cộng đồng đọc sách

- **Xu hướng tiêu dùng**:
  - Tăng nhu cầu mua hàng trực tuyến (78% người dùng internet Việt Nam mua hàng online)
  - Xu hướng mua sách e-book tăng (đặc biệt ở các thành phố lớn)
  - Yêu cầu về dịch vụ giao hàng nhanh, thanh toán linh hoạt
  - Sự phát triển của các ứng dụng di động (80% lưu lượng TMĐT đến từ mobile)

#### B. Bài toán kinh doanh

**Vấn đề kinh doanh cần giải quyết:**

1. **Hiệu suất bán hàng thấp**: Các nhà bán sách truyền thống gặp khó khăn trong việc tiếp cận khách hàng rộng khắp, chi phí quản lý kho hàng cao, và doanh số bán hàng không ổn định.

2. **Trải nghiệm khách hàng kém**: Khách hàng hiện tại gặp khó khăn:
   - Tìm kiếm sách chất lượng kém (thiếu filter, sort, search nâng cao)
   - Không có thông tin đầy đủ về sách (tác giả, nhà xuất bản, mô tả chi tiết)
   - Thanh toán chỉ bằng một vài phương thức
   - Không có cộng đồng hoặc tư vấn

3. **Cơ hội kinh doanh**:
   - Xây dựng nền tảng TMĐT chuyên biệt cho bán sách
   - Cung cấp dịch vụ tăng giá trị (tư vấn AI, đánh giá sách, wishlist, giảm giá)
   - Tích hợp với các API sách quốc tế (Google Books, etc.) để mở rộng danh mục sản phẩm
   - Phát triển các tính năng độc quyền (chatbot tư vấn sách, recommendations)

**Mô hình kinh doanh:**

- **Doanh thu từ bán sách**: Margin từ bán sách trực tuyến (20-30% markup)
- **Phí dịch vụ**: Commission từ các nhà cung cấp sách
- **Quảng cáo**: Bán vị trí quảng cáo cho nhà xuất bản
- **Subscription**: Gói hội viên VIP với các ưu đãi đặc biệt

**Mục tiêu kinh doanh (6-12 tháng):**

- Tăng trưởng khách hàng: 100-500 khách hàng hoạt động
- Giá trị đơn hàng trung bình: 200,000 - 500,000 VND
- Tỷ lệ chuyển đổi (conversion rate): 2-5%
- Tỷ lệ giữ khách hàng (retention rate): 30-50%
- Độ hài lòng khách hàng: ≥ 4.0/5.0 sao

---

## 1.2 KHẢO SÁT, XÁC ĐỊNH YÊU CẦU BÀI TOÁN

### 1.2.1 Quy trình khảo sát

Để xác định yêu cầu chính xác cho hệ thống BookStore, chúng tôi đã thực hiện quy trình khảo sát toàn diện bao gồm các bước sau:

#### **Bước 1: Phân tích bối cảnh và các nền tảng cạnh tranh**

**Phương pháp**: 
- Nghiên cứu tài liệu thứ cấp (secondary research): Phân tích các báo cáo về thị trường TMĐT, tình hình bán lẻ sách
- Phân tích ngang hàng (competitor analysis): So sánh các website bán sách hiện có (Tiki Books, Shopee Books, Amazon Việt)

**Kết quả**:
- Xác định các tính năng cần thiết: tìm kiếm, lọc, giỏ hàng, thanh toán, quản lý đơn hàng
- Xác định các tính năng khác biệt: AI chatbot, tích hợp Google Books API, tư vấn cá nhân hóa
- Xác định các vấn đề trong các nền tảng hiện có: giao diện lỗi thời, tìm kiếm kém hiệu quả, thanh toán hạn chế

#### **Bước 2: Phỏng vấn người dùng (User Interviews)**

**Phương pháp**: 
- Phỏng vấn trực tiếp 10-15 người (sinh viên, người đọc sách, nhân viên bán sách)
- Hỏi về nhu cầu, thói quen mua sách, điểm đau

**Kết quả**:
- **Nhu cầu tìm kiếm**: Khách muốn tìm sách theo tên tác giả, tiêu đề, thể loại, giá tiền
- **Nhu cầu thông tin**: Khách muốn xem mô tả chi tiết, đánh giá, bình luận trước khi mua
- **Nhu cầu thanh toán**: Khách muốn thanh toán linh hoạt (COD, online, ví điện tử)
- **Nhu cầu tư vấn**: Khách muốn được gợi ý sách phù hợp với sở thích, hoặc chat với AI để hỏi thông tin sách
- **Nhu cầu quản lý**: Khách muốn xem lịch sử đơn hàng, wishlist, tài khoản cá nhân

#### **Bước 3: Khảo sát nhu cầu chức năng (Functional Requirements Survey)**

**Phương pháp**: 
- Phân tích các use case trong ngành bán lẻ TMĐT
- Tham khảo các tiêu chuẩn ISO 9001 cho hệ thống bán hàng

**Kết quả**: Liệt kê 20+ chức năng cần thiết (xem mục 1.2.2)

#### **Bước 4: Khảo sát yêu cầu phi chức năng (Non-Functional Requirements Survey)**

**Phương pháp**: 
- Khảo sát kỹ năng, kinh nghiệm của team phát triển
- Phân tích các ràng buộc kỹ thuật (công nghệ, cơ sở hạ tầng)
- Phân tích các tiêu chuẩn bảo mật (PCI-DSS cho thanh toán, OWASP cho web security)

**Kết quả**: Xác định các yêu cầu hiệu suất, bảo mật, khả năng mở rộng (xem mục 1.2.3)

### 1.2.2 Yêu cầu chức năng (Functional Requirements)

Hệ thống BookStore cần đáp ứng các yêu cầu chức năng sau:

#### **Group 1: Quản lý xác thực và tài khoản người dùng (User & Authentication Management)**

| ID | Tên yêu cầu | Mô tả | Ưu tiên |
|----|------------|-------|--------|
| FR-1.1 | Đăng ký tài khoản | Người dùng mới có thể tạo tài khoản bằng email và mật khẩu | Cao |
| FR-1.2 | Xác minh email | Hệ thống gửi OTP hoặc link xác minh qua email để xác nhận tài khoản | Cao |
| FR-1.3 | Đăng nhập | Người dùng đăng nhập bằng email/mật khẩu, nhận JWT token | Cao |
| FR-1.4 | Quên mật khẩu | Người dùng có thể yêu cầu đặt lại mật khẩu qua email | Cao |
| FR-1.5 | Quản lý hồ sơ cá nhân | Người dùng có thể xem/sửa thông tin: tên, SĐT, địa chỉ, ảnh đại diện | Cao |
| FR-1.6 | Quản lý địa chỉ giao hàng | Người dùng có thể lưu nhiều địa chỉ giao hàng | Trung bình |
| FR-1.7 | Đăng xuất | Người dùng có thể đăng xuất, xóa session/token | Cao |
| FR-1.8 | Phân quyền người dùng | Hệ thống phân quyền giữa Admin và User thường | Cao |

#### **Group 2: Quản lý sản phẩm (Product Management)**

| ID | Tên yêu cầu | Mô tả | Ưu tiên |
|----|------------|-------|--------|
| FR-2.1 | Xem danh sách sách | Hiển thị danh sách sách với phân trang, 12-20 sách/trang | Cao |
| FR-2.2 | Tìm kiếm sách | Tìm kiếm theo tiêu đề, tác giả, từ khóa | Cao |
| FR-2.3 | Lọc sách | Lọc theo danh mục, giá tiền, đánh giá, năm xuất bản | Cao |
| FR-2.4 | Sắp xếp sách | Sắp xếp theo giá (tăng/giảm), đánh giá cao nhất, mới nhất | Cao |
| FR-2.5 | Xem chi tiết sách | Hiển thị tên, tác giả, nhà xuất bản, mô tả, giá, hình ảnh bìa, đánh giá, bình luận | Cao |
| FR-2.6 | Quản lý danh mục | Admin có thể tạo/sửa/xóa danh mục sách | Trung bình |
| FR-2.7 | Tích hợp Google Books API | Lấy thông tin sách từ Google Books (metadata, hình ảnh bìa) | Trung bình |
| FR-2.8 | Quản lý Banner & HomeSection | Admin quản lý banner quảng cáo và section trên trang chủ | Trung bình |

#### **Group 3: Quản lý giỏ hàng (Shopping Cart Management)**

| ID | Tên yêu cầu | Mô tả | Ưu tiên |
|----|------------|-------|--------|
| FR-3.1 | Thêm sách vào giỏ | Người dùng thêm sách vào giỏ hàng, chỉ định số lượng | Cao |
| FR-3.2 | Xem giỏ hàng | Hiển thị danh sách sách trong giỏ, tổng giá | Cao |
| FR-3.3 | Chỉnh sửa số lượng | Thay đổi số lượng sách trong giỏ | Cao |
| FR-3.4 | Xóa sách khỏi giỏ | Xóa một hoặc tất cả sách khỏi giỏ | Cao |
| FR-3.5 | Tính toán giá | Tính tổng giá dựa trên sách và số lượng | Cao |
| FR-3.6 | Lưu giỏ hàng | Lưu giỏ hàng tạm thời (có thể xóa khi logout) | Trung bình |

#### **Group 4: Quản lý thanh toán (Payment & Checkout Management)**

| ID | Tên yêu cầu | Mô tả | Ưu tiên |
|----|------------|-------|--------|
| FR-4.1 | Tạo đơn hàng | Chuyển đổi giỏ hàng thành đơn hàng chính thức | Cao |
| FR-4.2 | Chọn phương thức thanh toán | Người dùng chọn COD, Thanh toán online (SePay, etc.) | Cao |
| FR-4.3 | Thanh toán COD | Hỗ trợ thanh toán khi nhận hàng | Cao |
| FR-4.4 | Thanh toán online (SePay) | Tích hợp SePay để thanh toán online | Cao |
| FR-4.5 | Áp dụng voucher/mã giảm giá | Người dùng nhập mã giảm giá, hệ thống xác thực và tính giảm giá | Cao |
| FR-4.6 | Tính toán phí ship | Tính phí giao hàng dựa trên địa chỉ | Trung bình |
| FR-4.7 | Sinh mã đơn hàng duy nhất | Tạo mã đơn hàng không trùng lặp (order code) | Cao |
| FR-4.8 | Hóa đơn điện tử | Gửi hóa đơn qua email sau khi tạo đơn | Trung bình |

#### **Group 5: Quản lý đơn hàng (Order Management)**

| ID | Tên yêu cầu | Mô tả | Ưu tiên |
|----|------------|-------|--------|
| FR-5.1 | Xem danh sách đơn | Người dùng xem danh sách đơn hàng của mình (phân trang) | Cao |
| FR-5.2 | Xem chi tiết đơn | Hiển thị chi tiết: mã, ngày, trạng thái, sách, giá, địa chỉ giao | Cao |
| FR-5.3 | Cập nhật trạng thái đơn | Admin cập nhật trạng thái: pending, approved, shipped, delivered, cancelled | Cao |
| FR-5.4 | Hủy đơn hàng | Người dùng hủy đơn nếu chưa duyệt | Trung bình |
| FR-5.5 | Theo dõi đơn hàng | Xem lịch sử thay đổi trạng thái của đơn | Trung bình |
| FR-5.6 | Quản lý đơn hàng (Admin) | Admin xem tất cả đơn, duyệt, cập nhật trạng thái, báo cáo doanh số | Cao |
| FR-5.7 | Hoàn tiền | Xử lý hoàn tiền khi đơn bị hủy | Trung bình |

#### **Group 6: Quản lý mã khuyến mãi (Voucher Management)**

| ID | Tên yêu cầu | Mô tả | Ưu tiên |
|----|------------|-------|--------|
| FR-6.1 | Tạo voucher | Admin tạo mã giảm giá (% hoặc số tiền cố định) | Trung bình |
| FR-6.2 | Cấu hình voucher | Cấu hình: giảm giá, giá trị tối thiểu đơn, số lần dùng, thời hạn | Trung bình |
| FR-6.3 | Kiểm tra voucher | Hệ thống kiểm tra voucher có hợp lệ hay không | Cao |
| FR-6.4 | Tính giảm giá | Tính số tiền giảm và cập nhật tổng giá | Cao |
| FR-6.5 | Lưu lịch sử dùng voucher | Ghi lại mỗi lần voucher được sử dụng (user, order, ngày) | Trung bình |

#### **Group 7: Quản lý đánh giá & bình luận (Reviews & Ratings)**

| ID | Tên yêu cầu | Mô tả | Ưu tiên |
|----|------------|-------|--------|
| FR-7.1 | Để lại đánh giá | Người dùng đánh giá sách từ 1-5 sao | Trung bình |
| FR-7.2 | Viết bình luận | Người dùng viết bình luận/nhận xét về sách | Trung bình |
| FR-7.3 | Xem bình luận | Hiển thị bình luận từ người dùng khác | Trung bình |
| FR-7.4 | Tính trung bình đánh giá | Tính rating trung bình của mỗi sách | Trung bình |
| FR-7.5 | Duyệt bình luận (Admin) | Admin duyệt/xóa bình luận không phù hợp | Thấp |

#### **Group 8: Quản lý danh sách yêu thích (Wishlist)**

| ID | Tên yêu cầu | Mô tả | Ưu tiên |
|----|------------|-------|--------|
| FR-8.1 | Thêm vào wishlist | Người dùng thêm sách vào danh sách yêu thích | Trung bình |
| FR-8.2 | Xem wishlist | Hiển thị danh sách sách yêu thích của user | Trung bình |
| FR-8.3 | Xóa khỏi wishlist | Xóa sách khỏi danh sách yêu thích | Trung bình |
| FR-8.4 | Chuyển từ wishlist vào giỏ | Thêm sách từ wishlist vào giỏ hàng một lần | Trung bình |

#### **Group 9: AI Chatbot tư vấn (AI Chat Assistant)**

| ID | Tên yêu cầu | Mô tả | Ưu tiên |
|----|------------|-------|--------|
| FR-9.1 | Chat với bot | Người dùng chat với bot để hỏi về sách | Trung bình |
| FR-9.2 | Bot trả lời câu hỏi | Bot sử dụng AI (Google Gemini) để trả lời câu hỏi | Trung bình |
| FR-9.3 | Bot gợi ý sách | Bot đề xuất sách phù hợp dựa trên câu hỏi | Trung bình |
| FR-9.4 | Lưu lịch sử chat | Lưu cuộc hội thoại để có context | Thấp |

#### **Group 10: Quản lý Admin (Admin Dashboard)**

| ID | Tên yêu cầu | Mô tả | Ưu tiên |
|----|------------|-------|--------|
| FR-10.1 | Dashboard overview | Hiển thị KPI: doanh số hôm nay, số user mới, số đơn chờ | Cao |
| FR-10.2 | Quản lý sản phẩm | Tạo/sửa/xóa sách, quản lý danh mục | Cao |
| FR-10.3 | Quản lý đơn hàng | Duyệt, cập nhật trạng thái, xem chi tiết đơn hàng | Cao |
| FR-10.4 | Quản lý người dùng | Xem danh sách user, vô hiệu hóa tài khoản | Trung bình |
| FR-10.5 | Báo cáo doanh số | Xem báo cáo doanh số theo ngày, tháng, danh mục | Trung bình |
| FR-10.6 | Quản lý voucher | Tạo/sửa/xóa mã giảm giá | Trung bình |

**Tóm tắt**: Hệ thống có **48 yêu cầu chức năng** được chia thành **10 nhóm** chính, với **21 yêu cầu ưu tiên cao** cần thực hiện trong phiên bản đầu tiên (MVP).

---

### 1.2.3 Yêu cầu phi chức năng (Non-Functional Requirements)

Bên cạnh các yêu cầu chức năng, hệ thống BookStore cần đáp ứng các yêu cầu phi chức năng sau:

#### **A. Hiệu suất (Performance)**

| ID | Tên yêu cầu | Tiêu chí | Ghi chú |
|----|------------|---------|--------|
| NFR-1.1 | Thời gian phản hồi API | ≤ 500ms cho 90% request | Không tính network latency |
| NFR-1.2 | Thời gian load trang | ≤ 3 giây từ khi nhấp đến hiển thị hoàn toàn | Trên kết nối 3G |
| NFR-1.3 | Số người dùng đồng thời | Ít nhất 100 người dùng truy cập cùng lúc | Có thể mở rộng với caching |
| NFR-1.4 | Tốc độ tìm kiếm | Trả về kết quả tìm kiếm trong ≤ 1 giây | Cho danh sách 10,000+ sách |
| NFR-1.5 | Caching dữ liệu | Cache danh mục, sách, banner lâu hơn 1 giờ | Giảm tải database |
| NFR-1.6 | Tối ưu hóa hình ảnh | Hình bìa sách ≤ 100KB, tối ưu với WebP/JPEG | Giảm tải mạng |

#### **B. Bảo mật (Security)**

| ID | Tên yêu cầu | Tiêu chí | Ghi chú |
|----|------------|---------|--------|
| NFR-2.1 | Mã hóa mật khẩu | Sử dụng BCrypt với salt rounds ≥ 10 | Không lưu plaintext |
| NFR-2.2 | JWT Token | Sử dụng JWT với HMAC-SHA256, thời hạn 24 giờ | Xác thực stateless |
| NFR-2.3 | HTTPS/TLS | Tất cả giao tiếp phải qua HTTPS | Sertifikat SSL/TLS hợp lệ |
| NFR-2.4 | CORS | Cấu hình CORS chặt chẽ, chỉ cho phép origin tin cậy | Ngăn chặn cross-origin attack |
| NFR-2.5 | SQL Injection | Sử dụng Prepared Statement, ORM (JPA) | Không concatenate SQL strings |
| NFR-2.6 | XSS Protection | Escape HTML output, CSP headers | Ngăn chặn script injection |
| NFR-2.7 | CSRF Protection | Sử dụng CSRF tokens cho state-changing requests | Hoặc dùng SameSite cookies |
| NFR-2.8 | Input Validation | Xác thực tất cả input (length, type, pattern) | Backend + Frontend validation |
| NFR-2.9 | Rate Limiting | Giới hạn số request per IP (ví dụ 100 req/phút) | Ngăn chặn brute force attack |
| NFR-2.10 | Bảo mật thanh toán | Tuân thủ PCI-DSS cho tích hợp cổng thanh toán | Không lưu card details |

#### **C. Khả năng sử dụng (Usability)**

| ID | Tên yêu cầu | Tiêu chí | Ghi chú |
|----|------------|---------|--------|
| NFR-3.1 | Giao diện thân thiện | Dễ hiểu, không có jargon kỹ thuật | User testing ≥ 5 người |
| NFR-3.2 | Responsive design | Hoạt động tốt trên desktop, tablet, mobile | Breakpoint: 320px, 768px, 1024px |
| NFR-3.3 | Hỗ trợ trình duyệt | Chrome, Firefox, Safari, Edge (phiên bản cuối 2 năm) | IE không cần hỗ trợ |
| NFR-3.4 | Accessibility | Tuân thủ WCAG 2.1 Level AA (màu, contrast, screen reader) | Alt text cho ảnh, semantic HTML |
| NFR-3.5 | Tốc độ học tập | Người dùng mới có thể mua hàng trong ≤ 5 phút | Walthrough hoặc hướng dẫn |
| NFR-3.6 | Thông báo lỗi rõ ràng | Thông báo lỗi chi tiết, đề xuất cách khắc phục | Không hiển thị stack trace |
| NFR-3.7 | Hỗ trợ tiếng Việt | Giao diện 100% tiếng Việt, không ký tự lỗi | UTF-8 encoding |

#### **D. Độ tin cậy (Reliability)**

| ID | Tên yêu cầu | Tiêu chí | Ghi chí |
|----|------------|---------|--------|
| NFR-4.1 | Uptime | Hệ thống available ≥ 99.5% trong tháng | ~3.6 giờ downtime/tháng |
| NFR-4.2 | Backup dữ liệu | Backup toàn bộ database hàng ngày, lưu ≥ 30 ngày | Off-site backup |
| NFR-4.3 | Disaster Recovery | Có kế hoạch phục hồi sau sự cố, RTO ≤ 4 giờ | Kiểm tra định kỳ |
| NFR-4.4 | Error Logging | Ghi lại tất cả errors, warnings vào log files | Log rotation hàng ngày |
| NFR-4.5 | Monitoring | Giám sát CPU, RAM, disk, database, errors | Alert nếu vượt ngưỡng |
| NFR-4.6 | Graceful Degradation | Nếu một dịch vụ (API) down, hệ thống vẫn hoạt động | Fallback responses |

#### **E. Khả năng mở rộng (Scalability)**

| ID | Tên yêu cầu | Tiêu chí | Ghi chí |
|----|------------|---------|--------|
| NFR-5.1 | Horizontal Scaling | Có thể chạy nhiều instance backend đồng thời | Load balancing ready |
| NFR-5.2 | Database Scaling | Hỗ trợ read replicas, sharding (trong tương lai) | JPA/Hibernate support |
| NFR-5.3 | Caching Layer | Redis hoặc Memcached để cache queries | Giảm tải database |
| NFR-5.4 | Message Queue | RabbitMQ hoặc Kafka cho async tasks (trong tương lai) | Gửi email, notifications |
| NFR-5.5 | CDN | Lưu trữ static files trên CDN (trong tương lai) | Images, CSS, JS |
| NFR-5.6 | Database Connection Pooling | Sử dụng connection pool (HikariCP) | Tối đa 20-50 connections |

#### **F. Bảo trì (Maintainability)**

| ID | Tên yêu cầu | Tiêu chí | Ghi chí |
|----|------------|---------|--------|
| NFR-6.1 | Code Quality | SonarQube score ≥ 80, code duplication < 5% | Code review bắt buộc |
| NFR-6.2 | Unit Test Coverage | ≥ 70% test coverage cho Service layer | Sử dụng JUnit 5 + Mockito |
| NFR-6.3 | API Documentation | Tài liệu API đầy đủ (Swagger/OpenAPI) | Self-documenting code |
| NFR-6.4 | Code Standards | Tuân thủ Google Java Style Guide | IDE auto-format |
| NFR-6.5 | Version Control | Git với branching strategy (GitFlow) | Commit messages chi tiết |
| NFR-6.6 | Deployment Automation | CI/CD pipeline (GitHub Actions hoặc Jenkins) | Automated testing before deploy |

#### **G. Tương thích (Compatibility)**

| ID | Tên yêu cầu | Tiêu chí | Ghi chí |
|----|------------|---------|--------|
| NFR-7.1 | Java Version | Java 21 LTS | Minimum Java 11 |
| NFR-7.2 | Database | MySQL 8.0 hoặc PostgreSQL 12+ | Compatible SQL dialects |
| NFR-7.3 | Framework | Spring Boot 3.2.3+ | Tương thích mới nhất |
| NFR-7.4 | API Compatibility | Backward compatible với previous versions | Semantic versioning |
| NFR-7.5 | Browser Compatibility | HTML5, CSS3, ES6+ JavaScript | Fallback cho older browsers |

#### **H. Tuân thủ (Compliance)**

| ID | Tên yêu cầu | Tiêu chí | Ghi chí |
|----|------------|---------|--------|
| NFR-8.1 | Bảo vệ dữ liệu cá nhân | Tuân thủ PDPA (Luật Bảo vệ Dữ liệu Cá nhân) | Có chính sách privacy |
| NFR-8.2 | Điều khoản sử dụng | Có ToS rõ ràng, công bằng | Hiển thị khi đăng ký |
| NFR-8.3 | Chính sách hoàn trả | Có chính sách hoàn tiền, hoàn sách | Hợp pháp, bảo vệ consumer |
| NFR-8.4 | Hoá đơn/Hóa đơn | Cấp hoá đơn điện tử hợp pháp (nếu bán theo quy định) | XML format |

**Tóm tắt**: Hệ thống có **59 yêu cầu phi chức năng** được chia thành **8 nhóm** chính (hiệu suất, bảo mật, usability, tin cậy, mở rộng, bảo trì, tương thích, tuân thủ).

---

## 1.3 TÓNG HỢP YÊU CẦU VÀ PHẠM VI DỰ ÁN

### 1.3.1 Phạm vi hệ thống

**Phạm vi bao gồm (In Scope)**:
- Xây dựng website bán sách trực tuyến hoàn chỉnh
- Backend REST API (Java Spring Boot)
- Frontend web responsive (HTML5, CSS3, JavaScript)
- Database MySQL với schema tối ưu
- Tích hợp Google Books API, Google Gemini API, SePay Payment Gateway
- Admin Dashboard để quản lý hệ thống
- System cho phép thanh toán COD, SePay online

**Phạm vi không bao gồm (Out of Scope)**:
- Mobile app (iOS/Android) - xem xét trong tương lai
- Tích hợp VNPAY/Momo ngay (có thể add sau)
- Machine Learning recommendations (sử dụng rules-based recommendation)
- Multi-language support (chỉ Tiếng Việt)
- Cấp các tính năng premium quá phức tạp (ví dụ: nhập sách từ nhà cung cấp tự động)

### 1.3.2 Các ràng buộc

**Ràng buộc kỹ thuật**:
- Backend: Java 21, Spring Boot 3.2.3, MySQL 8.0
- Frontend: HTML5, CSS3, Vanilla JavaScript (không dùng frameworks hạng nặng)
- Hosting: Có thể deployment trên cloud (AWS, GCP, Azure) hoặc VPS

**Ràng buộc về thời gian**:
- MVP (Minimum Viable Product): 2-3 tháng phát triển
- Phát hành phiên bản đầu tiên: Quý 3/2026

**Ràng buộc về nguồn lực**:
- Team: 2-3 developer (1 backend, 1 frontend, 1 DevOps/QA)
- Kinh phí: Hosting, API keys (Google Books, Gemini, SePay) - ước tính ~100-200 USD/tháng

---

## KẾT LUẬN CHƯƠNG I

Chương I đã trình bày chi tiết:

1. **Mô tả bài toán**: 
   - Lý do chọn đề tài "Xây dựng website bán sách trực tuyến BookStore"
   - Bối cảnh thị trường TMĐT Việt Nam đang phát triển mạnh
   - Vấn đề kinh doanh: Nhu cầu bán sách online tăng, nên cần nền tảng TMĐT chuyên biệt
   - Mục tiêu kinh doanh rõ ràng

2. **Khảo sát yêu cầu bài toán**:
   - Quy trình khảo sát gồm 4 bước: phân tích bối cảnh, phỏng vấn user, khảo sát chức năng, khảo sát phi chức năng
   - Xác định 48 yêu cầu chức năng (FR) chia thành 10 nhóm
   - Xác định 59 yêu cầu phi chức năng (NFR) chia thành 8 nhóm
   - Rõ ràng phạm vi và ràng buộc

Những yêu cầu này sẽ là cơ sở cho các chương tiếp theo về thiết kế hệ thống (BFD, DFD), kiến trúc kỹ thuật, và phát triển chi tiết.

