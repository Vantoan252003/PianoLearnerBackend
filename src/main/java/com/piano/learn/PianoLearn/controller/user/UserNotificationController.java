package com.piano.learn.PianoLearn.controller.user;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.piano.learn.PianoLearn.entity.notification.Notification;
import com.piano.learn.PianoLearn.repository.notification.NotificationRepository;
import com.piano.learn.PianoLearn.service.UserService;

@RestController
@RequestMapping("/api/user/notifications")
public class UserNotificationController {

    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private UserService userService;

    /**
     * Lấy danh sách thông báo của user hiện tại
     */
    @GetMapping
    public ResponseEntity<?> getMyNotifications() {
        Integer userId = getCurrentUserId();

        List<Notification> notifications = notificationRepository.findByUser_UserIdOrderByCreatedAtDesc(userId);

        List<Map<String, Object>> result = notifications.stream()
            .map(this::convertToMap)
            .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of(
            "success", true,
            "notifications", result
        ));
    }

    /**
     * Lấy thông báo theo ID
     */
    @GetMapping("/{notificationId}")
    public ResponseEntity<?> getNotificationById(@PathVariable Integer notificationId) {
        Integer userId = getCurrentUserId();

        Notification notification = notificationRepository.findById(notificationId).orElse(null);

        if (notification == null) {
            return ResponseEntity.notFound().build();
        }

        // Kiểm tra quyền truy cập - chỉ user nhận thông báo mới được xem
        if (notification.getUser() == null || !notification.getUser().getUserId().equals(userId)) {
            return ResponseEntity.status(403).body(Map.of(
                "success", false,
                "message", "Không có quyền truy cập thông báo này"
            ));
        }

        return ResponseEntity.ok(Map.of(
            "success", true,
            "notification", convertToMap(notification)
        ));
    }

    /**
     * Convert Notification entity sang Map để trả về JSON
     */
    private Map<String, Object> convertToMap(Notification notification) {
        Map<String, Object> map = new java.util.HashMap<>();
        map.put("notificationId", notification.getNotificationId());
        map.put("title", notification.getTitle());
        map.put("body", notification.getBody());
        map.put("imageUrl", notification.getImageUrl());
        map.put("dataPayload", notification.getDataPayload());
        map.put("notificationType", notification.getNotificationType());
        map.put("targetAudience", notification.getTargetAudience());
        map.put("targetCriteria", notification.getTargetCriteria());
        map.put("status", notification.getStatus());
        map.put("sentCount", notification.getSentCount());
        map.put("failedCount", notification.getFailedCount());
        map.put("createdAt", notification.getCreatedAt());
        
        if (notification.getSentBy() != null) {
            Map<String, Object> sentBy = new java.util.HashMap<>();
            sentBy.put("userId", notification.getSentBy().getUserId());
            sentBy.put("fullName", notification.getSentBy().getFullName());
            sentBy.put("email", notification.getSentBy().getEmail());
            map.put("sentBy", sentBy);
        } else {
            map.put("sentBy", null);
        }
        
        return map;
    }
    
    private Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userService.findUserByEmail(email).getUserId();
    }
}