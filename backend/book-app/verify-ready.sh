#!/bin/bash

echo "🔍 Verifying setup for real Kindle file testing..."
echo ""

cd "$(dirname "$0")"

echo "✅ Integration tests status:"
if mvn test -Dtest=HighlightReminderIntegrationTest,KindleHighLightUserIsolationIntegrationTest -q > /dev/null 2>&1; then
    echo "   Integration tests: PASSED"
else
    echo "   Integration tests: FAILED (run 'mvn test' to see details)"
    exit 1
fi

echo ""
echo "✅ Scripts available:"
if [ -f "./import-kindle-highlights.sh" ]; then
    echo "   ✓ import-kindle-highlights.sh"
else
    echo "   ✗ import-kindle-highlights.sh (missing)"
fi

if [ -f "./check-server.sh" ]; then
    echo "   ✓ check-server.sh"
else
    echo "   ✗ check-server.sh (missing)"
fi

echo ""
echo "✅ Ready to test with real Kindle files!"
echo ""
echo "Next steps:"
echo "  1. Start the server: mvn spring-boot:run"
echo "  2. Check server: ./check-server.sh"
echo "  3. Import your file: ./import-kindle-highlights.sh /path/to/MyClippings.txt"
echo ""

