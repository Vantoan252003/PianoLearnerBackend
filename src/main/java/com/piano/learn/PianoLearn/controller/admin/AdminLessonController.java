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

import com.piano.learn.PianoLearn.entity.lesson.Lesson;
import com.piano.learn.PianoLearn.service.admin.LessonService;

@RestController
@RequestMapping("/api/admin/lessons")
public class AdminLessonController {
    
    @Autowired
    private LessonService lessonService;
    
    @GetMapping
    public ResponseEntity<List<Lesson>> getAllLessons() {
        return ResponseEntity.ok(lessonService.getAllLessons());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Lesson> getLessonById(@PathVariable Integer id) {
        Lesson lesson = lessonService.getLessonById(id);
        return lesson != null ? ResponseEntity.ok(lesson) : ResponseEntity.notFound().build();
    }
    
    @PostMapping
    public ResponseEntity<Lesson> createLesson(@RequestBody Lesson lesson) {
        Lesson saved = lessonService.createLesson(lesson);
        return ResponseEntity.ok(saved);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Lesson> updateLesson(@PathVariable Integer id, @RequestBody Lesson lesson) {
        Lesson updated = lessonService.updateLesson(id, lesson);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLesson(@PathVariable Integer id) {
        boolean deleted = lessonService.deleteLesson(id);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}
