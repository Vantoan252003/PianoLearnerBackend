package com.piano.learn.PianoLearn.controller.achievement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.piano.learn.PianoLearn.dto.auth.UserDetailResponse;
import com.piano.learn.PianoLearn.entity.auth.User;
import com.piano.learn.PianoLearn.service.UserService;

@RestController
@RequestMapping("/api/auth/user-achievements")
public class UserAchievementController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getUserAchievements() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "User not authenticated");
                return ResponseEntity.status(401).body(error);
            }

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.findUserByEmail(userDetails.getUsername());

            List<UserDetailResponse.UserAchievementInfo> achievements = userService.getUserAchievements(user.getUserId());

            return ResponseEntity.ok(achievements);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
}
