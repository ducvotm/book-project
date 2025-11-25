#!/bin/bash

BASE_URL="${BASE_URL:-http://localhost:8080}"

echo "🔍 Checking if server is running at $BASE_URL..."
echo ""

HEALTH_CHECK=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/login" || echo "000")

if [ "$HEALTH_CHECK" = "000" ]; then
    echo "❌ Server is not responding at $BASE_URL"
    echo ""
    echo "Make sure the server is running:"
    echo "  cd backend/book-app"
    echo "  mvn spring-boot:run"
    exit 1
elif [ "$HEALTH_CHECK" = "405" ] || [ "$HEALTH_CHECK" = "400" ]; then
    echo "✅ Server is running (got HTTP $HEALTH_CHECK - endpoint exists)"
elif [ "$HEALTH_CHECK" = "200" ]; then
    echo "✅ Server is running and responding"
else
    echo "⚠️  Server responded with HTTP $HEALTH_CHECK"
fi

echo ""
echo "Server URL: $BASE_URL"
echo ""
echo "To test with a different port, set BASE_URL:"
echo "  BASE_URL=http://localhost:5000 ./check-server.sh"

