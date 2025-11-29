package com.piano.learn.PianoLearn.service.notification;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.piano.learn.PianoLearn.entity.auth.User;
import com.piano.learn.PianoLearn.entity.notification.Notification;
import com.piano.learn.PianoLearn.entity.notification.Notification.NotificationStatus;
import com.piano.learn.PianoLearn.entity.notification.Notification.NotificationType;
import com.piano.learn.PianoLearn.entity.notification.Notification.TargetAudience;
import com.piano.learn.PianoLearn.entity.notification.ScheduledNotification;
import com.piano.learn.PianoLearn.entity.notification.ScheduledNotification.ScheduleStatus;
import com.piano.learn.PianoLearn.repository.auth.UserRepository;
import com.piano.learn.PianoLearn.repository.notification.NotificationRepository;
import com.piano.learn.PianoLearn.repository.notification.ScheduledNotificationRepository;

/**
 * Service cho admin quản lý và gửi thông báo
 */
@Service
public class AdminNotificationService {
    
    @Autowired
    private FCMService fcmService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private ScheduledNotificationRepository scheduledNotificationRepository;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Gửi thông báo ngay lập tức
     */
    @Transactional
    public Notification sendImmediateNotification(
            Integer adminId,
            String title,
            String body,
            String imageUrl,
            NotificationType type,
            TargetAudience targetAudience,
            String targetCriteria,
            Map<String, String> dataPayload) {
        
        List<User> targetUsers = getTargetUsers(targetAudience, targetCriteria);
        
        int sentCount = 0;
        int failedCount = 0;
        
        // Gửi notification đến từng user
        for (User user : targetUsers) {
            try {
                fcmService.sendNotificationWithData(
                    user.getUserId(), 
                    title, 
                    body, 
                    imageUrl, 
                    dataPayload
                );
                sentCount++;
            } catch (Exception e) {
                failedCount++;
                System.err.println("Failed to send notification to user " + user.getUserId());
            }
        }
        
        // Lưu lịch sử
        User admin = User.builder().userId(adminId).build();
        
        NotificationStatus status = failedCount == 0 ? NotificationStatus.SENT :
                                   sentCount == 0 ? NotificationStatus.FAILED :
                                   NotificationStatus.PARTIAL;
        
        Notification notification = Notification.builder()
                .title(title)
                .body(body)
                .imageUrl(imageUrl)
                .notificationType(type)
                .targetAudience(targetAudience)
                .targetCriteria(targetCriteria)
                .dataPayload(convertMapToJson(dataPayload))
                .sentBy(admin)
                .sentCount(sentCount)
                .failedCount(failedCount)
                .status(status)
                .build();
        
        return notificationRepository.save(notification);
    }
    
    /**
     * Đặt lịch gửi thông báo
     */
    @Transactional
    public ScheduledNotification scheduleNotification(
            Integer adminId,
            String title,
            String body,
            String imageUrl,
            NotificationType type,
            TargetAudience targetAudience,
            String targetCriteria,
            LocalDateTime scheduledTime,
            Map<String, String> dataPayload) {
        
        User admin = User.builder().userId(adminId).build();
        
        ScheduledNotification scheduled = ScheduledNotification.builder()
                .title(title)
                .body(body)
                .imageUrl(imageUrl)
                .notificationType(type)
                .targetAudience(targetAudience)
                .targetCriteria(targetCriteria)
                .scheduledTime(scheduledTime)
                .dataPayload(convertMapToJson(dataPayload))
                .createdBy(admin)
                .status(ScheduleStatus.PENDING)
                .build();
        
        return scheduledNotificationRepository.save(scheduled);
    }
    
    private List<User> getTargetUsers(TargetAudience targetAudience, String targetCriteria) {
        return switch (targetAudience) {
            case ALL -> userRepository.findAll();
                
            case SPECIFIC_USER -> {
                // targetCriteria chứa userId
                Integer userId = Integer.valueOf(targetCriteria);
                yield userRepository.findById(userId)
                        .map(List::of)
                        .orElse(List.of());
            }
                
            case BY_LEVEL ->
                // targetCriteria chứa level name
                userRepository.findByLevelName(targetCriteria);
                
            case BY_ROLE -> {
                // targetCriteria chứa role name
                yield userRepository.findAll().stream()
                        .filter(u -> u.getRole().name().equalsIgnoreCase(targetCriteria))
                        .collect(Collectors.toList());
            }
                
            case ACTIVE_USERS -> {
                // Users có lastLogin trong 30 ngày gần đây
                LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
                yield userRepository.findAll().stream()
                        .filter(u -> u.getLastLogin() != null && u.getLastLogin().isAfter(thirtyDaysAgo))
                        .collect(Collectors.toList());
            }
                
            case INACTIVE_USERS -> {
                // Users không hoạt động > 30 ngày
                LocalDateTime thirtyDaysAgo2 = LocalDateTime.now().minusDays(30);
                yield userRepository.findAll().stream()
                        .filter(u -> u.getLastLogin() == null || u.getLastLogin().isBefore(thirtyDaysAgo2))
                        .collect(Collectors.toList());
            }
        };
    }
    
    /**
     * Hủy scheduled notification
     */
    @Transactional
    public boolean cancelScheduledNotification(Integer scheduledId) {
        return scheduledNotificationRepository.findById(scheduledId)
                .map(scheduled -> {
                    scheduled.setStatus(ScheduleStatus.CANCELLED);
                    scheduled.setUpdatedAt(LocalDateTime.now());
                    scheduledNotificationRepository.save(scheduled);
                    return true;
                })
                .orElse(false);
    }
    
    /**
     * Lấy lịch sử notifications
     */
    public List<Notification> getNotificationHistory(Integer adminId) {
        if (adminId != null) {
            return notificationRepository.findBySentBy_UserIdOrderByCreatedAtDesc(adminId);
        }
        return notificationRepository.findAll();
    }
    
    /**
     * Lấy danh sách scheduled notifications
     */
    public List<ScheduledNotification> getScheduledNotifications(ScheduleStatus status) {
        if (status != null) {
            return scheduledNotificationRepository.findByStatusOrderByScheduledTimeDesc(status);
        }
        return scheduledNotificationRepository.findAll();
    }
    
    /**
     * Thống kê notifications
     */
    public Map<String, Object> getNotificationStats() {
        Map<String, Object> stats = new HashMap<>();
        
        Long totalSent = notificationRepository.getTotalSentCount();
        Long pendingScheduled = scheduledNotificationRepository.countByStatus(ScheduleStatus.PENDING);
        
        stats.put("totalSent", totalSent != null ? totalSent : 0);
        stats.put("pendingScheduled", pendingScheduled != null ? pendingScheduled : 0);
        stats.put("totalNotifications", notificationRepository.count());
        stats.put("totalScheduled", scheduledNotificationRepository.count());
        
        return stats;
    }
    
    /**
     * Convert Map to JSON string
     */
    private String convertMapToJson(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
