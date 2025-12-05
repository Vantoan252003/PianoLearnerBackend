-- Script tạo bảng notifications và scheduled_notifications
-- Để quản lý thông báo push từ admin

-- Bảng lưu lịch sử notifications đã gửi
CREATE TABLE IF NOT EXISTS notifications (
    notification_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NULL,
    title VARCHAR(200) NOT NULL,
    body TEXT NOT NULL,
    image_url VARCHAR(500),
    data_payload JSON,
    notification_type VARCHAR(50),
    target_audience VARCHAR(50),
    target_criteria VARCHAR(500),
    status VARCHAR(20) DEFAULT 'SENT',
    sent_by INT,
    sent_count INT DEFAULT 0,
    failed_count INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    FOREIGN KEY (sent_by) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_sent_by (sent_by),
    INDEX idx_created_at (created_at),
    INDEX idx_target_audience (target_audience)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng lưu notifications đã đặt lịch
CREATE TABLE IF NOT EXISTS scheduled_notifications (
    scheduled_notification_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    body TEXT NOT NULL,
    image_url VARCHAR(500),
    data_payload JSON,
    notification_type VARCHAR(50),
    target_audience VARCHAR(50),
    target_criteria VARCHAR(500),
    scheduled_time DATETIME NOT NULL,
    -- Recurring notification fields
    is_recurring BOOLEAN DEFAULT FALSE,
    recurrence_type VARCHAR(20) COMMENT 'ONCE, DAILY, WEEKLY, MONTHLY',
    recurrence_days VARCHAR(100) COMMENT 'JSON array: [1,2,3,4,5] for days of week (1=Monday, 7=Sunday)',
    recurrence_time VARCHAR(10) COMMENT 'HH:mm format (e.g., 09:00)',
    last_sent_at DATETIME COMMENT 'Lần gửi cuối cùng (cho recurring)',
    -- Other fields
    status VARCHAR(20) DEFAULT 'PENDING',
    created_by INT,
    sent_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_scheduled_time (scheduled_time),
    INDEX idx_status (status),
    INDEX idx_created_by (created_by),
    INDEX idx_is_recurring (is_recurring),
    INDEX idx_recurrence_type (recurrence_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Comments
ALTER TABLE notifications COMMENT = 'Lịch sử thông báo push đã gửi từ admin';
ALTER TABLE scheduled_notifications COMMENT = 'Thông báo push đã đặt lịch từ admin';
