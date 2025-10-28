-- Piano Learn Database Setup Script

-- Create database if not exists
CREATE DATABASE IF NOT EXISTS learn_piano CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE learn_piano;

-- Drop existing tables (in correct order due to foreign keys)
DROP TABLE IF EXISTS favorites;
DROP TABLE IF EXISTS daily_goals;
DROP TABLE IF EXISTS practice_sessions;
DROP TABLE IF EXISTS user_achievements;
DROP TABLE IF EXISTS achievements;
DROP TABLE IF EXISTS exercise_results;
DROP TABLE IF EXISTS user_progress;
DROP TABLE IF EXISTS exercises;
DROP TABLE IF EXISTS lessons;
DROP TABLE IF EXISTS courses;
DROP TABLE IF EXISTS songs;
DROP TABLE IF EXISTS users;

-- Bảng người dùng
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('learner', 'admin') NOT NULL DEFAULT 'learner',
    avatar_url VARCHAR(255),
    level_name VARCHAR(50) DEFAULT 'Beginner',
    total_exp INT DEFAULT 0,
    streak_days INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_practice_date TIMESTAMP NULL,
    last_login TIMESTAMP NULL
);

-- Bảng khóa học
CREATE TABLE courses (
    course_id INT PRIMARY KEY AUTO_INCREMENT,
    course_name VARCHAR(150) NOT NULL,
    description TEXT,
    thumbnail_url VARCHAR(255),
    difficulty_level VARCHAR(50),
    duration_weeks INT,
    total_lessons INT DEFAULT 0,
    create_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Bảng bài học
CREATE TABLE lessons (
    lesson_id INT PRIMARY KEY AUTO_INCREMENT,
    course_id INT NOT NULL,
    lesson_title VARCHAR(150) NOT NULL,
    lesson_order INT NOT NULL,
    description TEXT,
    video_url VARCHAR(255),
    sheet_music_url VARCHAR(255),
    duration_minutes INT,
    exp_reward INT DEFAULT 10,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE
);

-- Bảng bài tập/luyện tập
CREATE TABLE exercises (
    exercise_id INT PRIMARY KEY AUTO_INCREMENT,
    lesson_id INT NOT NULL,
    exercise_title VARCHAR(150) NOT NULL,
    exercise_type VARCHAR(50),
    difficulty VARCHAR(50),
    midi_file_url VARCHAR(255),
    sheet_music_url VARCHAR(255),
    demo_audio_url VARCHAR(255),
    target_score INT DEFAULT 80,
    max_score INT DEFAULT 100,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (lesson_id) REFERENCES lessons(lesson_id) ON DELETE CASCADE
);

-- Bảng tiến độ học của user
CREATE TABLE user_progress (
    progress_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    lesson_id INT NOT NULL,
    is_completed BOOLEAN DEFAULT FALSE,
    completion_percentage INT DEFAULT 0,
    time_spent_minutes INT DEFAULT 0,
    last_accessed TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (lesson_id) REFERENCES lessons(lesson_id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_lesson (user_id, lesson_id)
);

-- Bảng kết quả bài tập
CREATE TABLE exercise_results (
    result_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    exercise_id INT NOT NULL,
    score INT NOT NULL,
    accuracy_percent DECIMAL(5,2),
    timing_accuracy DECIMAL(5,2),
    notes_correct INT,
    notes_total INT,
    is_passed BOOLEAN DEFAULT FALSE,
    played_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (exercise_id) REFERENCES exercises(exercise_id) ON DELETE CASCADE
);

-- Bảng thành tích/huy hiệu
CREATE TABLE achievements (
    achievement_id INT PRIMARY KEY AUTO_INCREMENT,
    achievement_name VARCHAR(100) NOT NULL,
    description TEXT,
    icon_url VARCHAR(255),
    requirement_type VARCHAR(50),
    requirement_value INT,
    exp_reward INT DEFAULT 50
);

-- Bảng thành tích của user
CREATE TABLE user_achievements (
    user_achievement_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    achievement_id INT NOT NULL,
    unlocked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (achievement_id) REFERENCES achievements(achievement_id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_achievement (user_id, achievement_id)
);

-- Bảng bài hát
CREATE TABLE songs (
    song_id INT PRIMARY KEY AUTO_INCREMENT,
    song_title VARCHAR(150) NOT NULL,
    artist VARCHAR(100),
    difficulty_level VARCHAR(50),
    duration_seconds INT,
    thumbnail_url VARCHAR(255),
    midi_file_url VARCHAR(255),
    sheet_music_url VARCHAR(255),
    audio_url VARCHAR(255),
    popularity_score INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Bảng lịch sử luyện tập
CREATE TABLE practice_sessions (
    session_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    song_id INT,
    exercise_id INT,
    practice_duration_minutes INT NOT NULL,
    session_date DATE NOT NULL,
    exp_earned INT DEFAULT 0,
    notes_played INT DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (song_id) REFERENCES songs(song_id) ON DELETE SET NULL,
    FOREIGN KEY (exercise_id) REFERENCES exercises(exercise_id) ON DELETE SET NULL
);

-- Bảng mục tiêu hàng ngày
CREATE TABLE daily_goals (
    goal_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    goal_date DATE NOT NULL,
    target_minutes INT DEFAULT 30,
    completed_minutes INT DEFAULT 0,
    is_completed BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_date (user_id, goal_date)
);

-- Bảng yêu thích
CREATE TABLE favorites (
    favorite_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    song_id INT NOT NULL,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (song_id) REFERENCES songs(song_id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_song (user_id, song_id)
);

-- Insert sample admin user (password: admin123)
-- Note: You should use /api/auth/register to create users with properly hashed passwords
INSERT INTO users (full_name, email, password_hash, role, level_name) VALUES 
('Admin User', 'admin@pianolearn.com', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'admin', 'Master');

-- Insert sample courses
INSERT INTO courses (course_name, description, difficulty_level, duration_weeks, total_lessons) VALUES
('Piano Basics', 'Khóa học cơ bản về piano dành cho người mới bắt đầu', 'Beginner', 8, 20),
('Intermediate Piano', 'Khóa học nâng cao kỹ năng chơi piano', 'Intermediate', 12, 30),
('Advanced Techniques', 'Kỹ thuật chơi piano nâng cao và chuyên nghiệp', 'Advanced', 16, 40);

-- Insert sample achievements
INSERT INTO achievements (achievement_name, description, requirement_type, requirement_value, exp_reward) VALUES
('First Steps', 'Hoàn thành bài học đầu tiên', 'lessons_completed', 1, 50),
('Week Warrior', 'Luyện tập 7 ngày liên tục', 'streak_days', 7, 100),
('Practice Makes Perfect', 'Luyện tập tổng cộng 10 giờ', 'practice_minutes', 600, 200),
('Master Learner', 'Hoàn thành 50 bài học', 'lessons_completed', 50, 500);

-- Insert sample songs
INSERT INTO songs (song_title, artist, difficulty_level, duration_seconds, popularity_score) VALUES
('Twinkle Twinkle Little Star', 'Traditional', 'Easy', 120, 100),
('Happy Birthday', 'Traditional', 'Easy', 90, 95),
('Für Elise', 'Ludwig van Beethoven', 'Medium', 180, 90),
('Moonlight Sonata', 'Ludwig van Beethoven', 'Hard', 360, 85);

COMMIT;

-- Show created tables
SHOW TABLES;
