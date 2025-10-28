# Piano Learn - Admin Management System

## Tổng quan

Hệ thống quản trị toàn diện cho Piano Learn với đầy đủ chức năng CRUD (Create, Read, Update, Delete) cho tất cả các bảng trong database.

## Cấu trúc Database

Hệ thống quản lý các bảng sau:

1. **users** - Quản lý người dùng (learner và admin)
2. **courses** - Quản lý khóa học
3. **lessons** - Quản lý bài học
4. **exercises** - Quản lý bài tập
5. **songs** - Quản lý bài hát
6. **achievements** - Quản lý thành tích
7. **user_progress** - Tiến độ học của người dùng
8. **exercise_results** - Kết quả bài tập
9. **user_achievements** - Thành tích của người dùng
10. **practice_sessions** - Lịch sử luyện tập
11. **daily_goals** - Mục tiêu hàng ngày
12. **favorites** - Danh sách yêu thích

## Cách sử dụng

### 1. Đăng nhập Admin

1. Truy cập: `http://localhost:8080/admin/login`
2. Đăng nhập với tài khoản có role `admin`
3. Hệ thống sẽ kiểm tra quyền và chuyển đến Dashboard

### 2. Tạo tài khoản Admin đầu tiên

Sử dụng API để tạo admin:

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Admin User",
    "email": "admin@pianolearn.com",
    "password": "admin123",
    "role": "admin"
  }'
```

### 3. Sử dụng Admin Dashboard

#### Giao diện chính
- **Sidebar**: Menu điều hướng giữa các bảng
- **Main Content**: Hiển thị dữ liệu dạng bảng
- **Action Buttons**: 
  - ✏️ **Sửa**: Mở modal để chỉnh sửa
  - 🗑️ **Xóa**: Xóa bản ghi (có xác nhận)
  - ➕ **Thêm mới**: Thêm bản ghi mới

#### Các chức năng

**Quản lý Người dùng (Users)**
- Xem danh sách người dùng
- Thêm/Sửa/Xóa người dùng
- Quản lý role (learner/admin)
- Theo dõi level, EXP, streak days

**Quản lý Khóa học (Courses)**
- Tạo và quản lý khóa học
- Thiết lập độ khó, thời lượng
- Upload thumbnail
- Quản lý số bài học

**Quản lý Bài học (Lessons)**
- Tạo bài học cho từng khóa học
- Thiết lập thứ tự bài học
- Upload video, sheet music
- Thiết lập EXP thưởng

**Quản lý Bài tập (Exercises)**
- Tạo bài tập cho từng bài học
- Upload MIDI files, sheet music, demo audio
- Thiết lập điểm đạt và điểm tối đa
- Phân loại độ khó

**Quản lý Bài hát (Songs)**
- Thêm bài hát vào thư viện
- Upload MIDI, sheet music, audio
- Thiết lập độ khó và thời lượng
- Theo dõi điểm phổ biến

**Quản lý Thành tích (Achievements)**
- Tạo huy hiệu/thành tích
- Thiết lập yêu cầu mở khóa
- Cấu hình EXP thưởng
- Upload icon

## API Endpoints

### Authentication
```
POST /api/auth/register - Đăng ký
POST /api/auth/login - Đăng nhập
```

### Admin APIs (Requires Admin Role)

#### Users
```
GET    /api/admin/users - Lấy danh sách
GET    /api/admin/users/{id} - Lấy chi tiết
POST   /api/admin/users - Tạo mới
PUT    /api/admin/users/{id} - Cập nhật
DELETE /api/admin/users/{id} - Xóa
```

#### Courses
```
GET    /api/admin/courses - Lấy danh sách
GET    /api/admin/courses/{id} - Lấy chi tiết
POST   /api/admin/courses - Tạo mới
PUT    /api/admin/courses/{id} - Cập nhật
DELETE /api/admin/courses/{id} - Xóa
```

#### Lessons
```
GET    /api/admin/lessons - Lấy danh sách
GET    /api/admin/lessons/{id} - Lấy chi tiết
POST   /api/admin/lessons - Tạo mới
PUT    /api/admin/lessons/{id} - Cập nhật
DELETE /api/admin/lessons/{id} - Xóa
```

#### Exercises
```
GET    /api/admin/exercises - Lấy danh sách
GET    /api/admin/exercises/{id} - Lấy chi tiết
POST   /api/admin/exercises - Tạo mới
PUT    /api/admin/exercises/{id} - Cập nhật
DELETE /api/admin/exercises/{id} - Xóa
```

#### Songs
```
GET    /api/admin/songs - Lấy danh sách
GET    /api/admin/songs/{id} - Lấy chi tiết
POST   /api/admin/songs - Tạo mới
PUT    /api/admin/songs/{id} - Cập nhật
DELETE /api/admin/songs/{id} - Xóa
```

#### Achievements
```
GET    /api/admin/achievements - Lấy danh sách
GET    /api/admin/achievements/{id} - Lấy chi tiết
POST   /api/admin/achievements - Tạo mới
PUT    /api/admin/achievements/{id} - Cập nhật
DELETE /api/admin/achievements/{id} - Xóa
```

## Bảo mật

1. **JWT Authentication**: Tất cả API yêu cầu JWT token
2. **Role-Based Access**: Chỉ tài khoản có role `admin` mới truy cập được
3. **Token Storage**: Token được lưu trong localStorage
4. **Auto Redirect**: Tự động chuyển về login nếu không có quyền

## Công nghệ sử dụng

### Backend
- Spring Boot 3.5.6
- Spring Security
- Spring Data JPA
- MySQL
- JWT (JSON Web Token)
- Lombok

### Frontend
- Thymeleaf
- Bootstrap 5.3.0
- Bootstrap Icons
- Vanilla JavaScript
- Fetch API

## Cấu trúc thư mục

```
src/main/
├── java/com/piano/learn/PianoLearn/
│   ├── controller/
│   │   ├── admin/ - Admin controllers
│   │   └── auth/ - Authentication controllers
│   ├── entity/ - JPA entities
│   │   ├── achievement/
│   │   ├── auth/
│   │   ├── courses/
│   │   ├── exercise/
│   │   ├── favorite/
│   │   ├── goal/
│   │   ├── lesson/
│   │   ├── practice/
│   │   ├── progress/
│   │   └── song/
│   ├── repository/ - JPA repositories
│   ├── service/ - Business logic
│   └── security/ - Security configuration
└── resources/
    ├── static/
    │   └── admin-script.js - Admin dashboard JS
    └── templates/
        └── admin/
            ├── dashboard.html - Admin dashboard
            └── login.html - Admin login page
```

## Tính năng nổi bật

✅ **Single Page Dashboard** - Tất cả CRUD trong 1 trang duy nhất
✅ **Modal Forms** - Form thêm/sửa dạng modal, tiết kiệm không gian
✅ **Responsive Design** - Tương thích mọi thiết bị
✅ **Real-time Updates** - Cập nhật dữ liệu ngay lập tức
✅ **Relationship Management** - Quản lý quan hệ giữa các bảng
✅ **Validation** - Kiểm tra dữ liệu đầu vào
✅ **Error Handling** - Xử lý lỗi thân thiện
✅ **Secure** - Bảo mật với JWT và role-based access

## Hướng dẫn mở rộng

### Thêm bảng mới vào Admin Dashboard

1. **Tạo Entity** trong package tương ứng
2. **Tạo Repository** extends JpaRepository
3. **Tạo Service** với CRUD methods
4. **Tạo Controller** với REST endpoints
5. **Cập nhật admin-script.js**:
   ```javascript
   entityConfig.newTable = {
       apiUrl: '/api/admin/newtable',
       fields: [...],
       tableColumns: [...]
   }
   ```
6. **Thêm section** trong dashboard.html
7. **Thêm menu item** trong sidebar

## Lưu ý

- Đảm bảo database đã được tạo và cấu hình đúng trong `application.properties`
- Port mặc định: 8080
- JWT secret key cần được cấu hình trong environment variables cho production
- Cascade delete đã được thiết lập cho các quan hệ cha-con

## Troubleshooting

**Lỗi 401/403**: Kiểm tra token và role của user
**Lỗi kết nối database**: Kiểm tra MySQL service và credentials
**Modal không hiển thị**: Kiểm tra Bootstrap CDN
**Dữ liệu không load**: Kiểm tra console log và network tab

## Contact

Để được hỗ trợ, vui lòng liên hệ team phát triển Piano Learn.
