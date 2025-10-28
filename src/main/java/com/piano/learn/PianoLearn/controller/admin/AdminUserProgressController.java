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

import com.piano.learn.PianoLearn.entity.progress.UserProgress;
import com.piano.learn.PianoLearn.service.admin.UserProgressService;

@RestController
@RequestMapping("/api/admin/user-progress")
public class AdminUserProgressController {
    
    @Autowired
    private UserProgressService userProgressService;
    
    @GetMapping
    public ResponseEntity<List<UserProgress>> getAllUserProgress() {
        return ResponseEntity.ok(userProgressService.getAllUserProgress());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UserProgress> getUserProgressById(@PathVariable Integer id) {
        UserProgress userProgress = userProgressService.getUserProgressById(id);
        return userProgress != null ? ResponseEntity.ok(userProgress) : ResponseEntity.notFound().build();
    }
    
    @PostMapping
    public ResponseEntity<UserProgress> createUserProgress(@RequestBody UserProgress userProgress) {
        UserProgress saved = userProgressService.createUserProgress(userProgress);
        return ResponseEntity.ok(saved);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<UserProgress> updateUserProgress(@PathVariable Integer id, @RequestBody UserProgress userProgress) {
        UserProgress updated = userProgressService.updateUserProgress(id, userProgress);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserProgress(@PathVariable Integer id) {
        boolean deleted = userProgressService.deleteUserProgress(id);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}
