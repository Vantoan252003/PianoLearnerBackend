# =====================================================
# HƯỚNG DẪN CÀI ĐẶT TÍNH NĂNG QUÊN MẬT KHẨU
# =====================================================

## 📋 Tổng quan

Tính năng quên mật khẩu với luồng:
1. **Bước 1**: Nhập email → Nhận OTP qua email
2. **Bước 2**: Nhập OTP → Xác thực
3. **Bước 3**: Nhập mật khẩu mới → Đổi mật khẩu

---

## 🔧 Cài đặt

### 1. Chạy SQL Script

```bash
mysql -u root -p piano_learner < password_reset_table.sql
```

### 2. Cấu hình Email (application.properties)

Thêm vào file `src/main/resources/application.properties`:

```properties
# =====================================================
# EMAIL CONFIGURATION (Gmail SMTP)
# =====================================================

# Email gửi đi
spring.mail.host=37nguyenvantoan@gmail.com
spring.mail.port=587
spring.mail.username=37nguyenvantoan@gmail.com
spring.mail.password=eoir lpsk ywll pvnq

# SMTP Settings
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
spring.mail.properties.mail.smtp.connectiontimeout=5000
spring.mail.properties.mail.smtp.timeout=5000
spring.mail.properties.mail.smtp.writetimeout=5000

# Debug (optional)
spring.mail.properties.mail.debug=false
```

### 3. Tạo App Password cho Gmail

**Quan trọng**: Không dùng password Gmail thông thường!

#### Các bước tạo App Password:

1. **Đăng nhập Gmail** → Vào https://myaccount.google.com/security

2. **Bật 2-Step Verification** (nếu chưa có):
   - Security → 2-Step Verification
   - Làm theo hướng dẫn để bật

3. **Tạo App Password**:
   - Security → 2-Step Verification → App passwords
   - Chọn app: **Mail**
   - Chọn device: **Other** (nhập "Piano Learner Backend")
   - Click **Generate**
   - Copy password 16 ký tự (dạng: `xxxx xxxx xxxx xxxx`)

4. **Paste vào application.properties**:
   ```properties
   spring.mail.username=your-email@gmail.com
   spring.mail.password=xxxx xxxx xxxx xxxx
   ```

### 4. Thêm Dependency (nếu chưa có)

Kiểm tra `pom.xml` có dependency này chưa:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
```

Nếu chưa có, thêm vào và chạy:
```bash
mvn clean install
```

---

## 📝 API Endpoints

### 1. Gửi OTP (Forgot Password)

```http
POST /api/auth/forgot-password
Content-Type: application/json

{
  "email": "user@example.com"
}
```

**Response Success:**
```json
{
  "message": "Mã OTP đã được gửi đến email của bạn. Vui lòng kiểm tra hộp thư."
}
```

**Response Error:**
```json
{
  "error": "Email không tồn tại trong hệ thống"
}
```

### 2. Xác thực OTP

```http
POST /api/auth/verify-otp
Content-Type: application/json

{
  "email": "user@example.com",
  "otpCode": "123456"
}
```

**Response Success:**
```json
{
  "success": true,
  "message": "Mã OTP hợp lệ"
}
```

**Response Error:**
```json
{
  "success": false,
  "error": "Mã OTP không hợp lệ hoặc đã hết hạn"
}
```

### 3. Đặt lại mật khẩu

```http
POST /api/auth/reset-password
Content-Type: application/json

{
  "email": "user@example.com",
  "otpCode": "123456",
  "newPassword": "newpass123",
  "confirmPassword": "newpass123"
}
```

**Response Success:**
```json
{
  "message": "Đặt lại mật khẩu thành công! Bạn có thể đăng nhập với mật khẩu mới."
}
```

**Response Error:**
```json
{
  "error": "Mã OTP không hợp lệ hoặc đã hết hạn"
}
```

---

## 🔒 Bảo mật

### Rate Limiting
- **Tối đa 3 request/giờ** cho mỗi email
- Ngăn chặn spam và brute force

### OTP Security
- OTP **6 số** ngẫu nhiên
- **Hết hạn sau 5 phút**
- **Chỉ dùng 1 lần** (one-time)
- OTP cũ tự động vô hiệu khi tạo OTP mới

### Auto Cleanup
- Scheduler tự động xóa OTP hết hạn mỗi ngày lúc 2h sáng

---

## 🧪 Test với Postman

### Test Flow đầy đủ:

1. **Gửi OTP**:
```bash
POST http://localhost:8080/api/auth/forgot-password
{
  "email": "test@example.com"
}
```

2. **Kiểm tra email** → Copy OTP (6 số)

3. **Verify OTP**:
```bash
POST http://localhost:8080/api/auth/verify-otp
{
  "email": "test@example.com",
  "otpCode": "123456"
}
```

4. **Reset Password**:
```bash
POST http://localhost:8080/api/auth/reset-password
{
  "email": "test@example.com",
  "otpCode": "123456",
  "newPassword": "newpass123",
  "confirmPassword": "newpass123"
}
```

5. **Login với mật khẩu mới**:
```bash
POST http://localhost:8080/api/auth/login
{
  "email": "test@example.com",
  "password": "newpass123"
}
```

---

## 🎨 Email Template

Email gửi đi có giao diện đẹp với:
- Header gradient Piano Learner
- OTP box nổi bật
- Countdown timer (5 phút)
- Warning box bảo mật
- Responsive design

---

## ⚠️ Troubleshooting

### Lỗi: "Failed to send email"

**Nguyên nhân:**
- Sai username/password
- Chưa bật 2-Step Verification
- Chưa tạo App Password
- Gmail chặn "Less secure app"

**Giải pháp:**
1. Kiểm tra lại App Password
2. Bật 2-Step Verification
3. Tạo lại App Password mới
4. Kiểm tra firewall/proxy

### Lỗi: "Email không tồn tại"

**Nguyên nhân:** Email chưa đăng ký trong hệ thống

**Giải pháp:** Đăng ký tài khoản trước

### Lỗi: "OTP không hợp lệ"

**Nguyên nhân:**
- Nhập sai OTP
- OTP đã hết hạn (>5 phút)
- OTP đã được sử dụng

**Giải pháp:** Request OTP mới

### Lỗi: "Quá nhiều request"

**Nguyên nhân:** Vượt quá 3 request/giờ

**Giải pháp:** Chờ 1 giờ hoặc reset database:
```sql
DELETE FROM password_reset_tokens WHERE user_id = <user_id>;
```

---

## 📊 Database Schema

```sql
password_reset_tokens
├── token_id (PK)
├── user_id (FK → users)
├── otp_code (6 digits)
├── expires_at (DateTime)
├── is_used (Boolean)
└── created_at (DateTime)
```

---

## 🔄 Luồng hoạt động chi tiết

```
1. User nhập email
   ↓
2. Backend tìm user trong DB
   ↓
3. Check rate limit (max 3/hour)
   ↓
4. Generate OTP 6 số
   ↓
5. Lưu OTP vào DB (expires in 5 min)
   ↓
6. Gửi email với OTP template
   ↓
7. User nhập OTP
   ↓
8. Backend verify OTP
   ↓
9. User nhập password mới
   ↓
10. Backend hash password
    ↓
11. Update password trong DB
    ↓
12. Đánh dấu OTP đã dùng
    ↓
13. Done! ✅
```

---

## 📱 Flutter Implementation (Sample)

```dart
// 1. Forgot Password Screen
Future<void> sendOTP(String email) async {
  final response = await http.post(
    Uri.parse('$baseUrl/api/auth/forgot-password'),
    headers: {'Content-Type': 'application/json'},
    body: json.encode({'email': email}),
  );
  
  if (response.statusCode == 200) {
    // Navigate to OTP screen
    Navigator.push(context, VerifyOTPScreen(email: email));
  }
}

// 2. Verify OTP Screen
Future<bool> verifyOTP(String email, String otp) async {
  final response = await http.post(
    Uri.parse('$baseUrl/api/auth/verify-otp'),
    headers: {'Content-Type': 'application/json'},
    body: json.encode({'email': email, 'otpCode': otp}),
  );
  
  final data = json.decode(response.body);
  return data['success'] ?? false;
}

// 3. Reset Password Screen
Future<void> resetPassword(String email, String otp, String newPassword) async {
  final response = await http.post(
    Uri.parse('$baseUrl/api/auth/reset-password'),
    headers: {'Content-Type': 'application/json'},
    body: json.encode({
      'email': email,
      'otpCode': otp,
      'newPassword': newPassword,
      'confirmPassword': newPassword,
    }),
  );
  
  if (response.statusCode == 200) {
    // Show success, navigate to login
  }
}
```

---

## ✅ Checklist Implementation

- [x] Entity `PasswordResetToken`
- [x] Repository `PasswordResetTokenRepository`
- [x] Service `EmailService`
- [x] Service `PasswordResetService`
- [x] DTOs (ForgotPassword, VerifyOTP, ResetPassword)
- [x] API Endpoints
- [x] SQL Script
- [x] Email Template HTML
- [x] Rate Limiting (3/hour)
- [x] OTP Expiration (5 min)
- [x] Auto Cleanup Job
- [ ] Config Email trong application.properties
- [ ] Test trên Postman
- [ ] Implement Flutter UI

---

## 🎉 Hoàn thành!

Tính năng quên mật khẩu đã sẵn sàng. Chỉ cần:
1. Cấu hình email trong `application.properties`
2. Chạy migration SQL
3. Test API
4. Implement UI trên Flutter

Chúc may mắn! 🚀
