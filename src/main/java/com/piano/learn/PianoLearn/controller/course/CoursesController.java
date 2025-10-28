package com.piano.learn.PianoLearn.controller.course;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.piano.learn.PianoLearn.entity.courses.Courses;
import com.piano.learn.PianoLearn.service.courses.CoursesService;

@RestController
@RequestMapping("/api/auth/courses")
public class CoursesController {
    private final CoursesService coursesService;
    public CoursesController (CoursesService coursesService) {
        this.coursesService = coursesService;
    }
    @GetMapping
    public ResponseEntity<List<Courses>> getCourse() {
        List<Courses> courses = coursesService.getAllCourses();
        return ResponseEntity.ok(courses);
    }
}
