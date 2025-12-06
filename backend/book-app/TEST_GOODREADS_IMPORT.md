# Testing Goodreads Import Feature

## Prerequisites

1. **Start the database** (if using Docker):
   ```bash
   cd /Users/ducvo/workspace/github.com/ducvotm/book-project
   docker-compose up -d db
   ```

2. **Start the Spring Boot server**:
   ```bash
   cd backend/book-app
   mvn spring-boot:run
   ```
   
   Wait until you see: `Started BookProjectApplication`

## Testing Methods

### Method 1: Using the Test Script (Recommended)

Once the server is running, open a **new terminal** and run:

```bash
cd backend/book-app
./test-goodreads-import.sh
```

### Method 2: Manual Step-by-Step

#### Step 1: Register User
```bash
curl -X POST http://localhost:8080/api/user \
  -H "Content-Type: application/json" \
  -d '{"email":"user@user.user","password":"password"}'
```

#### Step 2: Login and Get Token
```bash
TOKEN=$(curl -s -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user@user.user","password":"password"}' \
  -i | grep -i "Authorization:" | cut -d' ' -f2- | tr -d '\r')

echo "Token: $TOKEN"
```

#### Step 3: Import CSV
```bash
curl -X POST http://localhost:8080/api/books/import/goodreads \
  -H "Authorization: $TOKEN" \
  -F "file=@src/test/resources/goodreads_library_export.csv"
```

#### Step 4: Verify Books Were Imported
```bash
curl -X GET http://localhost:8080/api/books \
  -H "Authorization: $TOKEN" | jq .
```

### Method 3: Using Postman

1. **Login Request**:
   - Method: POST
   - URL: `http://localhost:8080/login`
   - Headers: `Content-Type: application/json`
   - Body (raw JSON):
     ```json
     {
       "username": "user@user.user",
       "password": "password"
     }
     ```
   - Copy the `Authorization` header value from the response

2. **Import Request**:
   - Method: POST
   - URL: `http://localhost:8080/api/books/import/goodreads`
   - Headers: 
     - `Authorization: Bearer <token-from-step-1>`
   - Body: form-data
     - Key: `file` (type: File)
     - Value: Select your CSV file

3. **Verify Request**:
   - Method: GET
   - URL: `http://localhost:8080/api/books`
   - Headers: `Authorization: Bearer <token>`

## Expected Results

### Successful Import:
```
Response: "Imported X books"
```

### Verify in Database:
```bash
# Count books
curl -X GET http://localhost:8080/api/books \
  -H "Authorization: $TOKEN" | grep -o '"title"' | wc -l
```

## Troubleshooting

### "Server is not running"
- Make sure `mvn spring-boot:run` is running
- Check for errors in the server logs
- Verify port 8080 is not in use by another application

### "Token is empty"
- Make sure user is registered first
- Check that login endpoint returns 200 status
- Verify username/password are correct

### "File is empty" or "Failed to open file"
- Check file path is correct
- Use absolute path if relative path doesn't work
- Verify file exists: `ls -la src/test/resources/goodreads_library_export.csv`

### "Error parsing CSV file"
- Verify CSV format matches Goodreads export
- Check CSV has required columns: Title, Author, Exclusive Shelf
- Ensure CSV file is not corrupted

## Quick Test Command

Once server is running, you can test everything in one go:

```bash
# Register, login, and import
curl -s -X POST http://localhost:8080/api/user \
  -H "Content-Type: application/json" \
  -d '{"email":"user@user.user","password":"password"}' > /dev/null && \
TOKEN=$(curl -s -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user@user.user","password":"password"}' \
  -i | grep -i "Authorization:" | cut -d' ' -f2- | tr -d '\r') && \
curl -X POST http://localhost:8080/api/books/import/goodreads \
  -H "Authorization: $TOKEN" \
  -F "file=@src/test/resources/goodreads_library_export.csv"
```

