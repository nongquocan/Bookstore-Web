# 📚 BookStore Backend - Spring Boot API

## Quick Start

```bash
cd Backend-Java

# Build
mvn clean install -DskipTests

# Run
mvn spring-boot:run

# Server: http://localhost:8080
```

## API Endpoints

### Books API

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/books` | GET | Lấy tất cả sách |
| `/api/books/{id}` | GET | Lấy sách theo ID |
| `/api/books/search/{query}` | GET | Tìm kiếm sách |
| `/api/books/featured/list` | GET | Sách nổi bật |
| `/api/books/new/list` | GET | Sách mới |
| `/api/books/flash-sale/list` | GET | Flash sale |
| `/api/books/category/{name}` | GET | Sách theo danh mục |
| `/api/books/stats/overview` | GET | Thống kê |
| `/api/books/sync` | POST | Sync từ Google Books |

### Categories API

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/categories` | GET | Lấy tất cả danh mục |
| `/api/categories/{id}` | GET | Lấy danh mục theo ID |

## Features

- ✅ RESTful API
- ✅ Full-text search
- ✅ Pagination & sorting
- ✅ Google Books API integration
- ✅ CORS enabled
- ✅ Error handling
- ✅ MySQL database
- ✅ Transaction management

## Configuration

File: `src/main/resources/application.properties`

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/bookstore_db
spring.datasource.username=root
spring.datasource.password=admin2003
google.books.api.key=AIzaSyCD69vesoQ5fpcjDVPlks6gzqhFyV9dvaA
server.port=8080
```

## Example Requests

```bash
# Get all books
curl http://localhost:8080/api/books

# Search books
curl http://localhost:8080/api/books/search/python

# Get featured books
curl http://localhost:8080/api/books/featured/list

# Sync from Google Books
curl -X POST "http://localhost:8080/api/books/sync?query=lap-trinh&limit=40"
```

## Project Structure

```
Backend-Java/
├── src/main/java/com/bookstore/
│   ├── BookStoreApplication.java
│   ├── controller/
│   │   ├── BookController.java
│   │   └── CategoryController.java
│   ├── service/
│   │   ├── BookService.java
│   │   └── GoogleBooksService.java
│   ├── repository/
│   │   ├── BookRepository.java
│   │   └── CategoryRepository.java
│   ├── entity/
│   │   ├── Book.java
│   │   ├── Category.java
│   │   └── User.java
│   ├── dto/
│   │   ├── BookDTO.java
│   │   └── ApiResponse.java
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java
│   │   └── EntityNotFoundException.java
│   └── config/
│       └── CorsConfig.java
├── src/main/resources/
│   ├── application.properties
│   └── application-prod.properties
├── pom.xml
└── SETUP.md
```

## Troubleshooting

**MySQL Connection Error**
```bash
# Start MySQL
net start MySQL80

# Verify
mysql -u root -padmin2003 -e "SELECT 1;"
```

**Port 8080 Already in Use**
```properties
# Change in application.properties
server.port=8081
```

**Google API Error**
- Check API key in application.properties
- Verify API is enabled in Google Cloud

## Next Steps

- [ ] Add user authentication (JWT)
- [ ] Add order management
- [ ] Add payment processing
- [ ] Add API documentation (Swagger)
- [ ] Add unit tests
- [ ] Deploy to cloud

---

**Status**: ✅ Ready for Testing  
**Version**: 1.0.0  
**Java**: 17  
**Spring Boot**: 3.2.3  
**Database**: MySQL 8.0
