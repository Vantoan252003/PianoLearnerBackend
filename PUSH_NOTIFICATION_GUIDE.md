# Hướng dẫn tích hợp Push Notification với Firebase Cloud Messaging (FCM)

## 📋 Tổng quan

Hệ thống đã được tích hợp Firebase Cloud Messaging (FCM) để gửi thông báo push đến thiết bị mobile của người dùng khi có các sự kiện sau:
- ✅ Có người thêm bài hát/sheet music của bạn vào yêu thích
- ✅ Có người đánh giá (rate) bài hát/sheet music của bạn

## 🏗️ Kiến trúc đã triển khai

### 1. Entity & Database
- **FCMToken**: Entity lưu thông tin FCM token của từng thiết bị
- **Bảng `fcm_tokens`**: Lưu token, device info, trạng thái active

### 2. Services
- **FCMService**: Xử lý việc lưu/xóa token và gửi thông báo FCM
- **NotificationService**: Logic nghiệp vụ cho các loại thông báo

### 3. Controller
- **FCMController**: API endpoints để Flutter app gửi token lên server

### 4. Tích hợp
- ✅ **FavoriteService**: Gửi thông báo khi có người favorite
- ✅ **SheetRatingService**: Gửi thông báo khi có người rate sheet music
- ⚠️ **SongRatingService**: Đã chuẩn bị sẵn (cần thêm trường uploadedBy vào Song entity)

## 📝 Các bước tiếp theo

### Bước 1: Cập nhật database ✅
```bash
# Chạy file SQL để tạo bảng fcm_tokens
mysql -u root -p piano_learner_db < fcm_tokens_table.sql
```

Hoặc chạy trực tiếp SQL:
```sql
CREATE TABLE IF NOT EXISTS fcm_tokens (
    fcm_token_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    token VARCHAR(500) NOT NULL UNIQUE,
    device_id VARCHAR(255),
    device_type VARCHAR(50),
    device_name VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_used_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);
```

### Bước 2: Cài đặt dependencies (Maven) ✅
Đã thêm vào `pom.xml`:
```xml
<dependency>
    <groupId>com.google.firebase</groupId>
    <artifactId>firebase-admin</artifactId>
    <version>9.2.0</version>
</dependency>
```

Chạy để cài đặt:
```bash
mvn clean install
```

### Bước 3: Cấu hình Firebase ✅
File service account key đã được đặt tại:
```
src/main/resources/config/learn-eea37-firebase-adminsdk-fbsvc-234d62e606.json
```

⚠️ **LƯU Ý BẢO MẬT**: 
- Thêm file này vào `.gitignore` để không commit lên Git
- Trên production, sử dụng environment variables hoặc secret management

```bash
# Thêm vào .gitignore
echo "src/main/resources/config/*.json" >> .gitignore
```

### Bước 4: Enable @Async trong Spring Boot
Tạo file cấu hình để bật async processing (tùy chọn, nhưng khuyến nghị):

```java
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {
    
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("fcm-");
        executor.initialize();
        return executor;
    }
}
```

### Bước 5: Test API endpoints ✅

#### 5.1. Lưu FCM Token (từ Flutter app)
```http
POST /api/auth/fcm/token
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "token": "fcm_token_string_from_firebase",
  "deviceId": "unique_device_id",
  "deviceType": "android",
  "deviceName": "Samsung Galaxy S21"
}
```

#### 5.2. Xóa FCM Token (khi logout)
```http
DELETE /api/auth/fcm/token?token=fcm_token_string
Authorization: Bearer {jwt_token}
```

#### 5.3. Lấy danh sách tokens của user
```http
GET /api/auth/fcm/tokens
Authorization: Bearer {jwt_token}
```

### Bước 6: Tích hợp Flutter App

#### 6.1. Thêm Firebase vào Flutter
```yaml
# pubspec.yaml
dependencies:
  firebase_core: latest_version
  firebase_messaging: latest_version
```

#### 6.2. Khởi tạo Firebase và lấy token
```dart
import 'package:firebase_core/firebase_core.dart';
import 'package:firebase_messaging/firebase_messaging.dart';

// Trong main()
await Firebase.initializeApp();

// Lấy FCM token
String? token = await FirebaseMessaging.instance.getToken();

// Gửi token lên server
await sendTokenToServer(token);

// Lắng nghe token refresh
FirebaseMessaging.instance.onTokenRefresh.listen((newToken) {
  sendTokenToServer(newToken);
});
```

#### 6.3. Gửi token lên server
```dart
Future<void> sendTokenToServer(String? token) async {
  if (token == null) return;
  
  final deviceId = await getDeviceId(); // Implement này
  
  final response = await http.post(
    Uri.parse('$baseUrl/api/auth/fcm/token'),
    headers: {
      'Authorization': 'Bearer $jwtToken',
      'Content-Type': 'application/json',
    },
    body: jsonEncode({
      'token': token,
      'deviceId': deviceId,
      'deviceType': Platform.isAndroid ? 'android' : 'ios',
      'deviceName': await getDeviceName(), // Implement này
    }),
  );
}
```

#### 6.4. Xử lý notification trong Flutter
```dart
// Foreground message
FirebaseMessaging.onMessage.listen((RemoteMessage message) {
  print('Got a message whilst in the foreground!');
  print('Message data: ${message.data}');
  
  if (message.notification != null) {
    showLocalNotification(message.notification!);
  }
});

// Background message
FirebaseMessaging.onBackgroundMessage(_firebaseMessagingBackgroundHandler);

// Notification tapped
FirebaseMessaging.onMessageOpenedApp.listen((RemoteMessage message) {
  print('A new onMessageOpenedApp event was published!');
  // Navigate to specific screen based on message.data['type']
  handleNotificationNavigation(message.data);
});
```

### Bước 7: Test thông báo

#### Test 1: Thêm vào yêu thích
1. User A upload một sheet music
2. User B đăng nhập và favorite sheet music đó
3. User A sẽ nhận được thông báo

```http
POST /api/user/favorites/sheet/{sheetId}
Authorization: Bearer {user_b_token}
```

#### Test 2: Đánh giá sheet music
1. User A upload một sheet music
2. User B đánh giá sheet music đó
3. User A sẽ nhận được thông báo

```http
POST /api/auth/sheets/{sheetId}/rating
Authorization: Bearer {user_b_token}
Content-Type: application/json

{
  "rating": 4.5,
  "comment": "Great sheet music!"
}
```

## 📊 Cấu trúc Notification Data

Mỗi notification sẽ có data payload để Flutter app xử lý:

### Sheet Favorited
```json
{
  "type": "sheet_favorited",
  "sheetId": "123",
  "userId": "456"
}
```

### Sheet Rated
```json
{
  "type": "sheet_rated",
  "sheetId": "123",
  "userId": "456",
  "rating": "4.5"
}
```

### Song Favorited
```json
{
  "type": "song_favorited",
  "songId": "789",
  "userId": "456"
}
```

### Song Rated
```json
{
  "type": "song_rated",
  "songId": "789",
  "userId": "456",
  "rating": "5.0"
}
```

## 🔧 Troubleshooting

### Lỗi: Firebase initialization failed
- Kiểm tra file service account key có đúng vị trí không
- Kiểm tra quyền đọc file
- Xem log để biết chi tiết lỗi

### Lỗi: Token không được lưu
- Kiểm tra bảng `fcm_tokens` đã được tạo chưa
- Kiểm tra user_id có tồn tại không
- Xem constraint foreign key

### Lỗi: Không nhận được notification
- Kiểm tra token có active không (`is_active = true`)
- Kiểm tra token còn valid không (Firebase có thể revoke token)
- Kiểm tra log server để xem có lỗi gửi notification không
- Kiểm tra Flutter app đã request permission chưa

## 🚀 Các tính năng có thể mở rộng

1. **Notification History**: Lưu lịch sử thông báo đã gửi
2. **Notification Preferences**: Cho phép user bật/tắt từng loại thông báo
3. **Scheduled Notifications**: Gửi thông báo theo lịch
4. **Rich Notifications**: Thêm actions, images, sounds
5. **Topic-based Notifications**: Gửi thông báo theo chủ đề
6. **Analytics**: Theo dõi tỷ lệ mở notification

## 📚 Tài liệu tham khảo

- [Firebase Cloud Messaging Documentation](https://firebase.google.com/docs/cloud-messaging)
- [Firebase Admin SDK for Java](https://firebase.google.com/docs/admin/setup)
- [Flutter Firebase Messaging](https://firebase.flutter.dev/docs/messaging/overview/)

## ⚠️ Lưu ý quan trọng

1. **Bảo mật**: Không commit Firebase service account key lên Git
2. **Performance**: Gửi notification không đồng bộ (@Async) để không block request
3. **Error Handling**: Xử lý trường hợp token expired/invalid
4. **Rate Limiting**: Firebase có giới hạn số lượng message/ngày cho free tier
5. **Privacy**: Tuân thủ quy định về thông báo và privacy của user

## ✅ Checklist triển khai

- [ ] Chạy SQL script tạo bảng fcm_tokens
- [ ] Cài đặt Firebase Admin SDK dependency
- [ ] Copy service account key vào resources/config
- [ ] Thêm service account key vào .gitignore
- [ ] Test API lưu/xóa FCM token
- [ ] Tích hợp Firebase vào Flutter app
- [ ] Test nhận notification trên mobile
- [ ] Deploy lên production
- [ ] Monitor logs và errors
