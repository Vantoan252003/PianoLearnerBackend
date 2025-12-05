package com.piano.learn.PianoLearn.scheduler;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.piano.learn.PianoLearn.entity.notification.ScheduledNotification;
import com.piano.learn.PianoLearn.entity.notification.ScheduledNotification.RecurrenceType;
import com.piano.learn.PianoLearn.entity.notification.ScheduledNotification.ScheduleStatus;
import com.piano.learn.PianoLearn.repository.notification.ScheduledNotificationRepository;
import com.piano.learn.PianoLearn.service.notification.AdminNotificationService;

import lombok.extern.slf4j.Slf4j;

/**
 * Scheduler để xử lý gửi notifications định kỳ
 * Chạy mỗi 5 phút kiểm tra và gửi notifications
 */
@Component
@Slf4j
public class RecurringNotificationScheduler {
    
    @Autowired
    private ScheduledNotificationRepository scheduledNotificationRepository;
    
    @Autowired
    private AdminNotificationService adminNotificationService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Chạy mỗi 5 phút để kiểm tra và gửi notifications
     * Cron: mỗi 5 phút
     */
    @Scheduled(cron = "0 */5 * * * *")
    @Transactional
    public void processRecurringNotifications() {
        log.info("Starting recurring notification check...");
        
        try {
            // 1. Xử lý notifications một lần (ONCE)
            processOneTimeNotifications();
            
            // 2. Xử lý recurring notifications
            processRecurringNotificationsByType();
            
            log.info("Recurring notification check completed successfully");
        } catch (Exception e) {
            log.error("Error processing recurring notifications", e);
        }
    }
    
    /**
     * Xử lý notifications gửi một lần
     */
    private void processOneTimeNotifications() {
        LocalDateTime now = LocalDateTime.now();
        
        List<ScheduledNotification> oneTimeNotifications = scheduledNotificationRepository
            .findByStatusAndIsRecurringAndScheduledTimeBefore(
                ScheduleStatus.PENDING, 
                false, 
                now
            );
        
        log.info("Found {} one-time notifications to send", oneTimeNotifications.size());
        
        for (ScheduledNotification scheduled : oneTimeNotifications) {
            try {
                sendScheduledNotification(scheduled);
                scheduled.setStatus(ScheduleStatus.SENT);
                scheduled.setSentAt(LocalDateTime.now());
                scheduledNotificationRepository.save(scheduled);
                
                log.info("Sent one-time notification: {}", scheduled.getScheduledNotificationId());
            } catch (Exception e) {
                log.error("Failed to send one-time notification: {}", scheduled.getScheduledNotificationId(), e);
                scheduled.setStatus(ScheduleStatus.FAILED);
                scheduledNotificationRepository.save(scheduled);
            }
        }
    }
    
    /**
     * Xử lý recurring notifications theo loại
     */
    private void processRecurringNotificationsByType() {
        List<ScheduledNotification> recurringNotifications = scheduledNotificationRepository
            .findByStatusAndIsRecurring(ScheduleStatus.PENDING, true);
        
        log.info("Found {} recurring notifications", recurringNotifications.size());
        
        LocalDateTime now = LocalDateTime.now();
        LocalTime currentTime = now.toLocalTime();
        
        for (ScheduledNotification scheduled : recurringNotifications) {
            try {
                boolean shouldSend = false;
                
                switch (scheduled.getRecurrenceType()) {
                    case DAILY:
                        shouldSend = shouldSendDaily(scheduled, currentTime);
                        break;
                    case WEEKLY:
                        shouldSend = shouldSendWeekly(scheduled, now, currentTime);
                        break;
                    case MONTHLY:
                        shouldSend = shouldSendMonthly(scheduled, now, currentTime);
                        break;
                    default:
                        break;
                }
                
                if (shouldSend) {
                    sendScheduledNotification(scheduled);
                    scheduled.setLastSentAt(LocalDateTime.now());
                    scheduledNotificationRepository.save(scheduled);
                    
                    log.info("Sent recurring notification ({}): {}", 
                        scheduled.getRecurrenceType(), 
                        scheduled.getScheduledNotificationId());
                }
            } catch (Exception e) {
                log.error("Failed to process recurring notification: {}", 
                    scheduled.getScheduledNotificationId(), e);
            }
        }
    }
    
    /**
     * Kiểm tra xem có nên gửi notification DAILY không
     */
    private boolean shouldSendDaily(ScheduledNotification scheduled, LocalTime currentTime) {
        if (scheduled.getRecurrenceTime() == null) {
            return false;
        }
        
        LocalTime scheduledTime = LocalTime.parse(scheduled.getRecurrenceTime(), 
            DateTimeFormatter.ofPattern("HH:mm"));
        
        // Kiểm tra xem đã đến giờ gửi chưa (trong khoảng 5 phút)
        if (!isTimeInRange(currentTime, scheduledTime, 5)) {
            return false;
        }
        
        // Kiểm tra xem hôm nay đã gửi chưa
        return !wasSentToday(scheduled.getLastSentAt());
    }
    
    /**
     * Kiểm tra xem có nên gửi notification WEEKLY không
     */
    private boolean shouldSendWeekly(ScheduledNotification scheduled, 
                                     LocalDateTime now, 
                                     LocalTime currentTime) {
        if (scheduled.getRecurrenceTime() == null || scheduled.getRecurrenceDays() == null) {
            return false;
        }
        
        try {
            // Parse days: [1,2,3,4,5] = Monday to Friday
            List<Integer> days = objectMapper.readValue(
                scheduled.getRecurrenceDays(), 
                new TypeReference<List<Integer>>() {}
            );
            
            int currentDayOfWeek = now.getDayOfWeek().getValue(); // 1=Monday, 7=Sunday
            
            // Kiểm tra xem hôm nay có trong danh sách không
            if (!days.contains(currentDayOfWeek)) {
                return false;
            }
            
            LocalTime scheduledTime = LocalTime.parse(scheduled.getRecurrenceTime(), 
                DateTimeFormatter.ofPattern("HH:mm"));
            
            // Kiểm tra xem đã đến giờ gửi chưa
            if (!isTimeInRange(currentTime, scheduledTime, 5)) {
                return false;
            }
            
            // Kiểm tra xem hôm nay đã gửi chưa
            return !wasSentToday(scheduled.getLastSentAt());
            
        } catch (Exception e) {
            log.error("Error parsing recurrence days for scheduled notification: {}", 
                scheduled.getScheduledNotificationId(), e);
            return false;
        }
    }
    
    /**
     * Kiểm tra xem có nên gửi notification MONTHLY không
     */
    private boolean shouldSendMonthly(ScheduledNotification scheduled, 
                                      LocalDateTime now, 
                                      LocalTime currentTime) {
        if (scheduled.getRecurrenceTime() == null || scheduled.getScheduledTime() == null) {
            return false;
        }
        
        int scheduledDayOfMonth = scheduled.getScheduledTime().getDayOfMonth();
        int currentDayOfMonth = now.getDayOfMonth();
        
        // Kiểm tra xem hôm nay có phải ngày trong tháng cần gửi không
        if (currentDayOfMonth != scheduledDayOfMonth) {
            return false;
        }
        
        LocalTime scheduledTime = LocalTime.parse(scheduled.getRecurrenceTime(), 
            DateTimeFormatter.ofPattern("HH:mm"));
        
        // Kiểm tra xem đã đến giờ gửi chưa
        if (!isTimeInRange(currentTime, scheduledTime, 5)) {
            return false;
        }
        
        // Kiểm tra xem tháng này đã gửi chưa
        return !wasSentThisMonth(scheduled.getLastSentAt());
    }
    
    /**
     * Kiểm tra xem thời gian hiện tại có trong khoảng thời gian cho phép không
     * @param current Thời gian hiện tại
     * @param target Thời gian mục tiêu
     * @param minuteRange Khoảng phút cho phép
     */
    private boolean isTimeInRange(LocalTime current, LocalTime target, int minuteRange) {
        LocalTime startRange = target.minusMinutes(minuteRange);
        LocalTime endRange = target.plusMinutes(minuteRange);
        
        return !current.isBefore(startRange) && !current.isAfter(endRange);
    }
    
    /**
     * Kiểm tra xem hôm nay đã gửi notification chưa
     */
    private boolean wasSentToday(LocalDateTime lastSentAt) {
        if (lastSentAt == null) {
            return false;
        }
        
        LocalDate lastSentDate = lastSentAt.toLocalDate();
        LocalDate today = LocalDate.now();
        
        return lastSentDate.equals(today);
    }
    
    /**
     * Kiểm tra xem tháng này đã gửi notification chưa
     */
    private boolean wasSentThisMonth(LocalDateTime lastSentAt) {
        if (lastSentAt == null) {
            return false;
        }
        
        return lastSentAt.getMonth() == LocalDateTime.now().getMonth() 
            && lastSentAt.getYear() == LocalDateTime.now().getYear();
    }
    
    /**
     * Gửi scheduled notification
     */
    private void sendScheduledNotification(ScheduledNotification scheduled) {
        adminNotificationService.sendScheduledNotificationNow(scheduled);
    }
}
