package com.piano.learn.PianoLearn.controller.admin;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.piano.learn.PianoLearn.entity.auth.User;
import com.piano.learn.PianoLearn.entity.notification.Notification;
import com.piano.learn.PianoLearn.entity.notification.Notification.NotificationType;
import com.piano.learn.PianoLearn.entity.notification.Notification.TargetAudience;
import com.piano.learn.PianoLearn.entity.notification.ScheduledNotification;
import com.piano.learn.PianoLearn.entity.notification.ScheduledNotification.ScheduleStatus;
import com.piano.learn.PianoLearn.repository.auth.UserRepository;
import com.piano.learn.PianoLearn.service.UserService;
import com.piano.learn.PianoLearn.service.notification.AdminNotificationService;

/**
 * Controller quản lý thông báo cho admin
 */
@Controller
@RequestMapping("/admin/notifications")
public class AdminNotificationController {
    
    @Autowired
    private AdminNotificationService adminNotificationService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Trang quản lý thông báo
     */
    @GetMapping
    public String notificationsPage(Model model) {
        // Lấy thống kê
        Map<String, Object> stats = adminNotificationService.getNotificationStats();
        model.addAttribute("stats", stats);
        
        // Lấy lịch sử gần đây
        List<Notification> recentNotifications = adminNotificationService.getNotificationHistory(null);
        if (recentNotifications.size() > 10) {
            recentNotifications = recentNotifications.subList(0, 10);
        }
        model.addAttribute("recentNotifications", recentNotifications);
        
        // Lấy scheduled notifications
        List<ScheduledNotification> pendingScheduled = 
            adminNotificationService.getScheduledNotifications(ScheduleStatus.PENDING);
        model.addAttribute("pendingScheduled", pendingScheduled);
        
        // Lấy danh sách users để chọn
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        
        // Enum values cho form
        model.addAttribute("notificationTypes", NotificationType.values());
        model.addAttribute("targetAudiences", TargetAudience.values());
        
        return "admin/notifications";
    }
    
    /**
     * Gửi thông báo ngay lập tức
     */
    @PostMapping("/send")
    @ResponseBody
    public ResponseEntity<?> sendNotification(
            @RequestParam String title,
            @RequestParam String body,
            @RequestParam(required = false) String imageUrl,
            @RequestParam NotificationType notificationType,
            @RequestParam TargetAudience targetAudience,
            @RequestParam(required = false) String targetCriteria,
            @RequestParam(required = false) String dataKey1,
            @RequestParam(required = false) String dataValue1,
            @RequestParam(required = false) String dataKey2,
            @RequestParam(required = false) String dataValue2) {
        
        try {
            Integer adminId = getCurrentUserId();
            
            // Build data payload
            Map<String, String> dataPayload = new HashMap<>();
            if (dataKey1 != null && !dataKey1.isEmpty()) {
                dataPayload.put(dataKey1, dataValue1 != null ? dataValue1 : "");
            }
            if (dataKey2 != null && !dataKey2.isEmpty()) {
                dataPayload.put(dataKey2, dataValue2 != null ? dataValue2 : "");
            }
            
            Notification notification = adminNotificationService.sendImmediateNotification(
                adminId, title, body, imageUrl, notificationType, 
                targetAudience, targetCriteria, dataPayload
            );
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Đã gửi thông báo thành công",
                "sentCount", notification.getSentCount(),
                "failedCount", notification.getFailedCount()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Lỗi: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Đặt lịch gửi thông báo
     */
    @PostMapping("/schedule")
    @ResponseBody
    public ResponseEntity<?> scheduleNotification(
            @RequestParam String title,
            @RequestParam String body,
            @RequestParam(required = false) String imageUrl,
            @RequestParam NotificationType notificationType,
            @RequestParam TargetAudience targetAudience,
            @RequestParam(required = false) String targetCriteria,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime scheduledTime,
            @RequestParam(required = false) String dataKey1,
            @RequestParam(required = false) String dataValue1,
            @RequestParam(required = false) String dataKey2,
            @RequestParam(required = false) String dataValue2) {
        
        try {
            Integer adminId = getCurrentUserId();
            
            // Build data payload
            Map<String, String> dataPayload = new HashMap<>();
            if (dataKey1 != null && !dataKey1.isEmpty()) {
                dataPayload.put(dataKey1, dataValue1 != null ? dataValue1 : "");
            }
            if (dataKey2 != null && !dataKey2.isEmpty()) {
                dataPayload.put(dataKey2, dataValue2 != null ? dataValue2 : "");
            }
            
            ScheduledNotification scheduled = adminNotificationService.scheduleNotification(
                adminId, title, body, imageUrl, notificationType,
                targetAudience, targetCriteria, scheduledTime, dataPayload
            );
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Đã đặt lịch thông báo thành công",
                "scheduledId", scheduled.getScheduledNotificationId()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Lỗi: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Hủy scheduled notification
     */
    @PostMapping("/cancel/{scheduledId}")
    @ResponseBody
    public ResponseEntity<?> cancelScheduledNotification(@PathVariable Integer scheduledId) {
        boolean cancelled = adminNotificationService.cancelScheduledNotification(scheduledId);
        
        if (cancelled) {
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Đã hủy thông báo thành công"
            ));
        } else {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Không tìm thấy thông báo"
            ));
        }
    }
    
    /**
     * Lấy lịch sử thông báo (API)
     */
    @GetMapping("/history")
    @ResponseBody
    public ResponseEntity<List<Notification>> getHistory() {
        List<Notification> history = adminNotificationService.getNotificationHistory(null);
        return ResponseEntity.ok(history);
    }
    
    /**
     * Lấy scheduled notifications (API)
     */
    @GetMapping("/scheduled")
    @ResponseBody
    public ResponseEntity<List<ScheduledNotification>> getScheduled(
            @RequestParam(required = false) ScheduleStatus status) {
        List<ScheduledNotification> scheduled = adminNotificationService.getScheduledNotifications(status);
        return ResponseEntity.ok(scheduled);
    }
    
    /**
     * Lấy thống kê (API)
     */
    @GetMapping("/stats")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = adminNotificationService.getNotificationStats();
        return ResponseEntity.ok(stats);
    }
    
    private Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userService.findUserByEmail(email).getUserId();
    }
}
