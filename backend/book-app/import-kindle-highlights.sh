#!/bin/bash

set -e

BASE_URL="${BASE_URL:-http://localhost:8080}"
USERNAME="${USERNAME:-user@user.user}"
PASSWORD="${PASSWORD:-password}"

if [ $# -eq 0 ]; then
    echo "Usage: $0 <path-to-kindle-file> [options]"
    echo ""
    echo "Options:"
    echo "  BASE_URL=<url>     Set the base URL (default: http://localhost:8080)"
    echo "  USERNAME=<email>   Set the username (default: user@user.user)"
    echo "  PASSWORD=<pass>     Set the password (default: password)"
    echo ""
    echo "Examples:"
    echo "  $0 /Volumes/Kindle/documents/My\\ Clippings.txt"
    echo "  $0 ~/Downloads/MyClippings.txt"
    echo "  BASE_URL=http://localhost:5000 $0 /path/to/file.txt"
    exit 1
fi

KINDLE_FILE="$1"

if [ ! -f "$KINDLE_FILE" ]; then
    echo "❌ Error: File not found: $KINDLE_FILE"
    exit 1
fi

echo "📝 Registering user $USERNAME..."
REGISTER_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/user" \
    -H "Content-Type: application/json" \
    -d "{\"username\":\"$USERNAME\",\"password\":\"$PASSWORD\"}")

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

echo "🔐 Logging in as $USERNAME..."
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/login" \
    -H "Content-Type: application/json" \
    -d "{\"username\":\"$USERNAME\",\"password\":\"$PASSWORD\"}" \
    -i)

JWT_TOKEN=$(echo "$LOGIN_RESPONSE" | grep -i "authorization:" | cut -d' ' -f2- | tr -d '\r')

if [ -z "$JWT_TOKEN" ]; then
    echo "❌ Error: Failed to login. Check your credentials and that the server is running."
    echo ""
    echo "Response:"
    echo "$LOGIN_RESPONSE"
    exit 1
fi

echo "✅ Login successful"
echo ""

echo "📚 Importing highlights from: $KINDLE_FILE"
HIGHLIGHT_COUNT=$(grep -c "==========" "$KINDLE_FILE" || echo "0")
echo "   Found approximately $HIGHLIGHT_COUNT highlight blocks"
echo ""

IMPORT_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/kindle-highlights/import" \
    -H "Authorization: $JWT_TOKEN" \
    -H "Content-Type: text/plain" \
    --data-binary "@$KINDLE_FILE")

HTTP_CODE=$(echo "$IMPORT_RESPONSE" | tail -n1)
RESPONSE_BODY=$(echo "$IMPORT_RESPONSE" | sed '$d')

if [ "$HTTP_CODE" -eq 200 ]; then
    echo "✅ $RESPONSE_BODY"
    echo ""
    
    echo "📋 Fetching all highlights..."
    HIGHLIGHTS_RESPONSE=$(curl -s -X GET "$BASE_URL/api/kindle-highlights" \
        -H "Authorization: $JWT_TOKEN")
    
    HIGHLIGHT_COUNT_SAVED=$(echo "$HIGHLIGHTS_RESPONSE" | grep -o '"id"' | wc -l | tr -d ' ')
    
    echo "✅ Total highlights in database: $HIGHLIGHT_COUNT_SAVED"
    echo ""
    
    echo "📊 Sample highlights (first 3):"
    echo "$HIGHLIGHTS_RESPONSE" | python3 -m json.tool 2>/dev/null | head -n 50 || echo "$HIGHLIGHTS_RESPONSE" | head -n 20
    echo ""
    
    echo "🎉 Import complete!"
    echo ""
    echo "Next steps:"
    echo "  - View all highlights: curl -H \"Authorization: $JWT_TOKEN\" $BASE_URL/api/kindle-highlights"
    echo "  - Send highlights via email: curl -X POST -H \"Authorization: $JWT_TOKEN\" $BASE_URL/api/kindle-highlights/email"
else
    echo "❌ Error: Import failed (HTTP $HTTP_CODE)"
    echo ""
    echo "Response:"
    echo "$RESPONSE_BODY"
    exit 1
fi

