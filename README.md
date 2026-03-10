# BookStore - Website Thương Mại Điện Tử Bán Sách

## 📋 Mô Tả Dự Án

BookStore là một website thương mại điện tử B2C hiện đại dành cho bán sách online. Website được thiết kế theo nguyên tắc **Mobile-First** với giao diện tối giản, hiện đại và dễ sử dụng, phù hợp cho người dùng từ 18-45 tuổi tại Việt Nam.

## 🎯 Tính Năng Chính

### 1. **Trang Chủ (Homepage)**
- ✅ Sticky Header với logo, thanh tìm kiếm thông minh, giỏ hàng, menu danh mục
- ✅ Carousel banner tự động chuyển slide (quảng cáo, khuyến mãi)
- ✅ Flash Sale với countdown timer
- ✅ Danh mục sản phẩm nổi bật (icon grid)
- ✅ Sản phẩm gợi ý cá nhân hóa
- ✅ Footer đầy đủ thông tin

### 2. **Trang Danh Mục & Tìm Kiếm**
- ✅ Sidebar filter: giá, danh mục, đánh giá, tình trạng
- ✅ Sorting: mới nhất, bán chạy, giá, đánh giá
- ✅ Hiển thị grid/list
- ✅ Pagination

### 3. **Trang Chi Tiết Sản Phẩm**
- ✅ Gallery ảnh với zoom
- ✅ Thông tin chi tiết (giá, SKU, mô tả, tình trạng kho)
- ✅ Nút Add to Cart & Buy Now
- ✅ Review section với rating
- ✅ Sản phẩm liên quan

### 4. **Trang Giỏ Hàng**
- ✅ Danh sách sản phẩm (ảnh, tên, giá, số lượng)
- ✅ Tóm tắt đơn hàng
- ✅ Ô nhập voucher
- ✅ Nút Checkout

### 5. **Trang Checkout**
- ✅ 3 bước: Địa chỉ giao hàng → Phương thức vận chuyển → Thanh toán
- ✅ Phương thức vận chuyển (Standard, Express, Same-day)
- ✅ Phương thức thanh toán (COD, VNPay, MoMo, Card)
- ✅ Order Summary

### 6. **Trang Tài Khoản**
- ✅ Dashboard với thống kê
- ✅ Order History
- ✅ Address Management
- ✅ Wishlist
- ✅ Change Password

### 7. **Trang Đăng Nhập / Đăng Ký**
- ✅ Login form & Register form
- ✅ Social login (Google, Facebook)
- ✅ Validation form

## 🛠️ Công Nghệ Sử Dụng

- **Frontend**: HTML5, CSS3, JavaScript (Vanilla)
- **Framework**: Bootstrap 5 (via CDN)
- **Icons**: FontAwesome 6.4
- **Storage**: LocalStorage (cho cart, favorites, user info)
- **Responsive**: Mobile-First design

## 📁 Cấu Trúc Thư Mục

```
Web-site/
├── index.html                 # Trang chủ
├── assets/
│   ├── css/
│   │   ├── style.css         # CSS chính
│   │   ├── cart.css          # CSS trang giỏ hàng
│   │   ├── checkout.css      # CSS trang thanh toán
│   │   └── account.css       # CSS trang tài khoản
│   ├── js/
│   │   └── main.js           # JavaScript chính
│   ├── data/
│   │   └── products.json     # Dữ liệu sản phẩm
│   └── images/               # Thư mục ảnh
├── pages/
│   ├── products.html         # Danh mục & Tìm kiếm
│   ├── product-detail.html   # Chi tiết sản phẩm
│   ├── cart.html            # Giỏ hàng
│   ├── checkout.html        # Thanh toán
│   ├── login.html           # Đăng nhập
│   ├── account.html         # Tài khoản
│   └── register.html        # Đăng ký (trong login.html)
└── README.md
```

## 🚀 Cách Chạy Project

### Cách 1: Dùng Live Server (VS Code)
1. Cài đặt extension "Live Server"
2. Right-click vào `index.html` → "Open with Live Server"
3. Browser sẽ mở tự động tại `http://127.0.0.1:5500`

### Cách 2: Dùng Python
```bash
# Python 3
python -m http.server 8000

# Hoặc Python 2
python -m SimpleHTTPServer 8000
```
Truy cập `http://localhost:8000`

### Cách 3: Dùng Node.js
```bash
# Cài đặt http-server
npm install -g http-server

# Chạy
http-server
```

## 💾 Dữ Liệu Mẫu

Project bao gồm 12 cuốn sách mẫu với thông tin:
- ID, tên, tác giả, danh mục
- Giá gốc, giá sale, % giảm giá
- Rating, số lượng đánh giá
- Mô tả chi tiết
- Ảnh (placeholder từ Unsplash)

Dữ liệu được lưu trong `assets/data/products.json`

## 🎨 Thiết Kế Giao Diện

### Màu Sắc
- **Primary**: #d32f2f (Đỏ)
- **Secondary**: #1976d2 (Xanh)
- **Background**: #f5f5f5
- **Text**: #212121

### Breakpoints
- **Mobile**: < 576px
- **Tablet**: 576px - 1024px
- **Desktop**: > 1024px

### Typography
- **Font**: Segoe UI, Tahoma, Geneva, Verdana
- **Heading**: Font-weight 700
- **Body**: Font-weight 400-500

## ⚙️ Tính Năng JavaScript

### APP Object (Main Controller)
```javascript
APP.products       // Array sản phẩm
APP.cart          // Giỏ hàng
APP.favorites     // Danh sách yêu thích
APP.currentUser   // Người dùng hiện tại

// Phương thức chủ yếu
APP.addToCart(productId, quantity)
APP.removeFromCart(productId)
APP.updateCartQuantity(productId, quantity)
APP.toggleFavorite(productId)
APP.formatPrice(price)            // Định dạng VND
APP.showNotification(message, type)
```

### LocalStorage Keys
```javascript
'cart'           // Giỏ hàng
'favorites'      // Sách yêu thích
'currentUser'    // Thông tin người dùng
```

## 🔒 Bảo Mật & Tối Ưu

### Accessibility (WCAG 2.1)
- ✅ Semantic HTML
- ✅ ARIA labels
- ✅ Keyboard navigation
- ✅ Screen reader support

### Performance
- ✅ Lazy loading images
- ✅ CSS minified
- ✅ Efficient DOM manipulation
- ✅ CSS Grid/Flexbox

### Mobile Optimization
- ✅ Responsive viewport
- ✅ Touch-friendly buttons
- ✅ Readable font sizes
- ✅ Fast loading

## 📱 Responsive Breakpoints

| Device | Width | CSS Media Query |
|--------|-------|-----------------|
| Mobile | < 576px | `@media (max-width: 576px)` |
| Tablet | 576-1024px | `@media (max-width: 1024px)` |
| Desktop | > 1024px | Default |

## 🧪 Testing

### Test Checklist
- [ ] Trang chủ load đầy đủ sản phẩm
- [ ] Carousel chuyển slide tự động
- [ ] Countdown timer hoạt động
- [ ] Add to cart lưu vào localStorage
- [ ] Search gợi ý tìm kiếm
- [ ] Filter sản phẩm hoạt động
- [ ] Responsive layout trên mobile/tablet/desktop
- [ ] Form validation hoạt động
- [ ] Pagination hoạt động

## 🐛 Known Issues & TODO

### Todo
- [ ] Kết nối backend API
- [ ] Thanh toán thực tế
- [ ] Email verification
- [ ] Two-factor authentication
- [ ] Visual search (tìm kiếm bằng hình)
- [ ] Chatbot hỗ trợ

### Hạn Chế
- Dữ liệu được lưu local, không persistent trên server
- Thanh toán là mock (chỉ frontend)
- Không có backend authentication

## 📝 Hướng Dẫn Customize

### Thay đổi Logo
Chỉnh sửa HTML trong header:
```html
<a href="index.html" class="logo">
  <i class="fas fa-book-open"></i>
  <span>BookStore</span>
</a>
```

### Thay đổi Màu Sắc
Chỉnh sửa biến CSS trong `assets/css/style.css`:
```css
:root {
  --primary-color: #d32f2f;      /* Màu chính */
  --secondary-color: #1976d2;    /* Màu phụ */
  /* ... */
}
```

### Thêm Sản Phẩm
Chỉnh sửa `assets/data/products.json` và thêm object:
```json
{
  "id": 13,
  "name": "Tên sách",
  "author": "Tác giả",
  "category": "Danh mục",
  "price": 199000,
  "originalPrice": 280000,
  "image": "URL ảnh",
  "rating": 4.8,
  "reviews": 2543,
  "quantity": 100,
  "discount": 29,
  "isNew": true,
  "isFeatured": false,
  "sku": "SKU001"
}
```

## 📚 Tài Liệu Thêm

- [Bootstrap 5 Documentation](https://getbootstrap.com/docs/5.0/)
- [FontAwesome Icons](https://fontawesome.com/icons)
- [MDN Web Docs](https://developer.mozilla.org/)

## 👥 Tác Giả

Dự án được tạo bởi GitHub Copilot.

## 📄 License

Dự án này được phát hành dưới license MIT.

---

## 💡 Tips & Tricks

### Tắt Notification
```javascript
// Comment dòng này trong main.js
// this.showNotification('Message', 'type');
```

### Thay đổi Giá Miễn phí Vận chuyển
Tìm trong cart.html & checkout.html:
```javascript
const shipping = subtotal > 200000 ? 0 : 30000;
```

### Thêm Breakpoint Mới
Trong style.css:
```css
@media (max-width: 1200px) {
  /* Thêm CSS mới */
}
```

---

**Website được thiết kế với ❤️ để cung cấp trải nghiệm mua sắm sách trực tuyến tốt nhất!**
