-- =====================================================
-- TRIGGER: Tự động giới hạn 10 notifications cho mỗi user
-- =====================================================

-- Xóa trigger cũ nếu có
DROP TRIGGER IF EXISTS limit_user_notifications;

-- Tạo trigger mới
DELIMITER $$

CREATE TRIGGER limit_user_notifications
AFTER INSERT ON notifications
FOR EACH ROW
BEGIN
    -- Chỉ xử lý nếu notification có user_id (không phải notification gửi cho tất cả)
    IF NEW.user_id IS NOT NULL THEN
        -- Đếm số lượng notifications của user này
        DECLARE notification_count INT;
        
        SELECT COUNT(*) INTO notification_count
        FROM notifications
        WHERE user_id = NEW.user_id;
        
        -- Nếu vượt quá 10, xóa các notification cũ nhất
        IF notification_count > 10 THEN
            DELETE FROM notifications
            WHERE user_id = NEW.user_id
            AND notification_id IN (
                SELECT notification_id FROM (
                    SELECT notification_id
                    FROM notifications
                    WHERE user_id = NEW.user_id
                    ORDER BY created_at ASC
                    LIMIT (notification_count - 10)
                ) AS old_notifications
            );
        END IF;
    END IF;
END$$

DELIMITER ;

-- =====================================================
-- Test trigger (optional - comment out nếu không cần)
-- =====================================================

-- Kiểm tra trigger hoạt động
-- Uncomment các dòng dưới để test:

/*
-- Insert 15 notifications cho user_id = 1 để test
INSERT INTO notifications (user_id, title, body, notification_type, target_audience, status, sent_count)
VALUES 
(1, 'Test 1', 'Body 1', 'SYSTEM', 'SPECIFIC_USER', 'SENT', 1),
(1, 'Test 2', 'Body 2', 'SYSTEM', 'SPECIFIC_USER', 'SENT', 1),
(1, 'Test 3', 'Body 3', 'SYSTEM', 'SPECIFIC_USER', 'SENT', 1),
(1, 'Test 4', 'Body 4', 'SYSTEM', 'SPECIFIC_USER', 'SENT', 1),
(1, 'Test 5', 'Body 5', 'SYSTEM', 'SPECIFIC_USER', 'SENT', 1),
(1, 'Test 6', 'Body 6', 'SYSTEM', 'SPECIFIC_USER', 'SENT', 1),
(1, 'Test 7', 'Body 7', 'SYSTEM', 'SPECIFIC_USER', 'SENT', 1),
(1, 'Test 8', 'Body 8', 'SYSTEM', 'SPECIFIC_USER', 'SENT', 1),
(1, 'Test 9', 'Body 9', 'SYSTEM', 'SPECIFIC_USER', 'SENT', 1),
(1, 'Test 10', 'Body 10', 'SYSTEM', 'SPECIFIC_USER', 'SENT', 1),
(1, 'Test 11', 'Body 11', 'SYSTEM', 'SPECIFIC_USER', 'SENT', 1),
(1, 'Test 12', 'Body 12', 'SYSTEM', 'SPECIFIC_USER', 'SENT', 1),
(1, 'Test 13', 'Body 13', 'SYSTEM', 'SPECIFIC_USER', 'SENT', 1),
(1, 'Test 14', 'Body 14', 'SYSTEM', 'SPECIFIC_USER', 'SENT', 1),
(1, 'Test 15', 'Body 15', 'SYSTEM', 'SPECIFIC_USER', 'SENT', 1);

-- Kiểm tra kết quả - chỉ còn 10 notifications mới nhất
SELECT notification_id, title, created_at 
FROM notifications 
WHERE user_id = 1 
ORDER BY created_at DESC;
*/

-- =====================================================
-- Hướng dẫn sử dụng:
-- 1. Chạy script này trên database MySQL
-- 2. Trigger sẽ tự động hoạt động khi insert notification mới
-- 3. Chỉ giữ lại 10 notifications mới nhất cho mỗi user
-- 4. Notifications gửi cho ALL users (user_id = NULL) không bị ảnh hưởng
-- =====================================================
