# PianoLearn Database Diagrams

## Cách sử dụng

### 1. PlantUML (`class-diagram.puml`)

**Xem online:**
- Truy cập: https://www.plantuml.com/plantuml/uml/
- Copy nội dung file `class-diagram.puml` và paste vào
- Hoặc sử dụng VS Code extension: "PlantUML"

**VS Code Extension:**
```bash
# Cài đặt extension
code --install-extension jebbs.plantuml
```

**Xem diagram:**
- Mở file `class-diagram.puml`
- Nhấn `Alt + D` để preview

---

### 2. Mermaid (`class-diagram.mmd`)

**Xem online:**
- Truy cập: https://mermaid.live/
- Copy nội dung file `class-diagram.mmd` và paste vào

**VS Code Extension:**
```bash
# Cài đặt extension
code --install-extension bierner.markdown-mermaid
```

**Xem trong Markdown:**
- Tạo file `.md` và nhúng code mermaid:
````markdown
```mermaid
[paste nội dung class-diagram.mmd vào đây]
```
````

**GitHub:**
- GitHub tự động render Mermaid diagram trong markdown files

---

## Tổng quan quan hệ

### Quan hệ One-to-Many (1:N)
1. **Courses → Lesson**: 1 khóa học có nhiều bài học
2. **Lesson → Exercise**: 1 bài học có nhiều bài tập
3. **Lesson → PianoQuestion**: 1 bài học có nhiều câu hỏi
4. **User → SheetMusic**: 1 user upload nhiều sheet music
5. **User → PracticeSession**: 1 user có nhiều phiên luyện tập
6. **User → DailyGoal**: 1 user có nhiều daily goals
7. **User → Favorite**: 1 user có nhiều favorites
8. **User → SongRating**: 1 user đánh giá nhiều bài hát
9. **User → SheetRating**: 1 user đánh giá nhiều sheet music
10. **Song → PracticeSession**: 1 bài hát được luyện nhiều lần
11. **Exercise → PracticeSession**: 1 bài tập được luyện nhiều lần
12. **Song → Favorite**: 1 bài hát được yêu thích bởi nhiều users
13. **SheetMusic → Favorite**: 1 sheet music được yêu thích bởi nhiều users
14. **Exercise → ExerciseResult**: 1 bài tập có nhiều kết quả
15. **Achievement → UserAchievement**: 1 thành tựu được unlock bởi nhiều users

### Quan hệ Many-to-Many (N:M) qua bảng trung gian
1. **User ↔ Lesson** (qua `UserProgress`):
   - 1 user học nhiều lessons
   - 1 lesson được học bởi nhiều users
   - Unique constraint: (userId, lessonId)

2. **User ↔ Achievement** (qua `UserAchievement`):
   - 1 user unlock nhiều achievements
   - 1 achievement được unlock bởi nhiều users
   - Unique constraint: (userId, achievementId)

### Quan hệ đặc biệt (Nullable Foreign Keys)
1. **Favorite**:
   - Có thể link đến Song HOẶC SheetMusic (không cả hai)
   - `songId` OR `sheetId` (nullable)

2. **PracticeSession**:
   - Có thể link đến Song HOẶC Exercise
   - `songId` OR `exerciseId` (nullable)

---

## Các bảng chính theo chức năng

### Authentication & User Management
- `User`

### Course Management
- `Courses`
- `Lesson`
- `Exercise`
- `PianoQuestion`

### Learning & Progress
- `UserProgress`
- `ExerciseResult`
- `PracticeSession`

### Content
- `Song`
- `SheetMusic`

### Gamification
- `Achievement`
- `UserAchievement`
- `DailyGoal`

### User Interaction
- `Favorite`
- `SongRating`
- `SheetRating`

---

## Lưu ý khi vẽ diagram

### Unique Constraints
- `UserProgress`: (userId, lessonId)
- `UserAchievement`: (userId, achievementId)
- `DailyGoal`: (userId, goalDate)

### Enum Values
- `User.role`: learner, admin
- `Song.difficultyLevel`: Beginner, Intermediate, Advanced
- `Exercise.difficulty`: Easy, Medium, Hard

### Rating Range
- `SongRating.rating`: 0.0 - 5.0
- `SheetRating.rating`: 0.0 - 5.0
