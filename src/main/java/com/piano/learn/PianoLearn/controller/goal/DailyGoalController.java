package com.piano.learn.PianoLearn.controller.goal;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.piano.learn.PianoLearn.entity.auth.User;
import com.piano.learn.PianoLearn.entity.goal.DailyGoal;
import com.piano.learn.PianoLearn.service.UserService;
import com.piano.learn.PianoLearn.service.admin.DailyGoalService;

@RestController
@RequestMapping("/api/daily-goals")
public class DailyGoalController {
    
    @Autowired
    private DailyGoalService dailyGoalService;
    
    @Autowired
    private UserService userService;
    
    /**
     * GET /api/daily-goals/check-status
     * Kiểm tra đã điểm danh hôm nay chưa
     */
    @GetMapping("/check-status")
    public ResponseEntity<CheckInStatusResponse> checkStatus() {
        User user = getCurrentUser();
        boolean hasCheckedIn = dailyGoalService.hasCheckedInToday(user.getUserId());
        DailyGoal todayCheckIn = dailyGoalService.getTodayCheckIn(user.getUserId());
        
        CheckInStatusResponse response = CheckInStatusResponse.builder()
            .hasCheckedInToday(hasCheckedIn)
            .streakDays(user.getStreakDays())
            .totalExp(user.getTotalExp())
            .todayCheckIn(todayCheckIn)
            .message(hasCheckedIn 
                ? "✅ Bạn đã điểm danh hôm nay rồi!" 
                : "🎹 Điểm danh hôm nay để nhận EXP nhé!")
            .build();
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * POST /api/daily-goals/check-in
     * Điểm danh hôm nay
     */
    @PostMapping("/check-in")
    public ResponseEntity<CheckInResponse> checkIn() {
        User user = getCurrentUser();
        
        try {
            DailyGoal goal = dailyGoalService.checkInToday(user.getUserId());
            
            // Lấy lại thông tin user sau khi update
            user = userService.findUserByEmail(user.getEmail());
            
            CheckInResponse response = CheckInResponse.builder()
                .success(true)
                .expEarned(goal.getExpEarned())
                .totalExp(user.getTotalExp())
                .streakDays(user.getStreakDays())
                .checkedInAt(goal.getCheckedInAt())
                .message(String.format("🎉 Điểm danh thành công! +%d EXP | Streak: %d ngày", 
                    goal.getExpEarned(), user.getStreakDays()))
                .build();
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            CheckInResponse response = CheckInResponse.builder()
                .success(false)
                .message(e.getMessage())
                .build();
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * GET /api/daily-goals/history
     * Lấy lịch sử điểm danh
     */
    @GetMapping("/history")
    public ResponseEntity<List<DailyGoal>> getHistory() {
        User user = getCurrentUser();
        List<DailyGoal> history = dailyGoalService.getCheckInHistory(user.getUserId());
        return ResponseEntity.ok(history);
    }
    
    // ========== Helper Methods ==========
    
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userService.findUserByEmail(email);
    }
    
    // ========== Response DTOs ==========
    
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class CheckInStatusResponse {
        private Boolean hasCheckedInToday;
        private Integer streakDays;
        private Integer totalExp;
        private DailyGoal todayCheckIn;
        private String message;
    }
    
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class CheckInResponse {
        private Boolean success;
        private Integer expEarned;
        private Integer totalExp;
        private Integer streakDays;
        private java.time.LocalDateTime checkedInAt;
        private String message;
    }
}
