package com.piano.learn.PianoLearn.controller.achievement;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.piano.learn.PianoLearn.dto.auth.UserDetailResponse;
import com.piano.learn.PianoLearn.entity.achievement.Achievement;
import com.piano.learn.PianoLearn.entity.achievement.UserAchievement;
import com.piano.learn.PianoLearn.service.UserService;
import com.piano.learn.PianoLearn.service.achievement.AchievementUnlockService;
import com.piano.learn.PianoLearn.service.admin.AchievementService;

@RestController
@RequestMapping("/api/auth")
public class AchievementController {

    @Autowired
    private AchievementService achievementService;

    @Autowired
    private AchievementUnlockService achievementUnlockService;

    @Autowired
    private UserService userService;

    @GetMapping("/achievements")
    public ResponseEntity<List<Achievement>> getAllAchievements() {
        return ResponseEntity.ok(achievementService.getAllAchievements());
    }

    @PostMapping("/check-achievements")
    public ResponseEntity<?> checkAchievements() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Integer userId = userService.findUserByEmail(email).getUserId();
        List<UserAchievement> newAchievements = achievementUnlockService.checkAndUnlockAchievements(userId);
        
        if (newAchievements.isEmpty()) {
            return ResponseEntity.ok(Map.of("message", "No new achievements unlocked"));
        } else {
            // Convert to DTO
            List<UserDetailResponse.UserAchievementInfo> achievementInfos = newAchievements.stream()
                .map(ua -> UserDetailResponse.UserAchievementInfo.builder()
                    .userAchievementId(ua.getUserAchievementId())
                    .achievementId(ua.getAchievement().getAchievementId())
                    .achievementName(ua.getAchievement().getAchievementName())
                    .description(ua.getAchievement().getDescription())
                    .iconUrl(ua.getAchievement().getIconUrl())
                    .requirementType(ua.getAchievement().getRequirementType())
                    .requirementValue(ua.getAchievement().getRequirementValue())
                    .expReward(ua.getAchievement().getExpReward())
                    .unlockedAt(ua.getUnlockedAt())
                    .build())
                .collect(Collectors.toList());
            return ResponseEntity.ok(Map.of("message", "New achievements unlocked", "newAchievements", achievementInfos));
        }
    }
}