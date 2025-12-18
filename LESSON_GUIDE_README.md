# Hướng Dẫn Sử Dụng Tính Năng "Hướng Dẫn Bài Học"

## Tổng Quan

Tính năng này cho phép admin soạn hướng dẫn cho các bài học bằng một Rich Text Editor (giống Word), sau đó tự động xuất ra file PDF và upload lên Cloudinary, rồi cập nhật link vào trường `videoUrl` của bài học.

## Các File Đã Tạo

### 1. Database
- `lesson_guides_table.sql` - Script tạo bảng `lesson_guides`

### 2. Entity
- `LessonGuide.java` - Entity cho bảng lesson_guides

### 3. Repository
- `LessonGuideRepository.java` - Repository để truy vấn lesson guides

### 4. Service
- `LessonGuideService.java` - Service xử lý logic:
  - Lưu nội dung HTML
  - Chuyển đổi HTML sang PDF
  - Upload PDF lên Cloudinary
  - Cập nhật videoUrl của lesson

### 5. Controller
- `AdminLessonGuideController.java` - Controller xử lý:
  - Hiển thị trang editor
  - API lấy hướng dẫn hiện có
  - API lưu và upload PDF

### 6. View
- `lesson_guide.html` - Trang admin với Quill.js Rich Text Editor

### 7. Dependencies (đã thêm vào pom.xml)
- `itext7-core` - Thư viện tạo PDF
- `html2pdf` - Chuyển đổi HTML sang PDF

## Cách Sử Dụng

### Bước 1: Chạy SQL Script
```sql
-- Chạy file lesson_guides_table.sql để tạo bảng mới
mysql -u username -p database_name < lesson_guides_table.sql
```

### Bước 2: Build Project
```bash
mvn clean install
```

### Bước 3: Khởi động ứng dụng
```bash
mvn spring-boot:run
```

### Bước 4: Sử dụng tính năng

1. Đăng nhập vào trang admin
2. Vào menu **Quản Lý Khóa Học** → **Hướng dẫn Bài học**
3. Chọn bài học từ dropdown
4. Soạn nội dung hướng dẫn bằng editor (có đầy đủ tính năng formatting)
5. Click **"Lưu & Upload PDF"**
6. Hệ thống sẽ:
   - Lưu nội dung HTML vào database
   - Chuyển đổi sang PDF
   - Upload lên Cloudinary
   - Cập nhật `videoUrl` của lesson với link PDF

### Các Tính Năng Editor

- **Text formatting**: Bold, Italic, Underline, Strike
- **Headers**: H1-H6
- **Lists**: Ordered, Unordered
- **Alignment**: Left, Center, Right, Justify
- **Colors**: Text color, Background color
- **Links**: Thêm liên kết
- **Images**: Chèn hình ảnh
- **Code blocks**: Hiển thị code
- **Blockquotes**: Trích dẫn

### API Endpoints

#### 1. Hiển thị trang editor
```
GET /admin/lesson-guide
```

#### 2. Lấy hướng dẫn hiện có
```
GET /api/admin/lesson-guide/{lessonId}
Response: { "content": "...", "updatedAt": "..." }
```

#### 3. Lưu và upload PDF
```
POST /api/admin/lesson-guide/save
Body: { "lessonId": 123, "content": "<html>...</html>" }
Response: { "message": "Success", "pdfUrl": "https://..." }
```

## Lưu Ý

1. **Cloudinary**: Đảm bảo config Cloudinary trong `CloudinaryService.java` đúng
2. **Database**: Chạy SQL script trước khi sử dụng
3. **Memory**: Với nội dung lớn, có thể cần tăng heap size JVM
4. **PDF Style**: Style PDF được định nghĩa trong `LessonGuideService.convertHtmlToPdf()`
5. **videoUrl**: Field này sẽ chứa link PDF của hướng dẫn

## Troubleshooting

### Lỗi "Lesson not found"
- Kiểm tra lessonId có tồn tại trong database không

### Lỗi upload Cloudinary
- Kiểm tra API key và secret
- Kiểm tra kết nối internet
- Kiểm tra dung lượng file PDF

### Lỗi convert HTML to PDF
- Kiểm tra HTML có hợp lệ không
- Kiểm tra các dependency iText đã được thêm vào pom.xml
- Rebuild project sau khi thêm dependency

## Cải Tiến Trong Tương Lai

- Thêm preview PDF trước khi upload
- Lưu lịch sử các phiên bản hướng dẫn
- Thêm template có sẵn
- Hỗ trợ nhiều ngôn ngữ
- Export sang các format khác (Word, Markdown)
