package com.piano.learn.PianoLearn.controller.achievement;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.piano.learn.PianoLearn.entity.achievement.Achievement;
import com.piano.learn.PianoLearn.service.admin.AchievementService;

@RestController
@RequestMapping("/api/auth")
public class AchievementController {

    @Autowired
    private AchievementService achievementService;

    @GetMapping("/achievements")
    public ResponseEntity<List<Achievement>> getAllAchievements() {
        return ResponseEntity.ok(achievementService.getAllAchievements());
    }
    
}