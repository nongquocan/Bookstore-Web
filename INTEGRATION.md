# 🔌 Kết Nối Frontend-Backend

## Overview

Sau khi hoàn thành cả Frontend và Backend, bạn cần kết nối chúng lại. Hiện tại, Frontend đang sử dụng dữ liệu từ `products.json` cục bộ. Hướng dẫn này sẽ giúp bạn kết nối Frontend đến Backend API.

## Trạng Thái Hiện Tại

**Frontend** ✅
- Đã hoàn thành toàn bộ UI
- Sử dụng localStorage để lưu trữ cart, favorites
- Có hỗ trợ cho cả API backend và local JSON (fallback)

**Backend** ✅
- API Spring Boot chạy trên `http://localhost:8080`
- Tất cả endpoints đã hoàn thành
- Database MySQL đã được setup

## Setup

### 1. Đảm bảo MySQL đang chạy

```bash
# Windows
net start MySQL80

# Verify
mysql -u root -padmin2003 -e "SELECT 1;"
```

### 2. Khởi động Backend Server

```bash
cd Backend-Java

# Build (lần đầu)
mvn clean install -DskipTests

# Run
mvn spring-boot:run

# Expected output:
# 🏪 BookStore Backend Started
# ✅ Server running on http://localhost:8080
# ✅ API: http://localhost:8080/api/books
```

### 3. Kiểm tra Backend Health

```bash
# Test endpoint
curl http://localhost:8080/api/books/health

# Expected response:
# {
#   "success": true,
#   "message": "Request successful",
#   "data": {
#     "status": "ok",
#     "message": "BookStore API is running"
#   }
# }
```

### 4. Khởi động Frontend

**Option A: Sử dụng Live Server (VS Code)**
```
1. Right-click index.html
2. Select "Open with Live Server"
3. Browser sẽ mở http://localhost:5500
```

**Option B: Sử dụng Python HTTP Server**
```bash
# Navigate to project root
cd d:\Web-site

# Python 3
python -m http.server 8000

# Access: http://localhost:8000
```

**Option C: Sử dụng Node.js HTTP Server**
```bash
npm install -g http-server
http-server
```

### 5. Kiểm tra Kết Nối

1. Mở DevTools (F12)
2. Mở Console tab
3. Kiểm tra các message:

```
✅ Loaded 100 products from Backend API
```

Hoặc nếu Backend không chạy:
```
⚠️ Fallback to local data...
📄 Loaded X products from local JSON
```

## Cấu Hình

### Frontend API Configuration

File: `assets/js/main.js` (dòng 1-10)

```javascript
const API_CONFIG = {
    BASE_URL: 'http://localhost:8080/api',
    TIMEOUT: 5000,
    USE_BACKEND: true  // Đặt thành false để sử dụng local JSON
};
```

**Tùy chỉnh:**
- `USE_BACKEND: true` - Sử dụng Backend API
- `USE_BACKEND: false` - Sử dụng local products.json (fallback)
- `BASE_URL` - Thay đổi nếu Backend chạy trên port khác

### Backend Configuration

File: `Backend-Java/src/main/resources/application.properties`

```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/bookstore_db
spring.datasource.username=root
spring.datasource.password=admin2003

# Server Port
server.port=8080

# Google Books API Key
google.books.api.key=AIzaSyCD69vesoQ5fpcjDVPlks6gzqhFyV9dvaA
```

## Troubleshooting

### ❌ CORS Error
```
Access to XMLHttpRequest from origin 'http://localhost:5500' 
has been blocked by CORS policy
```

**Giải pháp:**
- CORS đã được enable cho `localhost:5500` trong `CorsConfig.java`
- Nếu Backend không nhận được request, kiểm tra:
  1. Backend đang chạy? (http://localhost:8080/api/books/health)
  2. Firewall cho phép port 8080?
  3. Browser có cache cũ?

```javascript
// Clear cache: Ctrl+Shift+Delete hoặc
console.clear();
location.reload(true);
```

### ❌ MySQL Connection Error
```
Error: connect ECONNREFUSED 127.0.0.1:3306
```

**Giải pháp:**
```bash
# 1. Start MySQL
net start MySQL80

# 2. Verify connection
mysql -u root -padmin2003 -e "SELECT 1;"

# 3. Restart Backend
mvn spring-boot:run
```

### ❌ Port 8080 Already in Use
```
Address [localhost]:8080 already in use
```

**Giải pháp:**
```bash
# Find process on port 8080
netstat -ano | findstr :8080

# Kill process (Windows)
taskkill /PID <PID> /F

# Or change port in application.properties
server.port=8081
```

### ❌ Products Not Loading
```
Loaded 0 products from Backend API
```

**Giải pháp:**
1. Kiểm tra database có dữ liệu: 
   ```sql
   mysql -u root -padmin2003 bookstore_db -e "SELECT COUNT(*) FROM books;"
   ```

2. Nếu bảng trống, sync dữ liệu từ Google Books:
   ```bash
   curl -X POST "http://localhost:8080/api/books/sync?query=lap-trinh&limit=40"
   ```

3. Hoặc enable fallback mode:
   ```javascript
   API_CONFIG.USE_BACKEND = false;
   ```

## Features After Connection

Sau khi kết nối thành công, các tính năng sau sẽ hoạt động:

✅ **Real-time Data**
- Products được load từ database MySQL
- Dữ liệu được cập nhật khi sync từ Google Books

✅ **Search & Filter**
- Tìm kiếm toàn bộ database
- Filter theo category, price, rating
- Sorting: newest, price, rating, bestseller

✅ **Product Details**
- Thông tin chi tiết từ Google Books
- Giá được tính toán tự động
- Discount percentage cập nhật

✅ **Sync Products**
- Admin có thể sync sách mới từ Google Books
- Tự động tính giá và discount

## Development Workflow

### Making Changes

**Frontend Changes:**
```bash
# Edit files in assets/
# Live Server auto-reload (F5 to manual refresh)
```

**Backend Changes:**
```bash
# Edit Java files
# Stop server: Ctrl+C
# Run lại: mvn spring-boot:run
# Or enable DevTools for auto-reload
```

**Database Changes:**
```bash
# Edit schema.sql
# Stop MySQL: net stop MySQL80
# Backup: mysqldump -u root -padmin2003 bookstore_db > backup.sql
# Import changes: mysql -u root -padmin2003 bookstore_db < schema.sql
```

## Next Steps

1. ✅ Backend API Running
2. ✅ Frontend Connected
3. ⏳ **Add User Authentication**
   - Implement JWT login/register
   - Protect API endpoints
4. ⏳ **Add Order Management**
   - Cart → Order conversion
   - Order history in DB
5. ⏳ **Add Payment Processing**
   - Integrate payment gateway (VNPay, Stripe)
6. ⏳ **Add Admin Dashboard**
   - Product management
   - Order management
   - Statistics

## Testing Checklist

- [ ] Backend API responds to requests
- [ ] Frontend loads products from API
- [ ] Search and filter work
- [ ] Add to cart works
- [ ] LocalStorage persists data
- [ ] CORS issues resolved
- [ ] No console errors
- [ ] Mobile responsive

## Support

**Common Issues:**
1. MySQL not running → `net start MySQL80`
2. Port in use → Change port or kill process
3. CORS error → Check Backend running & CorsConfig
4. No products → Sync from Google Books or check DB
5. API Error → Check logs in Backend console

**Debug Mode:**
```javascript
// Enable in console
API_CONFIG.DEBUG = true;

// Then check console logs for detailed info
```

## Performance Tips

- 🚀 Frontend caching: Products loaded once, using localStorage
- 🚀 Backend pagination: Max 100 items per page
- 🚀 Search optimization: Indexed database columns
- 🚀 API compression: Enabled in application.properties

---

**Last Updated**: 2024  
**Status**: ✅ Ready for Integration Testing
