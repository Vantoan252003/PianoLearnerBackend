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

import com.piano.learn.PianoLearn.entity.practice.PracticeSession;
import com.piano.learn.PianoLearn.service.admin.PracticeSessionService;

@RestController
@RequestMapping("/api/admin/practice-sessions")
public class AdminPracticeSessionController {
    
    @Autowired
    private PracticeSessionService practiceSessionService;
    
    @GetMapping
    public ResponseEntity<List<PracticeSession>> getAllPracticeSessions() {
        return ResponseEntity.ok(practiceSessionService.getAllPracticeSessions());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PracticeSession> getPracticeSessionById(@PathVariable Integer id) {
        PracticeSession practiceSession = practiceSessionService.getPracticeSessionById(id);
        return practiceSession != null ? ResponseEntity.ok(practiceSession) : ResponseEntity.notFound().build();
    }
    
    @PostMapping
    public ResponseEntity<PracticeSession> createPracticeSession(@RequestBody PracticeSession practiceSession) {
        PracticeSession saved = practiceSessionService.createPracticeSession(practiceSession);
        return ResponseEntity.ok(saved);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<PracticeSession> updatePracticeSession(@PathVariable Integer id, @RequestBody PracticeSession practiceSession) {
        PracticeSession updated = practiceSessionService.updatePracticeSession(id, practiceSession);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePracticeSession(@PathVariable Integer id) {
        boolean deleted = practiceSessionService.deletePracticeSession(id);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}
