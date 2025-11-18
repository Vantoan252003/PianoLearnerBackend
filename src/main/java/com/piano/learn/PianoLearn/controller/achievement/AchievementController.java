package com.piano.learn.PianoLearn.controller.achievement;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.piano.learn.PianoLearn.entity.achievement.Achievement;
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
    public ResponseEntity<String> checkAchievements() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Integer userId = userService.findUserByEmail(email).getUserId();
        achievementUnlockService.checkAndUnlockAchievements(userId);
        return ResponseEntity.ok("Achievements checked and unlocked if any");
    }
}