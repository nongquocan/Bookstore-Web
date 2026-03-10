# 🏪 BookStore Backend - Java Spring Boot

## Project Structure

```
Backend-Java/
├── pom.xml                                    # Maven configuration
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/bookstore/
│   │   │       ├── BookStoreApplication.java  # Entry point
│   │   │       ├── controller/
│   │   │       │   ├── BookController.java
│   │   │       │   └── CategoryController.java
│   │   │       ├── entity/
│   │   │       │   ├── Book.java
│   │   │       │   ├── Category.java
│   │   │       │   └── User.java
│   │   │       ├── repository/
│   │   │       │   ├── BookRepository.java
│   │   │       │   └── CategoryRepository.java
│   │   │       ├── service/
│   │   │       │   ├── BookService.java
│   │   │       │   └── GoogleBooksService.java
│   │   │       ├── dto/
│   │   │       │   ├── BookDTO.java
│   │   │       │   └── ApiResponse.java
│   │   │       ├── exception/
│   │   │       │   ├── GlobalExceptionHandler.java
│   │   │       │   └── EntityNotFoundException.java
│   │   │       └── config/
│   │   │           └── CorsConfig.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/bookstore/  # Tests (future)
└── target/                      # Build output
```

## Prerequisites

- Java 17 or higher
- Maven 3.8+
- MySQL 8.0
- Git

## Quick Start

### 1. Database Setup

```sql
-- Create database
CREATE DATABASE IF NOT EXISTS bookstore_db;

-- Create tables (import from Backend/db/schema.sql)
mysql -u root -padmin2003 bookstore_db < Backend/db/schema.sql
```

### 2. Build the Project

```bash
cd Backend-Java

# Build with Maven
mvn clean install

# Skip tests (faster)
mvn clean install -DskipTests
```

### 3. Run the Application

```bash
# Option 1: Maven Spring Boot plugin
mvn spring-boot:run

# Option 2: Run JAR file (after build)
java -jar target/bookstore-backend-1.0.0.jar
```

### 4. Verify Server is Running

```bash
# Check health endpoint
curl http://localhost:8080/api/books/health

# Response:
# {
#   "success": true,
#   "message": "BookStore API is running",
#   "data": {
#     "status": "ok"
#   }
# }
```

## API Endpoints

### Books API

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/books` | Get all books with pagination |
| GET | `/api/books/:id` | Get book by ID |
| GET | `/api/books/search/:query` | Search books |
| GET | `/api/books/featured/list` | Get featured books |
| GET | `/api/books/new/list` | Get new books |
| GET | `/api/books/flash-sale/list` | Get flash sale books |
| GET | `/api/books/category/:name` | Get books by category |
| GET | `/api/books/stats/overview` | Get statistics |
| POST | `/api/books/sync` | Sync from Google Books |
| GET | `/api/books/health` | Health check |

### Categories API

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/categories` | Get all categories |
| GET | `/api/categories/:id` | Get category by ID |

## Example Requests

### Get All Books
```bash
curl "http://localhost:8080/api/books?page=1&limit=12&sort=newest"
```

### Search Books
```bash
curl "http://localhost:8080/api/books/search/python?limit=20"
```

### Get Featured Books
```bash
curl "http://localhost:8080/api/books/featured/list?limit=8"
```

### Sync from Google Books
```bash
curl -X POST "http://localhost:8080/api/books/sync?query=lap-trinh&limit=40"
```

## Configuration

### application.properties

```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/bookstore_db
spring.datasource.username=root
spring.datasource.password=admin2003

# Server
server.port=8080

# Google Books API
google.books.api.key=AIzaSyCD69vesoQ5fpcjDVPlks6gzqhFyV9dvaA
```

## Troubleshooting

### MySQL Connection Error
```
com.mysql.cj.jdbc.exceptions.CommunicationsException
```
**Solution**: 
- Ensure MySQL is running: `net start MySQL80` (Windows)
- Check credentials in `application.properties`
- Verify database exists: `mysql -u root -padmin2003 -e "SHOW DATABASES;"`

### Google Books API Error
```
403 Forbidden - Invalid API Key
```
**Solution**:
- Check API key in `application.properties`
- Verify API key is enabled in Google Cloud Console
- Check API quota limits

### Port Already in Use
```
Address already in use
```
**Solution**:
```bash
# Change port in application.properties
server.port=8081

# Or kill process on port 8080
# Windows: netstat -ano | findstr :8080
# Linux: lsof -i :8080
```

## Features

✅ **Book Management**
- Full-text search across title, author, description
- Pagination and sorting
- Filter by category, price, rating
- Featured and new books sections
- Flash sale (discount > 25%)

✅ **Google Books Integration**
- Search Google Books API
- Auto-sync to MySQL database
- Price calculation and discount generation
- Book validation (>50 pages, has image)

✅ **API Features**
- RESTful endpoints
- CORS enabled for frontend
- Global exception handling
- Request validation
- Pagination with customizable limits

✅ **Database**
- MySQL with 9 tables
- Foreign key relationships
- Indexed searches
- Timestamp tracking

## Development

### Code Style
- Follow Spring conventions
- Use Lombok for boilerplate
- Log with Slf4j
- Transaction management with @Transactional

### Testing
```bash
# Run tests
mvn test

# Run specific test
mvn test -Dtest=BookServiceTest
```

### Logging

Set logging level in `application.properties`:
```properties
logging.level.com.bookstore=DEBUG
logging.level.org.springframework.web=DEBUG
```

## Security Notes

⚠️ **API Key Exposed**
The Google Books API key is visible in configuration files. For production:
1. Use environment variables
2. Use Spring Cloud Config
3. Use AWS Secrets Manager
4. Rotate keys regularly

⚠️ **Database Credentials**
Never commit real credentials. Use `.gitignore`:
```
.env
application-prod.properties
```

## Performance Optimization

- Database indexes on: title, category, google_id, created_at
- Pagination limit: max 100 items per page
- Search results: max 20 items
- Disable SQL logging in production

## Next Steps

1. ✅ Controllers created
2. ✅ Configuration complete
3. ⏳ Connect to frontend (update main.js API_URL)
4. ⏳ Add authentication/JWT
5. ⏳ Add API documentation (Swagger)
6. ⏳ Add unit tests
7. ⏳ Deploy to cloud (Azure, AWS, Heroku)

---

**Last Updated**: 2024
**Version**: 1.0.0
**Status**: Ready for Testing
