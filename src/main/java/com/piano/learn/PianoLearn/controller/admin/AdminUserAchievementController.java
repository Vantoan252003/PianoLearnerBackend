package com.piano.learn.PianoLearn.controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.piano.learn.PianoLearn.entity.achievement.UserAchievement;
import com.piano.learn.PianoLearn.service.admin.UserAchievementService;

@RestController
@RequestMapping("/api/admin/user-achievements")
public class AdminUserAchievementController {
    
    @Autowired
    private UserAchievementService userAchievementService;
    
    @GetMapping
    public ResponseEntity<List<UserAchievement>> getAllUserAchievements() {
        return ResponseEntity.ok(userAchievementService.getAllUserAchievements());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UserAchievement> getUserAchievementById(@PathVariable Integer id) {
        UserAchievement userAchievement = userAchievementService.getUserAchievementById(id);
        return userAchievement != null ? ResponseEntity.ok(userAchievement) : ResponseEntity.notFound().build();
    }
    
    @PostMapping
    public ResponseEntity<UserAchievement> createUserAchievement(@RequestBody UserAchievement userAchievement) {
        UserAchievement saved = userAchievementService.createUserAchievement(userAchievement);
        return ResponseEntity.ok(saved);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<UserAchievement> updateUserAchievement(@PathVariable Integer id, @RequestBody UserAchievement userAchievement) {
        UserAchievement updated = userAchievementService.updateUserAchievement(id, userAchievement);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserAchievement(@PathVariable Integer id) {
        boolean deleted = userAchievementService.deleteUserAchievement(id);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}
