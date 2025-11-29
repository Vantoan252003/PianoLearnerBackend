package com.piano.learn.PianoLearn.repository.notification;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.piano.learn.PianoLearn.entity.notification.Notification;
import com.piano.learn.PianoLearn.entity.notification.Notification.TargetAudience;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    
    /**
     * Lấy notifications theo user
     */
    List<Notification> findByUser_UserIdOrderByCreatedAtDesc(Integer userId);
    
    /**
     * Lấy notifications theo target audience
     */
    List<Notification> findByTargetAudienceOrderByCreatedAtDesc(TargetAudience targetAudience);
    
    /**
     * Lấy notifications trong khoảng thời gian
     */
    List<Notification> findByCreatedAtBetweenOrderByCreatedAtDesc(
        LocalDateTime startDate, 
        LocalDateTime endDate
    );
    
    /**
     * Lấy notifications của admin đã gửi
     */
    List<Notification> findBySentBy_UserIdOrderByCreatedAtDesc(Integer adminId);
    
    /**
     * Thống kê số lượng thông báo đã gửi
     */
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.sentBy.userId = :adminId")
    Long countBySentBy(@Param("adminId") Integer adminId);
    
    /**
     * Thống kê tổng số người nhận
     */
    @Query("SELECT SUM(n.sentCount) FROM Notification n")
    Long getTotalSentCount();
}
