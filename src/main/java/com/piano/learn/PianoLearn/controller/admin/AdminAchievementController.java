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

import com.piano.learn.PianoLearn.entity.achievement.Achievement;
import com.piano.learn.PianoLearn.service.admin.AchievementService;

@RestController
@RequestMapping("/api/admin/achievements")
public class AdminAchievementController {
    
    @Autowired
    private AchievementService achievementService;
    
    @GetMapping
    public ResponseEntity<List<Achievement>> getAllAchievements() {
        return ResponseEntity.ok(achievementService.getAllAchievements());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Achievement> getAchievementById(@PathVariable Integer id) {
        Achievement achievement = achievementService.getAchievementById(id);
        return achievement != null ? ResponseEntity.ok(achievement) : ResponseEntity.notFound().build();
    }
    
    @PostMapping
    public ResponseEntity<Achievement> createAchievement(@RequestBody Achievement achievement) {
        Achievement saved = achievementService.createAchievement(achievement);
        return ResponseEntity.ok(saved);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Achievement> updateAchievement(@PathVariable Integer id, @RequestBody Achievement achievement) {
        Achievement updated = achievementService.updateAchievement(id, achievement);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAchievement(@PathVariable Integer id) {
        boolean deleted = achievementService.deleteAchievement(id);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}
