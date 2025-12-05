-- =====================================================
-- MIGRATION: Thêm tính năng recurring notifications
-- Date: 2025-12-05
-- =====================================================

-- Thêm các cột mới cho scheduled_notifications
ALTER TABLE scheduled_notifications
ADD COLUMN is_recurring BOOLEAN DEFAULT FALSE COMMENT 'Có phải notification định kỳ không',
ADD COLUMN recurrence_type VARCHAR(20) COMMENT 'ONCE, DAILY, WEEKLY, MONTHLY',
ADD COLUMN recurrence_days VARCHAR(100) COMMENT 'JSON array: [1,2,3,4,5] for days of week (1=Monday, 7=Sunday)',
ADD COLUMN recurrence_time VARCHAR(10) COMMENT 'HH:mm format (e.g., 09:00)',
ADD COLUMN last_sent_at DATETIME COMMENT 'Lần gửi cuối cùng (cho recurring)';

-- Thêm index cho performance
CREATE INDEX idx_is_recurring ON scheduled_notifications(is_recurring);
CREATE INDEX idx_recurrence_type ON scheduled_notifications(recurrence_type);
CREATE INDEX idx_last_sent_at ON scheduled_notifications(last_sent_at);

-- =====================================================
-- Hướng dẫn sử dụng:
-- 
-- 1. ONCE (gửi 1 lần):
--    - is_recurring = FALSE
--    - scheduled_time = thời gian gửi
--
-- 2. DAILY (gửi hàng ngày):
--    - is_recurring = TRUE
--    - recurrence_type = 'DAILY'
--    - recurrence_time = '09:00' (gửi lúc 9h sáng mỗi ngày)
--
-- 3. WEEKLY (gửi theo các ngày trong tuần):
--    - is_recurring = TRUE
--    - recurrence_type = 'WEEKLY'
--    - recurrence_days = '[1,3,5]' (Thứ 2, 4, 6)
--    - recurrence_time = '14:00' (gửi lúc 2h chiều)
--
-- 4. MONTHLY (gửi hàng tháng):
--    - is_recurring = TRUE
--    - recurrence_type = 'MONTHLY'
--    - scheduled_time = ngày trong tháng (vd: 1 = mỗi đầu tháng)
--    - recurrence_time = '10:00'
-- =====================================================

-- Ví dụ insert notifications định kỳ:

-- Gửi hàng ngày lúc 9h sáng
-- INSERT INTO scheduled_notifications 
-- (title, body, notification_type, target_audience, is_recurring, recurrence_type, recurrence_time, scheduled_time, status, created_by)
-- VALUES 
-- ('Daily Reminder', 'Don''t forget to practice piano today!', 'REMINDER', 'ALL', TRUE, 'DAILY', '09:00', NOW(), 'PENDING', 1);

-- Gửi mỗi Thứ 2, 3, 5 lúc 2h chiều
-- INSERT INTO scheduled_notifications 
-- (title, body, notification_type, target_audience, is_recurring, recurrence_type, recurrence_days, recurrence_time, scheduled_time, status, created_by)
-- VALUES 
-- ('Weekday Practice', 'Time for your piano lesson!', 'REMINDER', 'ALL', TRUE, 'WEEKLY', '[1,2,4]', '14:00', NOW(), 'PENDING', 1);
