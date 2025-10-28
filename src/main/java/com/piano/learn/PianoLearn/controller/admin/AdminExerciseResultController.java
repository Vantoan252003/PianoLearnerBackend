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

import com.piano.learn.PianoLearn.entity.exercise.ExerciseResult;
import com.piano.learn.PianoLearn.service.admin.ExerciseResultService;

@RestController
@RequestMapping("/api/admin/exercise-results")
public class AdminExerciseResultController {
    
    @Autowired
    private ExerciseResultService exerciseResultService;
    
    @GetMapping
    public ResponseEntity<List<ExerciseResult>> getAllExerciseResults() {
        return ResponseEntity.ok(exerciseResultService.getAllExerciseResults());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ExerciseResult> getExerciseResultById(@PathVariable Integer id) {
        ExerciseResult exerciseResult = exerciseResultService.getExerciseResultById(id);
        return exerciseResult != null ? ResponseEntity.ok(exerciseResult) : ResponseEntity.notFound().build();
    }
    
    @PostMapping
    public ResponseEntity<ExerciseResult> createExerciseResult(@RequestBody ExerciseResult exerciseResult) {
        ExerciseResult saved = exerciseResultService.createExerciseResult(exerciseResult);
        return ResponseEntity.ok(saved);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ExerciseResult> updateExerciseResult(@PathVariable Integer id, @RequestBody ExerciseResult exerciseResult) {
        ExerciseResult updated = exerciseResultService.updateExerciseResult(id, exerciseResult);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExerciseResult(@PathVariable Integer id) {
        boolean deleted = exerciseResultService.deleteExerciseResult(id);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}
