# HƯỚNG DẪN SỬ DỤNG RECURRING NOTIFICATIONS

## 📋 Tổng quan

Hệ thống đã được cập nhật với 2 tính năng mới:

1. **Giới hạn số lượng notifications cho mỗi user** - Tự động giữ tối đa 10 notifications/user
2. **Gửi notifications định kỳ** - Hỗ trợ gửi hàng ngày, hàng tuần, hàng tháng

---

## 🔧 Cài đặt

### 1. Chạy Trigger MySQL (Giới hạn notifications)

```bash
mysql -u your_username -p your_database < notification_limit_trigger.sql
```

**Trigger này sẽ:**
- Tự động giữ tối đa 10 notifications cho mỗi user
- Xóa notifications cũ nhất khi có notification mới (thứ 11)
- Chỉ áp dụng cho notifications có `user_id` (không ảnh hưởng notifications gửi cho ALL)

### 2. Chạy Migration (Thêm cột recurring)

```bash
mysql -u your_username -p your_database < migration_recurring_notifications.sql
```

**Migration này thêm các cột:**
- `is_recurring` - Boolean cho biết có phải recurring notification không
- `recurrence_type` - Loại: ONCE, DAILY, WEEKLY, MONTHLY
- `recurrence_days` - JSON array các ngày trong tuần (cho WEEKLY)
- `recurrence_time` - Thời gian gửi mỗi ngày (format HH:mm)
- `last_sent_at` - Lần gửi cuối cùng

---

## 📝 Cách sử dụng

### A. Gửi Notification Một Lần (ONCE)

**Giống như trước, không cần thay đổi gì:**

```json
{
  "title": "Welcome!",
  "body": "Welcome to Piano Learner",
  "notificationType": "SYSTEM",
  "targetAudience": "ALL",
  "scheduledTime": "2025-12-06T10:00:00"
}
```

### B. Gửi Hàng Ngày (DAILY)

**Gửi notification mỗi ngày lúc 9:00 sáng:**

```json
{
  "title": "Daily Practice Reminder",
  "body": "Time to practice piano! Don't forget your daily goal 🎹",
  "notificationType": "REMINDER",
  "targetAudience": "ALL",
  "isRecurring": true,
  "recurrenceType": "DAILY",
  "recurrenceTime": "09:00",
  "scheduledTime": "2025-12-06T09:00:00"
}
```

**Lưu ý:**
- `scheduledTime` - Ngày bắt đầu gửi
- `recurrenceTime` - Giờ gửi mỗi ngày (format: HH:mm)

### C. Gửi Hàng Tuần (WEEKLY)

**Gửi notification mỗi Thứ 2, 3, 5 lúc 2:00 chiều:**

```json
{
  "title": "Practice Session",
  "body": "It's time for your scheduled piano lesson!",
  "notificationType": "REMINDER",
  "targetAudience": "ACTIVE_USERS",
  "isRecurring": true,
  "recurrenceType": "WEEKLY",
  "recurrenceDays": "[1,2,4]",
  "recurrenceTime": "14:00",
  "scheduledTime": "2025-12-06T14:00:00"
}
```

**Days of week mapping:**
- 1 = Monday (Thứ 2)
- 2 = Tuesday (Thứ 3)
- 3 = Wednesday (Thứ 4)
- 4 = Thursday (Thứ 5)
- 5 = Friday (Thứ 6)
- 6 = Saturday (Thứ 7)
- 7 = Sunday (Chủ nhật)

**Ví dụ:**
- `"[1,2,3,4,5]"` - Thứ 2 đến Thứ 6 (weekdays)
- `"[6,7]"` - Cuối tuần
- `"[1,3,5]"` - Thứ 2, 4, 6

### D. Gửi Hàng Tháng (MONTHLY)

**Gửi notification mỗi đầu tháng (ngày 1) lúc 10:00 sáng:**

```json
{
  "title": "Monthly Progress",
  "body": "Check out your monthly progress report!",
  "notificationType": "SYSTEM",
  "targetAudience": "ALL",
  "isRecurring": true,
  "recurrenceType": "MONTHLY",
  "recurrenceTime": "10:00",
  "scheduledTime": "2025-12-01T10:00:00"
}
```

**Lưu ý:**
- Ngày trong `scheduledTime` sẽ được sử dụng làm ngày gửi mỗi tháng
- Ví dụ: `2025-12-15T10:00:00` → Sẽ gửi ngày 15 mỗi tháng

---

## 🔄 Scheduler

**Scheduler tự động chạy mỗi 5 phút** để kiểm tra và gửi notifications.

### Cách hoạt động:

1. **Mỗi 5 phút**, scheduler sẽ:
   - Kiểm tra notifications ONCE đã đến hạn → Gửi và đánh dấu SENT
   - Kiểm tra recurring notifications cần gửi → Gửi và cập nhật `last_sent_at`

2. **Logic kiểm tra:**
   - **DAILY**: Gửi nếu chưa gửi hôm nay và đã đến giờ
   - **WEEKLY**: Gửi nếu hôm nay nằm trong `recurrenceDays` và chưa gửi
   - **MONTHLY**: Gửi nếu hôm nay là ngày đã set và chưa gửi tháng này

3. **Khoảng thời gian cho phép**: ±5 phút từ `recurrenceTime`
   - Ví dụ: Set 09:00 → Sẽ gửi nếu thời gian hiện tại từ 08:55 đến 09:05

---

## 📊 Database Structure

### Table: `scheduled_notifications`

```sql
CREATE TABLE scheduled_notifications (
    scheduled_notification_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    body TEXT NOT NULL,
    image_url VARCHAR(500),
    data_payload JSON,
    notification_type VARCHAR(50),
    target_audience VARCHAR(50),
    target_criteria VARCHAR(500),
    scheduled_time DATETIME NOT NULL,
    
    -- Recurring fields
    is_recurring BOOLEAN DEFAULT FALSE,
    recurrence_type VARCHAR(20),  -- ONCE, DAILY, WEEKLY, MONTHLY
    recurrence_days VARCHAR(100),  -- JSON: [1,2,3,4,5]
    recurrence_time VARCHAR(10),   -- HH:mm
    last_sent_at DATETIME,
    
    -- Status
    status VARCHAR(20) DEFAULT 'PENDING',
    created_by INT,
    sent_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

---

## 🎯 Ví dụ Thực Tế

### 1. Nhắc nhở luyện tập hàng ngày

```sql
INSERT INTO scheduled_notifications 
(title, body, notification_type, target_audience, 
 is_recurring, recurrence_type, recurrence_time, 
 scheduled_time, status, created_by)
VALUES 
('Daily Practice 🎹', 
 'Good morning! Time to practice piano for 30 minutes.',
 'REMINDER', 'ALL', 
 TRUE, 'DAILY', '08:00', 
 NOW(), 'PENDING', 1);
```

### 2. Bài tập mới mỗi Thứ 2 & Thứ 4

```sql
INSERT INTO scheduled_notifications 
(title, body, notification_type, target_audience, 
 is_recurring, recurrence_type, recurrence_days, recurrence_time, 
 scheduled_time, status, created_by)
VALUES 
('New Exercises Available! 🎼', 
 'Check out new piano exercises for this week.',
 'UPDATE', 'ACTIVE_USERS', 
 TRUE, 'WEEKLY', '[1,4]', '15:00', 
 NOW(), 'PENDING', 1);
```

### 3. Báo cáo tiến độ đầu tháng

```sql
INSERT INTO scheduled_notifications 
(title, body, notification_type, target_audience, 
 is_recurring, recurrence_type, recurrence_time, 
 scheduled_time, status, created_by)
VALUES 
('Monthly Progress Report 📊', 
 'Your monthly piano learning report is ready!',
 'SYSTEM', 'ALL', 
 TRUE, 'MONTHLY', '09:00', 
 '2025-12-01 09:00:00', 'PENDING', 1);
```

---

## 🛑 Quản lý Recurring Notifications

### Tạm dừng (Pause)

```sql
UPDATE scheduled_notifications 
SET status = 'CANCELLED' 
WHERE scheduled_notification_id = 123;
```

### Kích hoạt lại (Resume)

```sql
UPDATE scheduled_notifications 
SET status = 'PENDING' 
WHERE scheduled_notification_id = 123;
```

### Xóa vĩnh viễn

```sql
DELETE FROM scheduled_notifications 
WHERE scheduled_notification_id = 123;
```

### Chỉnh sửa thời gian

```sql
UPDATE scheduled_notifications 
SET recurrence_time = '10:00',
    updated_at = NOW()
WHERE scheduled_notification_id = 123;
```

---

## 📱 API Endpoints (Nếu có)

### Tạo Recurring Notification

```http
POST /admin/notifications/schedule
Content-Type: application/json

{
  "title": "Daily Reminder",
  "body": "Practice time!",
  "notificationType": "REMINDER",
  "targetAudience": "ALL",
  "isRecurring": true,
  "recurrenceType": "DAILY",
  "recurrenceTime": "09:00",
  "scheduledTime": "2025-12-06T09:00:00"
}
```

### Hủy Recurring Notification

```http
PUT /admin/notifications/scheduled/{id}/cancel
```

### Xem danh sách

```http
GET /admin/notifications/dashboard
```

---

## ⚠️ Lưu ý quan trọng

1. **Scheduler chạy mỗi 5 phút** → Notification có thể gửi muộn tối đa 5 phút
2. **Múi giờ** → Đảm bảo server time đúng múi giờ Việt Nam (UTC+7)
3. **Database backup** → Backup trước khi chạy migration
4. **Testing** → Test kỹ với một vài users trước khi gửi cho ALL
5. **Monitoring** → Theo dõi logs để đảm bảo scheduler hoạt động tốt

---

## 🐛 Troubleshooting

### Notification không gửi

1. Kiểm tra `status = 'PENDING'`
2. Kiểm tra `recurrence_time` đúng format HH:mm
3. Kiểm tra server time và múi giờ
4. Xem logs của scheduler

### Gửi trùng lặp

1. Kiểm tra `last_sent_at` có được cập nhật không
2. Kiểm tra có nhiều server instances đang chạy không

### Scheduler không chạy

1. Kiểm tra `@EnableScheduling` trong Application class
2. Kiểm tra không có exception trong logs
3. Restart application

---

## 📞 Support

Nếu có vấn đề, kiểm tra:
- Application logs
- MySQL error logs
- Scheduler execution logs

Chúc bạn sử dụng tính năng recurring notifications hiệu quả! 🎉
