package com.piano.learn.PianoLearn.service.notification;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.piano.learn.PianoLearn.entity.auth.User;
import com.piano.learn.PianoLearn.entity.notification.Notification;
import com.piano.learn.PianoLearn.entity.notification.Notification.NotificationStatus;
import com.piano.learn.PianoLearn.entity.notification.ScheduledNotification;
import com.piano.learn.PianoLearn.entity.notification.ScheduledNotification.ScheduleStatus;
import com.piano.learn.PianoLearn.repository.auth.UserRepository;
import com.piano.learn.PianoLearn.repository.notification.NotificationRepository;
import com.piano.learn.PianoLearn.repository.notification.ScheduledNotificationRepository;

/**
 * Service xử lý gửi scheduled notifications
 */
@Service
public class ScheduledNotificationProcessor {
    
    @Autowired
    private ScheduledNotificationRepository scheduledNotificationRepository;
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private FCMService fcmService;
    
    @Autowired
    private UserRepository userRepository;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Chạy mỗi phút để check và gửi scheduled notifications
     */
    @Scheduled(fixedRate = 60000) // Chạy mỗi 1 phút
    @Transactional
    public void processScheduledNotifications() {
        LocalDateTime now = LocalDateTime.now();
        
        // Lấy tất cả notifications cần gửi
        List<ScheduledNotification> pendingNotifications = 
            scheduledNotificationRepository.findByStatusAndScheduledTimeLessThanEqualOrderByScheduledTimeAsc(
                ScheduleStatus.PENDING, 
                now
            );
        
        System.out.println("Found " + pendingNotifications.size() + " scheduled notifications to send");
        
        for (ScheduledNotification scheduled : pendingNotifications) {
            try {
                sendScheduledNotification(scheduled);
            } catch (Exception e) {
                System.err.println("Error sending scheduled notification " + 
                                  scheduled.getScheduledNotificationId() + ": " + e.getMessage());
                
                // Đánh dấu failed
                scheduled.setStatus(ScheduleStatus.FAILED);
                scheduled.setUpdatedAt(LocalDateTime.now());
                scheduledNotificationRepository.save(scheduled);
            }
        }
    }
    
    /**
     * Gửi một scheduled notification
     */
    private void sendScheduledNotification(ScheduledNotification scheduled) {
        // Lấy target users
        List<User> targetUsers = getTargetUsers(
            scheduled.getTargetAudience(), 
            scheduled.getTargetCriteria()
        );
        
        // Parse data payload
        Map<String, String> dataPayload = parseDataPayload(scheduled.getDataPayload());
        
        int sentCount = 0;
        int failedCount = 0;
        
        // Gửi đến từng user
        for (User user : targetUsers) {
            try {
                fcmService.sendNotificationWithData(
                    user.getUserId(), 
                    scheduled.getTitle(), 
                    scheduled.getBody(), 
                    scheduled.getImageUrl(), 
                    dataPayload
                );
                sentCount++;
            } catch (Exception e) {
                failedCount++;
                System.err.println("Failed to send to user " + user.getUserId());
            }
        }
        
        // Update scheduled notification status
        scheduled.setStatus(ScheduleStatus.SENT);
        scheduled.setSentAt(LocalDateTime.now());
        scheduled.setUpdatedAt(LocalDateTime.now());
        scheduledNotificationRepository.save(scheduled);
        
        // Save to notification history
        NotificationStatus status = failedCount == 0 ? NotificationStatus.SENT :
                                   sentCount == 0 ? NotificationStatus.FAILED :
                                   NotificationStatus.PARTIAL;
        
        Notification notification = Notification.builder()
                .title(scheduled.getTitle())
                .body(scheduled.getBody())
                .imageUrl(scheduled.getImageUrl())
                .notificationType(scheduled.getNotificationType())
                .targetAudience(scheduled.getTargetAudience())
                .targetCriteria(scheduled.getTargetCriteria())
                .dataPayload(scheduled.getDataPayload())
                .sentBy(scheduled.getCreatedBy())
                .sentCount(sentCount)
                .failedCount(failedCount)
                .status(status)
                .build();
        
        notificationRepository.save(notification);
        
        System.out.println("Sent scheduled notification " + scheduled.getScheduledNotificationId() + 
                          ": " + sentCount + " success, " + failedCount + " failed");
    }
    
    /**
     * Lấy target users (copy logic từ AdminNotificationService)
     */
    private List<User> getTargetUsers(
            com.piano.learn.PianoLearn.entity.notification.Notification.TargetAudience targetAudience, 
            String targetCriteria) {
        
        return switch (targetAudience) {
            case ALL -> userRepository.findAll();
                
            case SPECIFIC_USER -> {
                Integer userId = Integer.valueOf(targetCriteria);
                yield userRepository.findById(userId)
                        .map(List::of)
                        .orElse(List.of());
            }
                
            case BY_LEVEL -> userRepository.findByLevelName(targetCriteria);
                
            case BY_ROLE -> userRepository.findAll().stream()
                    .filter(u -> u.getRole().name().equalsIgnoreCase(targetCriteria))
                    .toList();
                
            case ACTIVE_USERS -> {
                LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
                yield userRepository.findAll().stream()
                        .filter(u -> u.getLastLogin() != null && u.getLastLogin().isAfter(thirtyDaysAgo))
                        .toList();
            }
                
            case INACTIVE_USERS -> {
                LocalDateTime thirtyDaysAgo2 = LocalDateTime.now().minusDays(30);
                yield userRepository.findAll().stream()
                        .filter(u -> u.getLastLogin() == null || u.getLastLogin().isBefore(thirtyDaysAgo2))
                        .toList();
            }
        };
    }
    
    /**
     * Parse JSON data payload
     */
    private Map<String, String> parseDataPayload(String json) {
        if (json == null || json.isEmpty()) {
            return new HashMap<>();
        }
        
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, String>>() {});
        } catch (JsonProcessingException e) {
            System.err.println("Error parsing data payload JSON: " + e.getMessage());
            return new HashMap<>();
        }
    }
}
