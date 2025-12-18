-- Create table for lesson guides
CREATE TABLE IF NOT EXISTS lesson_guides (
    guide_id INT PRIMARY KEY AUTO_INCREMENT,
    lesson_id INT NOT NULL UNIQUE,
    content LONGTEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (lesson_id) REFERENCES lessons(lesson_id) ON DELETE CASCADE
);

-- Add index for better performance
CREATE INDEX idx_lesson_guides_lesson_id ON lesson_guides(lesson_id);
