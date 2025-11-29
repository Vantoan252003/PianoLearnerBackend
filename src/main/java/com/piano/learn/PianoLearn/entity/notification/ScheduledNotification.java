package com.piano.learn.PianoLearn.entity.notification;

import java.time.LocalDateTime;

import com.piano.learn.PianoLearn.entity.auth.User;
import com.piano.learn.PianoLearn.entity.notification.Notification.NotificationType;
import com.piano.learn.PianoLearn.entity.notification.Notification.TargetAudience;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity lưu thông báo đã đặt lịch
 */
@Entity
@Table(name = "scheduled_notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduledNotification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scheduled_notification_id")
    private Integer scheduledNotificationId;
    
    @Column(name = "title", nullable = false, length = 200)
    private String title;
    
    @Column(name = "body", nullable = false, columnDefinition = "TEXT")
    private String body;
    
    @Column(name = "image_url", length = 500)
    private String imageUrl;
    
    @Column(name = "data_payload", columnDefinition = "JSON")
    private String dataPayload;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", length = 50)
    private NotificationType notificationType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "target_audience", length = 50)
    private TargetAudience targetAudience;
    
    @Column(name = "target_criteria", length = 500)
    private String targetCriteria;
    
    @Column(name = "scheduled_time", nullable = false)
    private LocalDateTime scheduledTime;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    @Builder.Default
    private ScheduleStatus status = ScheduleStatus.PENDING;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;
    
    @Column(name = "sent_at")
    private LocalDateTime sentAt;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    public enum ScheduleStatus {
        PENDING,   // Đang chờ
        SENT,      // Đã gửi
        CANCELLED, // Đã hủy
        FAILED     // Gửi thất bại
    }
}
