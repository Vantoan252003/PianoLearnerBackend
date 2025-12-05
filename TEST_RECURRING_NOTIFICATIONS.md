## Test Recurring Notifications - Quick Guide

### 1. Chạy Migration SQL

```bash
mysql -u root -p piano_learner < migration_recurring_notifications.sql
```

### 2. Khởi động lại ứng dụng

```bash
mvn spring-boot:run
```

### 3. Test UI

Truy cập: http://localhost:8080/admin/notifications

#### Test Gửi Hàng Ngày (DAILY)

1. Click tab **"Đặt Lịch"**
2. Điền form:
   - **Tiêu đề**: "Daily Practice Reminder"
   - **Nội dung**: "Đừng quên luyện piano hôm nay! 🎹"
   - **Loại Thông Báo**: REMINDER
   - **Đối Tượng**: ALL
   - **Thời Gian Gửi**: Chọn ngày hôm nay
   - **Loại Lặp Lại**: **Hàng Ngày**
   - **Giờ Gửi**: 09:00 (hoặc giờ hiện tại + 5 phút để test nhanh)
3. Click **"Đặt Lịch"**

#### Test Gửi Hàng Tuần (WEEKLY)

1. Điền form tương tự
2. **Loại Lặp Lại**: **Hàng Tuần**
3. **Chọn Các Ngày**: Tick T2, T4, T6
4. **Giờ Gửi**: 14:00
5. Click **"Đặt Lịch"**

#### Test Gửi Hàng Tháng (MONTHLY)

1. Điền form tương tự
2. **Loại Lặp Lại**: **Hàng Tháng**
3. **Thời Gian Gửi**: Chọn ngày 1 (đầu tháng)
4. **Giờ Gửi**: 10:00
5. Click **"Đặt Lịch"**

### 4. Kiểm tra Scheduler

Scheduler chạy mỗi 5 phút. Để test nhanh:

**Test ngay lập tức:**
- Set giờ gửi = giờ hiện tại + 5 phút
- Chờ 5 phút
- Check logs hoặc database

**Xem logs:**
```bash
# Trong console application
# Tìm dòng:
# "Starting recurring notification check..."
# "Found X recurring notifications"
# "Sent recurring notification (DAILY): 123"
```

**Check database:**
```sql
-- Xem scheduled notifications
SELECT * FROM scheduled_notifications 
WHERE is_recurring = TRUE 
ORDER BY created_at DESC;

-- Xem last_sent_at đã được update chưa
SELECT scheduled_notification_id, title, recurrence_type, 
       last_sent_at, created_at 
FROM scheduled_notifications 
WHERE is_recurring = TRUE;

-- Xem notifications đã gửi
SELECT * FROM notifications 
ORDER BY created_at DESC 
LIMIT 10;
```

### 5. Verify Logic

**DAILY:**
- Chỉ gửi 1 lần/ngày
- Gửi vào giờ đã chọn (±5 phút)
- `last_sent_at` được cập nhật sau mỗi lần gửi

**WEEKLY:**
- Chỉ gửi vào các ngày đã chọn
- Chỉ gửi 1 lần/ngày
- VD: Chọn T2,T4,T6 → Chỉ gửi vào các ngày đó

**MONTHLY:**
- Gửi vào cùng ngày mỗi tháng
- VD: Chọn ngày 1 → Gửi mỗi đầu tháng

### 6. Test Scenarios

#### Scenario 1: Test DAILY notification
```sql
-- Insert test
INSERT INTO scheduled_notifications 
(title, body, notification_type, target_audience, 
 is_recurring, recurrence_type, recurrence_time, 
 scheduled_time, status, created_by)
VALUES 
('Test Daily', 'This is a daily test', 'REMINDER', 'ALL', 
 TRUE, 'DAILY', '14:30', NOW(), 'PENDING', 1);

-- Chờ đến 14:30 (hoặc 5 phút sau scheduler chạy)
-- Check notifications table
SELECT COUNT(*) FROM notifications WHERE title = 'Test Daily';
```

#### Scenario 2: Test WEEKLY notification
```sql
-- Monday = 1, Wednesday = 3, Friday = 5
INSERT INTO scheduled_notifications 
(title, body, notification_type, target_audience, 
 is_recurring, recurrence_type, recurrence_days, recurrence_time, 
 scheduled_time, status, created_by)
VALUES 
('Test Weekly', 'MWF notification', 'REMINDER', 'ALL', 
 TRUE, 'WEEKLY', '[1,3,5]', '15:00', NOW(), 'PENDING', 1);
```

### 7. Debug Tips

**Nếu không gửi:**

1. Check scheduler có chạy không:
   - Xem logs có dòng "Starting recurring notification check..." mỗi 5 phút
   
2. Check time zone:
   - Server time phải đúng múi giờ +7
   
3. Check status:
   ```sql
   SELECT status FROM scheduled_notifications WHERE is_recurring = TRUE;
   -- Phải là 'PENDING', không phải 'CANCELLED' hoặc 'SENT'
   ```

4. Check recurrence_time format:
   - Phải đúng format HH:mm (VD: "09:00", không phải "9:00")

5. Check last_sent_at:
   ```sql
   -- Nếu hôm nay đã gửi rồi thì không gửi lại nữa
   SELECT last_sent_at FROM scheduled_notifications 
   WHERE is_recurring = TRUE;
   ```

### 8. Manual Test với Postman

```http
POST http://localhost:8080/admin/notifications/schedule
Content-Type: multipart/form-data

{
  "title": "Daily Test",
  "body": "Test message",
  "notificationType": "REMINDER",
  "targetAudience": "ALL",
  "scheduledTime": "2025-12-06T09:00:00",
  "isRecurring": true,
  "recurrenceType": "DAILY",
  "recurrenceTime": "09:00"
}
```

### Expected Results

✅ **DAILY**: Gửi mỗi ngày lúc 9h sáng
✅ **WEEKLY**: Gửi T2,T4,T6 lúc 2h chiều
✅ **MONTHLY**: Gửi đầu tháng lúc 10h sáng
✅ **ONCE**: Gửi 1 lần, status → SENT
✅ **Recurring**: Status vẫn PENDING, last_sent_at cập nhật

Done! 🎉
