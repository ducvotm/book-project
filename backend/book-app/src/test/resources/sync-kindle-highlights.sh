#!/bin/bash

KINDLE_PATH="/Volumes/Kindle/documents/My Clippings.txt"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DEST_PATH="$SCRIPT_DIR/kindleHighlightsSample.txt"

if [ ! -f "$KINDLE_PATH" ]; then
    echo "❌ Kindle not plugged in"
    exit 1
fi

echo "📱 Syncing Kindle highlights..."

cp "$KINDLE_PATH" "$DEST_PATH"

if [ $? -eq 0 ]; then
    HIGHLIGHT_COUNT=$(grep -c "==========" "$DEST_PATH")
    echo "✅ Synced $HIGHLIGHT_COUNT highlights"
    echo "📁 Saved to: $DEST_PATH"
else
    echo "❌ Sync failed"
    exit 1
fi
