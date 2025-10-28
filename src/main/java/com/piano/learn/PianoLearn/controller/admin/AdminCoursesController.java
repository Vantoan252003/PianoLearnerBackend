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

import com.piano.learn.PianoLearn.entity.courses.Courses;
import com.piano.learn.PianoLearn.repository.courses.CoursesRepository;

@RestController
@RequestMapping("/api/admin/courses")
public class AdminCoursesController {
    
    @Autowired
    private CoursesRepository coursesRepository;
    
    @GetMapping
    public ResponseEntity<List<Courses>> getAllCourses() {
        return ResponseEntity.ok(coursesRepository.findAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Courses> getCourseById(@PathVariable Integer id) {
        return coursesRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Courses> createCourse(@RequestBody Courses course) {
        Courses saved = coursesRepository.save(course);
        return ResponseEntity.ok(saved);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Courses> updateCourse(@PathVariable Integer id, @RequestBody Courses course) {
        if (!coursesRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        course.setCourseId(id);
        Courses updated = coursesRepository.save(course);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Integer id) {
        if (!coursesRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        coursesRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
