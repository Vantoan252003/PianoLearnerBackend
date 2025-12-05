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
            Map<String, String> dataPayload,
            Boolean isRecurring,
            String recurrenceTypeStr,
            String recurrenceTime,
            String recurrenceDays) {
        
        System.out.println("=== Schedule Notification Debug ===");
        System.out.println("ScheduledTime received: " + scheduledTime);
        System.out.println("Current time: " + LocalDateTime.now());
        System.out.println("isRecurring: " + isRecurring);
        System.out.println("recurrenceType: " + recurrenceTypeStr);
        System.out.println("recurrenceTime: " + recurrenceTime);
        System.out.println("recurrenceDays: " + recurrenceDays);
        
        User admin = User.builder().userId(adminId).build();
        
        // Parse recurrence type
        ScheduledNotification.RecurrenceType recurrenceType = null;
        if (recurrenceTypeStr != null && !recurrenceTypeStr.isEmpty()) {
            try {
                recurrenceType = ScheduledNotification.RecurrenceType.valueOf(recurrenceTypeStr);
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid recurrence type: " + recurrenceTypeStr);
            }
        }
        
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
                .isRecurring(isRecurring != null ? isRecurring : false)
                .recurrenceType(recurrenceType)
                .recurrenceTime(recurrenceTime)
                .recurrenceDays(recurrenceDays)
                .build();
        
        ScheduledNotification saved = scheduledNotificationRepository.save(scheduled);
        System.out.println("Saved scheduled notification with ID: " + saved.getScheduledNotificationId() 
            + ", ScheduledTime: " + saved.getScheduledTime()
            + ", isRecurring: " + saved.getIsRecurring()
            + ", recurrenceType: " + saved.getRecurrenceType());
        
        return saved;
    }
    
    /**
     * Đặt lịch gửi thông báo (overload method cũ để tương thích ngược)
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
        
        return scheduleNotification(adminId, title, body, imageUrl, type, 
            targetAudience, targetCriteria, scheduledTime, dataPayload,
            false, "ONCE", null, null);
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
     * Xóa scheduled notification vĩnh viễn
     */
    @Transactional
    public boolean deleteScheduledNotification(Integer scheduledId) {
        return scheduledNotificationRepository.findById(scheduledId)
                .map(scheduled -> {
                    scheduledNotificationRepository.delete(scheduled);
                    return true;
                })
                .orElse(false);
    }
    
    /**
     * Kích hoạt lại scheduled notification
     */
    @Transactional
    public boolean activateScheduledNotification(Integer scheduledId) {
        return scheduledNotificationRepository.findById(scheduledId)
                .map(scheduled -> {
                    if (scheduled.getStatus() == ScheduleStatus.CANCELLED) {
                        scheduled.setStatus(ScheduleStatus.PENDING);
                        scheduled.setUpdatedAt(LocalDateTime.now());
                        scheduledNotificationRepository.save(scheduled);
                        return true;
                    }
                    return false;
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
     * Lấy tất cả scheduled notifications
     */
    public List<ScheduledNotification> getAllScheduledNotifications() {
        return scheduledNotificationRepository.findAll();
    }
    
    /**
     * Lấy scheduled notification theo ID
     */
    public ScheduledNotification getScheduledNotificationById(Integer id) {
        return scheduledNotificationRepository.findById(id).orElse(null);
    }
    
    /**
     * Cập nhật scheduled notification
     */
    @Transactional
    public boolean updateScheduledNotification(
            Integer scheduledId,
            String title,
            String body,
            String imageUrl,
            NotificationType type,
            TargetAudience targetAudience,
            String targetCriteria,
            LocalDateTime scheduledTime,
            Map<String, String> dataPayload) {
        
        return scheduledNotificationRepository.findById(scheduledId)
                .map(scheduled -> {
                    scheduled.setTitle(title);
                    scheduled.setBody(body);
                    scheduled.setImageUrl(imageUrl);
                    scheduled.setNotificationType(type);
                    scheduled.setTargetAudience(targetAudience);
                    scheduled.setTargetCriteria(targetCriteria);
                    scheduled.setScheduledTime(scheduledTime);
                    scheduled.setDataPayload(convertMapToJson(dataPayload));
                    scheduled.setUpdatedAt(LocalDateTime.now());
                    scheduledNotificationRepository.save(scheduled);
                    return true;
                })
                .orElse(false);
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
    
    /**
     * Gửi scheduled notification ngay lập tức (được gọi bởi scheduler)
     * Method này được sử dụng bởi RecurringNotificationScheduler
     */
    @Transactional
    public void sendScheduledNotificationNow(ScheduledNotification scheduled) {
        try {
            // Gửi notification dựa trên target audience
            Notification notification = sendImmediateNotification(
                scheduled.getCreatedBy() != null ? scheduled.getCreatedBy().getUserId() : null,
                scheduled.getTitle(),
                scheduled.getBody(),
                scheduled.getImageUrl(),
                scheduled.getNotificationType(),
                scheduled.getTargetAudience(),
                scheduled.getTargetCriteria(),
                parseJsonToMap(scheduled.getDataPayload())
            );
            
            System.out.println("Successfully sent scheduled notification: " + 
                scheduled.getScheduledNotificationId() + " with result: " + 
                notification.getNotificationId());
                
        } catch (Exception e) {
            System.err.println("Failed to send scheduled notification: " + 
                scheduled.getScheduledNotificationId());
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * Parse JSON string to Map
     */
    private Map<String, String> parseJsonToMap(String json) {
        if (json == null || json.isEmpty()) {
            return new HashMap<>();
        }
        try {
            return objectMapper.readValue(json, 
                objectMapper.getTypeFactory().constructMapType(Map.class, String.class, String.class));
        } catch (JsonProcessingException e) {
            System.err.println("Error parsing JSON to Map: " + e.getMessage());
            return new HashMap<>();
        }
    }
}
