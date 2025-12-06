#!/bin/bash

set -e

BASE_URL="${BASE_URL:-http://localhost:8080}"
USERNAME="${USERNAME:-user@user.user}"
PASSWORD="${PASSWORD:-password}"
CSV_FILE="${1:-src/test/resources/goodreads_library_export.csv}"

echo "🔍 Debugging Goodreads Import"
echo "============================="
echo ""

# Register user
echo "📝 Registering user..."
curl -s -X POST "$BASE_URL/api/user" \
    -H "Content-Type: application/json" \
    -d "{\"email\":\"$USERNAME\",\"password\":\"$PASSWORD\"}" > /dev/null
echo "✅ Done"
echo ""

# Login
echo "🔐 Logging in..."
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/login" \
    -H "Content-Type: application/json" \
    -d "{\"username\":\"$USERNAME\",\"password\":\"$PASSWORD\"}" \
    -i)

TOKEN=$(echo "$LOGIN_RESPONSE" | grep -i "Authorization:" | cut -d' ' -f2- | tr -d '\r' | tr -d '\n')

if [ -z "$TOKEN" ]; then
    TOKEN=$(echo "$LOGIN_RESPONSE" | grep -i "authorization:" | awk '{print $2}' | tr -d '\r' | tr -d '\n')
fi

if [ -z "$TOKEN" ]; then
    echo "❌ Failed to get token"
    exit 1
fi

echo "✅ Login successful"
echo ""

# Import CSV with verbose output
echo "📥 Importing CSV..."
echo "File: $CSV_FILE"
echo ""

RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/books/import/goodreads" \
    -H "Authorization: $TOKEN" \
    -F "file=@$CSV_FILE")

HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
BODY=$(echo "$RESPONSE" | sed '$d')

echo "HTTP Status: $HTTP_CODE"
echo "Response Body:"
echo "$BODY"
echo ""

if [ "$HTTP_CODE" -eq 200 ]; then
    echo "✅ Import successful!"
    
    # Verify books
    echo ""
    echo "📚 Verifying imported books..."
    BOOKS=$(curl -s -X GET "$BASE_URL/api/books" -H "Authorization: $TOKEN")
    BOOK_COUNT=$(echo "$BOOKS" | grep -o '"title"' | wc -l | tr -d ' ')
    echo "Found $BOOK_COUNT books in database"
else
    echo "❌ Import failed"
    echo ""
    echo "Check the server logs for detailed error information."
    echo "The error message above should give you more details."
fi

