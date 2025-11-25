# Kindle Highlights Import Guide

This guide helps you test the application with real Kindle highlight files.

## Prerequisites

1. **Server Running**: Make sure your Spring Boot application is running
   ```bash
   cd backend/book-app
   mvn spring-boot:run
   ```

2. **Default Configuration**:
   - Server URL: `http://localhost:8080` (or `http://localhost:5000` for dev profile)
   - Test User: `user@user.user` / `password`

## Quick Start

### Step 1: Check if Server is Running

```bash
./check-server.sh
```

Or with custom port:
```bash
BASE_URL=http://localhost:5000 ./check-server.sh
```

### Step 2: Import Your Kindle Highlights

**Option A: From Kindle Device**
```bash
./import-kindle-highlights.sh "/Volumes/Kindle/documents/My Clippings.txt"
```

**Option B: From Any File**
```bash
./import-kindle-highlights.sh ~/Downloads/MyClippings.txt
```

**Option C: With Custom Credentials**
```bash
USERNAME=your@email.com PASSWORD=yourpassword ./import-kindle-highlights.sh /path/to/file.txt
```

**Option D: With Custom Server URL**
```bash
BASE_URL=http://localhost:5000 ./import-kindle-highlights.sh /path/to/file.txt
```

## Manual Testing

### 1. Login and Get Token

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user@user.user","password":"password"}' \
  -i | grep -i authorization | cut -d' ' -f2 | tr -d '\r')

echo "Token: $TOKEN"
```

### 2. Import Highlights

```bash
curl -X POST http://localhost:8080/api/kindle-highlights/import \
  -H "Authorization: $TOKEN" \
  -H "Content-Type: text/plain" \
  --data-binary @/path/to/MyClippings.txt
```

### 3. View All Highlights

```bash
curl -X GET http://localhost:8080/api/kindle-highlights \
  -H "Authorization: $TOKEN" | jq .
```

### 4. View Specific Highlight

```bash
curl -X GET http://localhost:8080/api/kindle-highlights/1 \
  -H "Authorization: $TOKEN" | jq .
```

### 5. Send Highlights via Email

```bash
curl -X POST http://localhost:8080/api/kindle-highlights/email \
  -H "Authorization: $TOKEN"
```

### 6. Delete a Highlight

```bash
curl -X DELETE http://localhost:8080/api/kindle-highlights/1 \
  -H "Authorization: $TOKEN"
```

## Expected Kindle File Format

The parser expects the standard Kindle export format:

```
Book Title (Author Name)
- Your Highlight on page X | Location Y-Y | Added on Date

Highlight content here
==========
Next Book Title (Author Name)
- Your Highlight on page X | Location Y-Y | Added on Date

Next highlight content
==========
```

## Troubleshooting

### Server Not Running
- Check if the server is running: `./check-server.sh`
- Start the server: `mvn spring-boot:run`

### Authentication Failed
- Verify credentials match your test user
- Check that JWT is configured correctly
- Ensure the user exists in the database

### Import Fails
- Verify the file format matches Kindle export format
- Check file encoding (should be UTF-8)
- Look at server logs for parsing errors

### No Highlights Returned
- Verify highlights were imported successfully
- Check that you're using the correct user's token
- Ensure user isolation is working (you only see your highlights)

## Testing the Reminder System

### Check Reminders Were Created

After importing, reminders should be automatically created. You can verify by:
1. Checking the database directly
2. Waiting for the scheduled reminder time (9 AM daily)
3. Manually triggering the scheduler in a test

### Manual Scheduler Test

The scheduler runs at 9 AM daily. To test immediately, you can:
1. Update reminder dates in the database to today
2. Or create a test that manually calls `scheduler.sendDailyReminders()`

## Next Steps

After importing:
1. ✅ Verify highlights are stored correctly
2. ✅ Test user isolation (only your highlights visible)
3. ✅ Test email sending functionality
4. ✅ Verify reminders are created
5. ✅ Test the daily reminder scheduler

