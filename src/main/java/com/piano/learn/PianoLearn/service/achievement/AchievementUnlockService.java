package com.piano.learn.PianoLearn.service.achievement;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.piano.learn.PianoLearn.dto.auth.UserDetailResponse;
import com.piano.learn.PianoLearn.entity.achievement.Achievement;
import com.piano.learn.PianoLearn.entity.achievement.UserAchievement;
import com.piano.learn.PianoLearn.entity.auth.User;
import com.piano.learn.PianoLearn.service.UserService;
import com.piano.learn.PianoLearn.service.admin.AchievementService;
import com.piano.learn.PianoLearn.service.admin.UserAchievementService;

@Service
public class AchievementUnlockService {

    @Autowired
    private AchievementService achievementService;

    @Autowired
    private UserAchievementService userAchievementService;

    @Autowired
    private UserService userService;

    public List<UserAchievement> checkAndUnlockAchievements(Integer userId) {
        List<Achievement> allAchievements = achievementService.getAllAchievements();
        UserDetailResponse userDetail = userService.getUserDetailInfo(userId);

        List<UserAchievement> newUnlocked = new ArrayList<>();

        for (Achievement ach : allAchievements) {
            boolean alreadyUnlocked = userAchievementService.getUserAchievementsByUserId(userId)
                .stream().anyMatch(ua -> ua.getAchievement().getAchievementId().equals(ach.getAchievementId()));

            if (!alreadyUnlocked && meetsRequirement(userDetail, ach, userId)) {
                UserAchievement ua = UserAchievement.builder()
                    .user(User.builder().userId(userId).build())
                    .achievement(ach)
                    .build();
                userAchievementService.createUserAchievement(ua);
                // Cập nhật EXP cho user
                userService.addExp(userId, ach.getExpReward());
                newUnlocked.add(ua);
            }
        }
        return newUnlocked;
    }

    private boolean meetsRequirement(UserDetailResponse user, Achievement ach, Integer userId) {
        switch (ach.getRequirementType()) {
            case "lessons_completed":
                return user.getTotalLessonsCompleted() >= ach.getRequirementValue();
            case "streak_days":
                return user.getStreakDays() >= ach.getRequirementValue();
            case "practice_minutes":
                return user.getTotalLearningTimeMinutes() >= ach.getRequirementValue();
            case "course_completed":
                return isCourseCompleted(userId, ach.getRequirementValue());
            default:
                return false;
        }
    }

    private boolean isCourseCompleted(Integer userId, Integer courseId) {
        // Logic kiểm tra user đã hoàn thành tất cả lessons trong course
        // Giả sử: Lấy tất cả lessons của course, kiểm tra user_progress cho mỗi lesson
        // Nếu tất cả is_completed = true, thì course completed
        // Cần implement trong UserService hoặc repository
        return userService.isCourseCompletedByUser(userId, courseId);
    }
}