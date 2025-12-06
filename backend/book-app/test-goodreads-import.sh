#!/bin/bash

set -e

BASE_URL="${BASE_URL:-http://localhost:8080}"
USERNAME="${USERNAME:-user@user.user}"
PASSWORD="${PASSWORD:-password}"
CSV_FILE="${1:-src/test/resources/goodreads_library_export.csv}"

echo "📚 Testing Goodreads Import"
echo "=========================="
echo "Server: $BASE_URL"
echo "User: $USERNAME"
echo "CSV File: $CSV_FILE"
echo ""

# Check if server is running
echo "📡 Checking if server is running..."
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/login" 2>/dev/null || echo "000")
if [ "$HTTP_CODE" = "000" ]; then
    echo "❌ Server is not running at $BASE_URL"
    echo ""
    echo "Please start the server first:"
    echo "  cd backend/book-app"
    echo "  mvn spring-boot:run"
    exit 1
fi
echo "✅ Server is running"
echo ""

# Register user (ignore if already exists)
echo "📝 Registering user..."
REGISTER_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/user" \
    -H "Content-Type: application/json" \
    -d "{\"email\":\"$USERNAME\",\"password\":\"$PASSWORD\"}")

REGISTER_HTTP_CODE=$(echo "$REGISTER_RESPONSE" | tail -n1)
REGISTER_BODY=$(echo "$REGISTER_RESPONSE" | sed '$d')

if [ "$REGISTER_HTTP_CODE" -eq 200 ] || [ "$REGISTER_HTTP_CODE" -eq 201 ]; then
    echo "✅ User registered successfully"
elif [ "$REGISTER_HTTP_CODE" -eq 400 ]; then
    echo "ℹ️  User may already exist (this is okay)"
else
    echo "⚠️  Registration returned HTTP $REGISTER_HTTP_CODE (continuing anyway)"
fi
echo ""

# Login
echo "🔐 Logging in..."
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/login" \
    -H "Content-Type: application/json" \
    -d "{\"username\":\"$USERNAME\",\"password\":\"$PASSWORD\"}" \
    -i)

echo "Debug: Full login response:"
echo "$LOGIN_RESPONSE" | head -20
echo ""

TOKEN=$(echo "$LOGIN_RESPONSE" | grep -i "Authorization:" | cut -d' ' -f2- | tr -d '\r')

if [ -z "$TOKEN" ]; then
    echo "❌ Login failed - no token received"
    echo ""
    echo "Trying alternative extraction methods..."
    
    # Try alternative extraction
    TOKEN=$(echo "$LOGIN_RESPONSE" | grep -i "authorization:" | awk '{print $2}')
    
    if [ -z "$TOKEN" ]; then
        echo "❌ Still no token found"
        echo ""
        echo "Please check:"
        echo "  1. Server is running and fully started"
        echo "  2. User credentials are correct"
        echo "  3. Login endpoint is working"
        exit 1
    fi
fi

echo "✅ Login successful"
echo "Token: ${TOKEN:0:50}..."
echo ""

# Check if CSV file exists
if [ ! -f "$CSV_FILE" ]; then
    echo "❌ Error: CSV file not found: $CSV_FILE"
    echo ""
    echo "Please provide the path to your Goodreads CSV file:"
    echo "  ./test-goodreads-import.sh /path/to/your/file.csv"
    exit 1
fi

echo "✅ CSV file found: $CSV_FILE"
echo ""

# Import CSV
echo "📥 Importing Goodreads CSV..."
IMPORT_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/books/import/goodreads" \
    -H "Authorization: $TOKEN" \
    -F "file=@$CSV_FILE")

IMPORT_HTTP_CODE=$(echo "$IMPORT_RESPONSE" | tail -n1)
IMPORT_BODY=$(echo "$IMPORT_RESPONSE" | sed '$d')

echo "Response (HTTP $IMPORT_HTTP_CODE):"
echo "$IMPORT_BODY"
echo ""

if [ "$IMPORT_HTTP_CODE" -eq 200 ]; then
    echo "✅ Import successful!"
else
    echo "❌ Import failed with HTTP $IMPORT_HTTP_CODE"
    exit 1
fi

# Get all books to verify
echo "📚 Verifying imported books..."
BOOKS_RESPONSE=$(curl -s -X GET "$BASE_URL/api/books" \
    -H "Authorization: $TOKEN")

BOOK_COUNT=$(echo "$BOOKS_RESPONSE" | grep -o '"title"' | wc -l | tr -d ' ')

echo "✅ Found $BOOK_COUNT books in database"
echo ""
echo "Sample of imported books:"
echo "$BOOKS_RESPONSE" | head -c 500
echo "..."
echo ""
echo "🎉 Test completed successfully!"

