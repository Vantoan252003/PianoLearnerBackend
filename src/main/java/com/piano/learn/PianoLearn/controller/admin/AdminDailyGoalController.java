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

import com.piano.learn.PianoLearn.entity.goal.DailyGoal;
import com.piano.learn.PianoLearn.service.admin.DailyGoalService;

@RestController
@RequestMapping("/api/admin/daily-goals")
public class AdminDailyGoalController {
    
    @Autowired
    private DailyGoalService dailyGoalService;
    
    @GetMapping
    public ResponseEntity<List<DailyGoal>> getAllDailyGoals() {
        return ResponseEntity.ok(dailyGoalService.getAllDailyGoals());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<DailyGoal> getDailyGoalById(@PathVariable Integer id) {
        DailyGoal dailyGoal = dailyGoalService.getDailyGoalById(id);
        return dailyGoal != null ? ResponseEntity.ok(dailyGoal) : ResponseEntity.notFound().build();
    }
    
    @PostMapping
    public ResponseEntity<DailyGoal> createDailyGoal(@RequestBody DailyGoal dailyGoal) {
        DailyGoal saved = dailyGoalService.createDailyGoal(dailyGoal);
        return ResponseEntity.ok(saved);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<DailyGoal> updateDailyGoal(@PathVariable Integer id, @RequestBody DailyGoal dailyGoal) {
        DailyGoal updated = dailyGoalService.updateDailyGoal(id, dailyGoal);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDailyGoal(@PathVariable Integer id) {
        boolean deleted = dailyGoalService.deleteDailyGoal(id);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}
