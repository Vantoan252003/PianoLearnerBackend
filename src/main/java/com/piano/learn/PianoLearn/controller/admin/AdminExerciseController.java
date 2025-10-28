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

import com.piano.learn.PianoLearn.entity.exercise.Exercise;
import com.piano.learn.PianoLearn.service.admin.ExerciseService;

@RestController
@RequestMapping("/api/admin/exercises")
public class AdminExerciseController {
    
    @Autowired
    private ExerciseService exerciseService;
    
    @GetMapping
    public ResponseEntity<List<Exercise>> getAllExercises() {
        return ResponseEntity.ok(exerciseService.getAllExercises());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Exercise> getExerciseById(@PathVariable Integer id) {
        Exercise exercise = exerciseService.getExerciseById(id);
        return exercise != null ? ResponseEntity.ok(exercise) : ResponseEntity.notFound().build();
    }
    
    @PostMapping
    public ResponseEntity<Exercise> createExercise(@RequestBody Exercise exercise) {
        Exercise saved = exerciseService.createExercise(exercise);
        return ResponseEntity.ok(saved);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Exercise> updateExercise(@PathVariable Integer id, @RequestBody Exercise exercise) {
        Exercise updated = exerciseService.updateExercise(id, exercise);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExercise(@PathVariable Integer id) {
        boolean deleted = exerciseService.deleteExercise(id);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}
