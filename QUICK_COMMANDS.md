# 🚀 Quick Commands - BookStore Project

## Database Setup (One-time)

```bash
# 1. Start MySQL
net start MySQL80

# 2. Create database
mysql -u root -padmin2003 -e "CREATE DATABASE bookstore_db;"

# 3. Import schema
mysql -u root -padmin2003 bookstore_db < Backend/db/schema.sql

# 4. Verify
mysql -u root -padmin2003 bookstore_db -e "SELECT COUNT(*) as total_tables FROM information_schema.tables WHERE table_schema='bookstore_db';"
```

## Running the Application (Daily)

### Terminal 1: Start Backend

```bash
cd Backend-Java

# Option A: Development (with auto-reload)
mvn spring-boot:run

# Option B: Build and run JAR
mvn clean install -DskipTests
java -jar target/bookstore-backend-1.0.0.jar
```

Expected output:
```
╔════════════════════════════════════════════╗
║     🏪 BookStore Backend Started 🏪       ║
║  ✅ Server running on http://localhost:8080  ║
║  ✅ API: http://localhost:8080/api/books    ║
╚════════════════════════════════════════════╝
```

### Terminal 2: Start Frontend

```bash
# Option A: VS Code Live Server
# Right-click index.html → Open with Live Server
# Access: http://localhost:5500

# Option B: Python HTTP Server
cd d:\Web-site
python -m http.server 8000
# Access: http://localhost:8000

# Option C: Node.js HTTP Server
npx http-server
# Access: http://localhost:8080
```

## Testing API

```bash
# Health check
curl http://localhost:8080/api/books/health

# List all books
curl http://localhost:8080/api/books

# Search books
curl http://localhost:8080/api/books/search/python

# Get featured books
curl http://localhost:8080/api/books/featured/list

# Sync from Google Books
curl -X POST "http://localhost:8080/api/books/sync?query=lap-trinh&limit=40"
```

## Database Management

```bash
# Connect to database
mysql -u root -padmin2003 bookstore_db

# View tables
SHOW TABLES;

# Check book count
SELECT COUNT(*) FROM books;

# View all books
SELECT * FROM books LIMIT 10;

# View categories
SELECT * FROM categories;

# Delete all books (for reset)
DELETE FROM books;

# Backup database
mysqldump -u root -padmin2003 bookstore_db > backup.sql

# Restore database
mysql -u root -padmin2003 bookstore_db < backup.sql
```

## Syncing Products from Google Books

```bash
# Sync books on specific topic
curl -X POST "http://localhost:8080/api/books/sync?query=lap-trinh&limit=40"

# Sync multiple categories
for category in "lap-trinh" "kinh-doanh" "tam-ly" "lich-su" "tieu-thuyet" "phat-trien"; do
  curl -X POST "http://localhost:8080/api/books/sync?query=$category&limit=40"
done
```

## Development Workflow

### Making Backend Changes

```bash
# 1. Edit Java files
# 2. Save file (auto-reload if DevTools enabled)
# 3. Or stop & restart server
#    Ctrl+C (stop current server)
#    mvn spring-boot:run (start new server)
```

### Making Frontend Changes

```bash
# 1. Edit HTML/CSS/JS files
# 2. Live Server auto-refresh
#    Or manual refresh: F5 or Ctrl+R
```

### Making Database Changes

```bash
# 1. Edit schema.sql if needed
# 2. Backup current database
#    mysqldump -u root -padmin2003 bookstore_db > backup.sql
# 3. Drop and recreate
#    mysql -u root -padmin2003 -e "DROP DATABASE bookstore_db; CREATE DATABASE bookstore_db;"
# 4. Reimport schema
#    mysql -u root -padmin2003 bookstore_db < Backend/db/schema.sql
# 5. Sync data from Google Books
#    (Use curl commands above)
```

## Troubleshooting

### Port Already in Use

```bash
# Check what's using port 8080
netstat -ano | findstr :8080

# Kill process (Windows)
taskkill /PID <PID> /F

# Or change port in application.properties
server.port=8081
```

### MySQL Connection Error

```bash
# Start MySQL
net start MySQL80

# Stop MySQL  
net stop MySQL80

# Check MySQL status
mysql -u root -padmin2003 -e "SELECT 1;"
```

### CORS Error

```
Access to XMLHttpRequest from origin 'http://localhost:5500' blocked
```

Solution: CORS already configured in CorsConfig.java
- Clear browser cache: Ctrl+Shift+Delete
- Hard refresh: Ctrl+Shift+R
- Check backend is running: http://localhost:8080/api/books/health

### Products Not Loading

```javascript
// Check in browser console (F12)
// If message says "Loaded from local JSON", backend is not running

// To debug:
fetch('http://localhost:8080/api/books')
  .then(r => r.json())
  .then(d => console.log(d))
  .catch(e => console.error(e))
```

## Performance Optimization

```bash
# Clean cache and rebuild
mvn clean install

# Skip tests for faster build
mvn clean install -DskipTests

# Run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Check database indexes
mysql -u root -padmin2003 bookstore_db -e "SHOW INDEX FROM books;"
```

## Deployment Preview

```bash
# Build production JAR
mvn clean package -DskipTests

# Create Docker image (optional)
docker build -t bookstore-backend:1.0.0 .

# Run Docker container
docker run -p 8080:8080 bookstore-backend:1.0.0
```

## Important Configuration Files

### Backend Config
- `Backend-Java/src/main/resources/application.properties`
  - Database credentials
  - Server port
  - Google API key

### Frontend Config  
- `assets/js/main.js` (lines 1-10)
  - `API_CONFIG.BASE_URL` - Backend URL
  - `API_CONFIG.USE_BACKEND` - Toggle between API and local data

## First-Time Setup Checklist

- [ ] MySQL installed and running
- [ ] Database created: `bookstore_db`
- [ ] Schema imported from `Backend/db/schema.sql`
- [ ] Backend compiled: `mvn clean install`
- [ ] Backend running: `mvn spring-boot:run`
- [ ] Frontend accessible: Open `index.html`
- [ ] API health check: `curl http://localhost:8080/api/books/health`
- [ ] Products loading in frontend
- [ ] Console shows "✅ Loaded X products from Backend API"

## Daily Workflow

```bash
# Morning startup (run in separate terminals)
Terminal 1: net start MySQL80 && cd Backend-Java && mvn spring-boot:run
Terminal 2: cd d:\Web-site && (Open index.html with Live Server or http-server)

# Do development work...

# Evening shutdown
Ctrl+C in each terminal

# Optional cleanup
Terminal 1: net stop MySQL80
```

## Useful Links

- 📖 [Backend Setup Guide](Backend-Java/SETUP.md)
- 🔗 [Integration Guide](INTEGRATION.md)
- 🎯 [Project Summary](PROJECT_SUMMARY.md)
- 📚 [README (Vietnamese)](README.md)

---

**Template Created**: 2024  
**Last Updated**: 2024  
**Status**: ✅ Ready to Use
