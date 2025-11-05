# MVP Plan: Book Management + Highlight Reminder (Month-End Launch)

## Vision

Minimal viable product combining book tracking with simple highlight reminders - ready to publish by month-end.

## MVP Scope (Absolute Minimum)

### 1. Book Management - Core Features

- ✅ Already: Add, view, update, delete books
- ✅ Already: Shelf organization (READ, READING, TO_READ, DID_NOT_FINISH)
- 🔨 **Must Fix**: User-scoped queries (security - currently exposes all books)

### 2. Highlight Reminder - Minimal Viable

- ✅ Already: Import highlights from Kindle
- ✅ Already: View/delete highlights
- ✅ Already: Manual email sending
- 🔨 **Must Add**: Simple scheduled reminders (daily digest, fixed schedule)

## Simplified Implementation Plan

### Week 1: Security Fixes (Critical)

**Goal**: Ensure users only see their own data

1. **Fix BookController user filtering**
   - Filter `findAll()` by current user
   - Filter `findById()` by current user ownership
   - Update `PredefinedShelfController` endpoints

2. **Fix BookService queries**
   - Add user parameter to `findAll()` methods
   - Update repository queries to join with user

3. **Fix KindleHighLight user filtering**
   - Filter highlights by `userEmail` (already stored)
   - Update `findAll()` in controller/service

### Week 2: Simple Reminder System

**Goal**: Automated daily highlight reminders

1. **Create simple Reminder entity**
   - Fields: `id`, `userEmail`, `highlightId`, `nextReminderDate`, `enabled`
   - No complex spaced repetition - just daily reminders
   - Migration file

2. **Reminder service (simple)**
   - Create reminder when highlight is imported
   - Query reminders due today
   - Update `nextReminderDate` to tomorrow after sending

3. **Enable Spring Scheduling**
   - Add `@EnableScheduling` to `BookProjectApplication`
   - Create `DailyReminderScheduler` with `@Scheduled` daily job
   - Send email digest with highlights due today

### Week 3: Polish & Testing

**Goal**: Ensure everything works end-to-end

1. **Integration testing**
   - Test user isolation works
   - Test reminder scheduling works
   - Test email sending works

2. **Fix any bugs**
   - Address any issues found

3. **Documentation**
   - API documentation
   - Deployment guide

## Files to Create (Minimal)

### New Files

- `HighlightReminder.java` (entity)
- `HighlightReminderRepository.java`
- `HighlightReminderService.java`
- `DailyReminderScheduler.java`
- `V031__CREATE_HIGHLIGHT_REMINDER.sql` (migration)

### Files to Modify

- `BookController.java` - add user filtering
- `BookService.java` - add user parameter
- `PredefinedShelfController.java` - add user filtering
- `KindleHighLightController.java` - filter by user, create reminder on import
- `KindleHighLightService.java` - filter by user
- `BookProjectApplication.java` - add `@EnableScheduling`

## What We're Skipping (For Speed)

❌ Highlight-to-Book linking (can add later)
❌ Spaced repetition algorithm (simple daily reminders only)
❌ User preference settings (one-size-fits-all for MVP)
❌ Complex reminder frequencies
❌ Frontend changes (API-only MVP)

## Success Criteria (MVP)

1. ✅ Users can only see their own books
2. ✅ Users can import Kindle highlights
3. ✅ Highlights trigger daily email reminders automatically
4. ✅ System sends reminder emails daily with user's highlights

## Timeline Estimate

- **Week 1**: Security fixes (3-4 days)
- **Week 2**: Reminder system (4-5 days)
- **Week 3**: Testing & polish (2-3 days)
- **Buffer**: 1 week for unexpected issues

**Total: ~3 weeks to MVP launch**
