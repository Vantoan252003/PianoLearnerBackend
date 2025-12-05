package com.piano.learn.PianoLearn.repository.notification;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.piano.learn.PianoLearn.entity.notification.ScheduledNotification;
import com.piano.learn.PianoLearn.entity.notification.ScheduledNotification.ScheduleStatus;

@Repository
public interface ScheduledNotificationRepository extends JpaRepository<ScheduledNotification, Integer> {
    
    /**
     * Lấy notifications đang chờ gửi
     */
    List<ScheduledNotification> findByStatusAndScheduledTimeLessThanEqualOrderByScheduledTimeAsc(
        ScheduleStatus status,
        LocalDateTime currentTime
    );
    
    /**
     * Lấy tất cả scheduled notifications theo status
     */
    List<ScheduledNotification> findByStatusOrderByScheduledTimeDesc(ScheduleStatus status);
    
    /**
     * Lấy scheduled notifications của admin
     */
    List<ScheduledNotification> findByCreatedBy_UserIdOrderByCreatedAtDesc(Integer adminId);
    
    /**
     * Đếm số lượng pending notifications
     */
    Long countByStatus(ScheduleStatus status);
    
    /**
     * Lấy notifications gửi một lần đã đến hạn
     */
    List<ScheduledNotification> findByStatusAndIsRecurringAndScheduledTimeBefore(
        ScheduleStatus status,
        Boolean isRecurring,
        LocalDateTime time
    );
    
    /**
     * Lấy recurring notifications đang active
     */
    List<ScheduledNotification> findByStatusAndIsRecurring(
        ScheduleStatus status,
        Boolean isRecurring
    );
}
