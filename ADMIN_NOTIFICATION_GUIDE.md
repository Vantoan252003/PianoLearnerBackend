# Hướng dẫn sử dụng tính năng Quản Lý Thông Báo Admin

## 📱 Tổng quan

Hệ thống quản lý thông báo push cho phép admin gửi thông báo đến người dùng với các tính năng:

✅ **Gửi ngay lập tức** - Gửi thông báo đến users ngay lập tức  
✅ **Đặt lịch gửi** - Lên lịch gửi thông báo vào thời gian cụ thể  
✅ **Đa dạng đối tượng** - Gửi cho tất cả, từng người, theo level, theo role, v.v  
✅ **Theo dõi lịch sử** - Xem lịch sử đã gửi với thống kê thành công/thất bại  
✅ **Quản lý lịch** - Xem và hủy các thông báo đã đặt lịch  

## 🏗️ Các thành phần đã tạo

### 1. Database Tables
```sql
- notifications: Lưu lịch sử thông báo đã gửi
- scheduled_notifications: Lưu thông báo đã đặt lịch
```

### 2. Backend
```
- Notification.java: Entity lịch sử
- ScheduledNotification.java: Entity đặt lịch
- NotificationRepository.java
- ScheduledNotificationRepository.java
- AdminNotificationService.java: Logic xử lý
- AdminNotificationController.java: API endpoints
- ScheduledNotificationProcessor.java: Auto gửi theo lịch
```

### 3. Frontend
```
- notifications.html: Giao diện quản lý đẹp với tabs
```

## 📝 Các bước triển khai

### Bước 1: Chạy SQL Scripts ✅

```bash
# Chạy cả 2 file SQL
mysql -u root -p piano_learner_db < fcm_tokens_table.sql
mysql -u root -p piano_learner_db < notification_tables.sql
```

Hoặc chạy trực tiếp trong MySQL Workbench/CLI:
```sql
-- Chạy nội dung từ fcm_tokens_table.sql
-- Sau đó chạy nội dung từ notification_tables.sql
```

### Bước 2: Build và Restart Application

```bash
# Clean và build
mvn clean install

# Restart ứng dụng
mvn spring-boot:run
```

### Bước 3: Truy cập trang quản lý

Đăng nhập admin và truy cập:
```
http://localhost:8080/admin/notifications
```

## 🎯 Hướng dẫn sử dụng

### 1. Gửi Thông Báo Ngay Lập Tức

1. Click tab **"Gửi Ngay"**
2. Nhập thông tin:
   - **Tiêu đề**: Tiêu đề thông báo (bắt buộc)
   - **Nội dung**: Nội dung chi tiết (bắt buộc)
   - **URL Hình Ảnh**: Link ảnh hiển thị (tùy chọn)
   - **Loại Thông Báo**: SYSTEM, MARKETING, UPDATE, v.v
   - **Đối Tượng Nhận**: Chọn ai sẽ nhận
   - **Điều Kiện Lọc**: Nhập điều kiện tùy theo đối tượng

3. **Data Payload** (Tùy chọn): Thêm dữ liệu custom
   - Key 1, Value 1
   - Key 2, Value 2

4. Click **"Gửi Ngay"**

#### Ví dụ gửi cho tất cả users:
```
Tiêu đề: Chào mừng sự kiện mới!
Nội dung: Tham gia khóa học Piano miễn phí từ 1-7/12
Loại: MARKETING
Đối tượng: ALL
```

#### Ví dụ gửi cho user cụ thể:
```
Tiêu đề: Chúc mừng!
Nội dung: Bạn đã hoàn thành khóa học Piano cơ bản
Loại: ACHIEVEMENT  
Đối tượng: SPECIFIC_USER
Điều kiện: 123 (userId)
```

#### Ví dụ gửi cho users level advanced:
```
Tiêu đề: Khóa học nâng cao mới
Nội dung: Khóa học Jazz Piano dành cho level advanced
Loại: UPDATE
Đối tượng: BY_LEVEL
Điều kiện: advanced
```

### 2. Đặt Lịch Gửi Thông Báo

1. Click tab **"Đặt Lịch"**
2. Nhập thông tin tương tự "Gửi Ngay"
3. **Chọn thời gian gửi**: Chọn ngày giờ muốn gửi
4. Click **"Đặt Lịch"**

#### Ví dụ đặt lịch:
```
Tiêu đề: Nhắc nhở luyện tập hàng ngày
Nội dung: Đừng quên luyện tập Piano 30 phút mỗi ngày!
Thời gian: 07/12/2025 08:00 (sáng mai 8h)
Loại: REMINDER
Đối tượng: ACTIVE_USERS
```

### 3. Xem Lịch Sử Đã Gửi

1. Click tab **"Lịch Sử"**
2. Xem danh sách thông báo đã gửi với:
   - Tiêu đề, nội dung
   - Loại thông báo
   - Đối tượng nhận
   - Số lượng gửi thành công/thất bại
   - Thời gian gửi

### 4. Quản Lý Thông Báo Đã Lên Lịch

1. Click tab **"Thông Báo Đã Lên Lịch"**
2. Xem danh sách thông báo đang chờ gửi
3. Click **"Hủy"** để hủy thông báo nếu cần

## 🎨 Các loại đối tượng nhận (Target Audience)

| Đối tượng | Mô tả | Điều kiện lọc |
|-----------|-------|---------------|
| **ALL** | Tất cả users | Không cần |
| **SPECIFIC_USER** | User cụ thể | User ID (VD: 123) |
| **BY_LEVEL** | Theo level | Level name (beginner, intermediate, advanced) |
| **BY_ROLE** | Theo role | Role name (learner, instructor, admin) |
| **ACTIVE_USERS** | Users hoạt động gần đây | Không cần (auto: 30 ngày gần đây) |
| **INACTIVE_USERS** | Users không hoạt động | Không cần (auto: >30 ngày không hoạt động) |

## 🏷️ Các loại thông báo (Notification Type)

- **SYSTEM**: Thông báo hệ thống (bảo trì, cập nhật)
- **MARKETING**: Quảng cáo, khuyến mãi
- **UPDATE**: Cập nhật tính năng mới
- **ACHIEVEMENT**: Thông báo thành tích
- **REMINDER**: Nhắc nhở (luyện tập, điểm danh)
- **CUSTOM**: Tùy chỉnh

## 🔧 Auto Send Scheduled Notifications

Hệ thống tự động chạy mỗi phút để check và gửi thông báo đã đặt lịch:

```java
@Scheduled(fixedRate = 60000) // Mỗi 1 phút
public void processScheduledNotifications()
```

- Tự động gửi khi đến thời gian đã đặt
- Lưu kết quả vào lịch sử
- Cập nhật trạng thái thành SENT hoặc FAILED

## 📊 Thống kê hiển thị

Dashboard hiển thị:
- **Tổng Thông Báo**: Tổng số notifications đã tạo
- **Đã Gửi**: Tổng số người đã nhận được
- **Đang Chờ**: Số thông báo chưa gửi (đã lên lịch)
- **Đã Lên Lịch**: Tổng số scheduled notifications

## 🎨 Giao diện

Giao diện được thiết kế:
- ✅ Gradient đẹp mắt
- ✅ Icons Bootstrap
- ✅ Responsive design
- ✅ SweetAlert2 cho notifications
- ✅ Tabs dễ sử dụng
- ✅ Form validation
- ✅ Loading states

## 🔐 Bảo mật

- Chỉ admin mới truy cập được `/admin/notifications`
- Tất cả operations đều có authentication check
- Validation input đầy đủ

## 📱 Test Case Examples

### Test 1: Gửi thông báo cho tất cả
```
POST /admin/notifications/send
{
  "title": "Welcome!",
  "body": "Chào mừng đến Piano Learner",
  "notificationType": "SYSTEM",
  "targetAudience": "ALL"
}
```

### Test 2: Đặt lịch gửi thông báo
```
POST /admin/notifications/schedule
{
  "title": "Reminder",
  "body": "Don't forget to practice!",
  "notificationType": "REMINDER",
  "targetAudience": "ACTIVE_USERS",
  "scheduledTime": "2025-12-07T08:00:00"
}
```

### Test 3: Hủy scheduled notification
```
POST /admin/notifications/cancel/{scheduledId}
```

## 🚀 Tính năng nâng cao có thể mở rộng

1. **Rich Notifications**: Actions buttons, reply từ notification
2. **A/B Testing**: Test nhiều phiên bản thông báo
3. **Analytics**: Theo dõi open rate, click rate
4. **Templates**: Lưu templates thông báo thường dùng
5. **Recurring Notifications**: Gửi định kỳ (hàng ngày, tuần, tháng)
6. **User Preferences**: Cho phép user chọn loại thông báo muốn nhận
7. **Notification Groups**: Gom nhóm thông báo liên quan
8. **Priority Levels**: Urgent, high, normal, low

## ⚠️ Lưu ý quan trọng

1. **Rate Limiting**: Firebase có giới hạn số message/ngày
2. **Token Management**: Tokens có thể expire, hệ thống tự động disable
3. **Data Payload**: Giới hạn size, dùng cho navigation data
4. **Scheduled Time**: Chọn thời gian hợp lý, tránh spam users
5. **Testing**: Test kỹ trước khi gửi cho tất cả users

## 📞 Support

Nếu gặp vấn đề:
1. Check logs server
2. Check database tables
3. Verify FCM tokens của users
4. Check Firebase console

---

**Hoàn thành!** 🎉 Hệ thống quản lý thông báo admin đã sẵn sàng sử dụng với đầy đủ tính năng và giao diện đẹp!
