package com.piano.learn.PianoLearn.service.admin;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.piano.learn.PianoLearn.entity.auth.User;
import com.piano.learn.PianoLearn.entity.goal.DailyGoal;
import com.piano.learn.PianoLearn.repository.auth.UserRepository;
import com.piano.learn.PianoLearn.repository.goal.DailyGoalRepository;

@Service
public class DailyGoalService {
    
    @Autowired
    private DailyGoalRepository dailyGoalRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    // ============ ADMIN METHODS ============
    
    public List<DailyGoal> getAllDailyGoals() {
        return dailyGoalRepository.findAll();
    }
    
    public DailyGoal getDailyGoalById(Integer id) {
        return dailyGoalRepository.findById(id).orElse(null);
    }
    
    public DailyGoal createDailyGoal(DailyGoal dailyGoal) {
        return dailyGoalRepository.save(dailyGoal);
    }
    
    public DailyGoal updateDailyGoal(Integer id, DailyGoal dailyGoal) {
        if (dailyGoalRepository.existsById(id)) {
            dailyGoal.setGoalId(id);
            return dailyGoalRepository.save(dailyGoal);
        }
        return null;
    }
    
    public boolean deleteDailyGoal(Integer id) {
        if (dailyGoalRepository.existsById(id)) {
            dailyGoalRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    // ============ USER METHODS - SIMPLIFIED CHECK-IN SYSTEM ============
    
    /**
     * Kiểm tra user đã điểm danh hôm nay chưa
     */
    public boolean hasCheckedInToday(Integer userId) {
        LocalDate today = LocalDate.now();
        return dailyGoalRepository.findByUser_UserIdAndGoalDate(userId, today).isPresent();
    }
    
    /**
     * Điểm danh hôm nay
     * @return EXP earned (50 EXP cho mỗi lần điểm danh)
     */
    public DailyGoal checkInToday(Integer userId) {
        LocalDate today = LocalDate.now();
        
        // Kiểm tra đã điểm danh chưa
        if (hasCheckedInToday(userId)) {
            throw new RuntimeException("Bạn đã điểm danh hôm nay rồi!");
        }
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Tạo daily goal mới
        int expReward = 50; // 50 EXP cho mỗi lần điểm danh
        
        DailyGoal goal = DailyGoal.builder()
            .user(user)
            .goalDate(today)
            .checkedInAt(LocalDateTime.now())
            .isCompleted(true)
            .expEarned(expReward)
            .build();
        
        goal = dailyGoalRepository.save(goal);
        
        // Cập nhật streak và EXP cho user
        updateUserAfterCheckIn(user);
        
        return goal;
    }
    
    /**
     * Cập nhật user sau khi điểm danh
     */
    private void updateUserAfterCheckIn(User user) {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        
        // Kiểm tra xem hôm qua có điểm danh không
        boolean yesterdayCheckedIn = dailyGoalRepository
            .findByUser_UserIdAndGoalDate(user.getUserId(), yesterday)
            .isPresent();
        
        if (yesterdayCheckedIn) {
            // Tăng streak
            user.setStreakDays(user.getStreakDays() + 1);
        } else {
            // Reset streak về 1
            user.setStreakDays(1);
        }
        
        // Thêm EXP (50 base + bonus theo streak)
        int baseExp = 50;
        int streakBonus = Math.min(user.getStreakDays() * 5, 100); // Tối đa +100 EXP
        user.setTotalExp(user.getTotalExp() + baseExp + streakBonus);
        
        // Cập nhật last login
        user.setLastLogin(LocalDateTime.now());
        
        userRepository.save(user);
    }
    
    /**
     * Lấy lịch sử điểm danh
     */
    public List<DailyGoal> getCheckInHistory(Integer userId) {
        return dailyGoalRepository.findByUser_UserIdOrderByGoalDateDesc(userId);
    }
    
    /**
     * Lấy thông tin điểm danh hôm nay (nếu có)
     */
    public DailyGoal getTodayCheckIn(Integer userId) {
        LocalDate today = LocalDate.now();
        return dailyGoalRepository.findByUser_UserIdAndGoalDate(userId, today).orElse(null);
    }
}
