package com.piano.learn.PianoLearn.entity.notification;

import java.time.LocalDateTime;

import com.piano.learn.PianoLearn.entity.auth.User;

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
 * Entity lưu lịch sử thông báo đã gửi
 */
@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Integer notificationId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // null nếu gửi cho tất cả
    
    @Column(name = "title", nullable = false, length = 200)
    private String title;
    
    @Column(name = "body", nullable = false, columnDefinition = "TEXT")
    private String body;
    
    @Column(name = "image_url", length = 500)
    private String imageUrl;
    
    @Column(name = "data_payload", columnDefinition = "JSON")
    private String dataPayload; // JSON string
    
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", length = 50)
    private NotificationType notificationType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "target_audience", length = 50)
    private TargetAudience targetAudience; // ALL, SPECIFIC_USER, BY_LEVEL, etc.
    
    @Column(name = "target_criteria", length = 500)
    private String targetCriteria; // level_name, user_ids, etc.
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    @Builder.Default
    private NotificationStatus status = NotificationStatus.SENT;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sent_by")
    private User sentBy; // Admin gửi
    
    @Column(name = "sent_count")
    @Builder.Default
    private Integer sentCount = 0;
    
    @Column(name = "failed_count")
    @Builder.Default
    private Integer failedCount = 0;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public enum NotificationType {
        SYSTEM,      // Thông báo hệ thống
        MARKETING,   // Marketing/Promotion
        UPDATE,      // Cập nhật tính năng
        ACHIEVEMENT, // Thông báo thành tích
        REMINDER,    // Nhắc nhở
        CUSTOM       // Tùy chỉnh
    }
    
    public enum TargetAudience {
        ALL,            // Tất cả users
        SPECIFIC_USER,  // User cụ thể
        BY_LEVEL,       // Theo level (beginner, intermediate, advanced)
        BY_ROLE,        // Theo role (learner, instructor)
        ACTIVE_USERS,   // Users hoạt động gần đây
        INACTIVE_USERS  // Users không hoạt động
    }
    
    public enum NotificationStatus {
        SENT,      // Đã gửi
        FAILED,    // Gửi thất bại
        PARTIAL    // Gửi một phần
    }
}
