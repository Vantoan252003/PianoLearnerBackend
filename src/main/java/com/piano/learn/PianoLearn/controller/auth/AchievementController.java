package com.piano.learn.PianoLearn.controller.auth;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.piano.learn.PianoLearn.entity.achievement.UserAchievement;
import com.piano.learn.PianoLearn.entity.auth.User;
import com.piano.learn.PianoLearn.service.UserService;
import com.piano.learn.PianoLearn.service.admin.AchievementService;
import com.piano.learn.PianoLearn.service.admin.UserAchievementService;

@RestController
@RequestMapping("/api/auth")
public class AchievementController {

    @Autowired
    private UserAchievementService userAchievementService;

    @Autowired
    private AchievementService achievementService;

    @Autowired
    private UserService userService;

    @GetMapping("/achievement")
    public ResponseEntity<List<UserAchievement>> getUserAchievements() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User currentUser = userService.findUserByEmail(email);
        Integer userId = currentUser.getUserId();

        List<UserAchievement> achievements = userAchievementService.getUserAchievementsByUserId(userId);
        return ResponseEntity.ok(achievements);
    }
}