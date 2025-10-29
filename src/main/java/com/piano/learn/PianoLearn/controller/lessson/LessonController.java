package com.piano.learn.PianoLearn.controller.lessson;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.piano.learn.PianoLearn.dto.lesson.LessonDto;
import com.piano.learn.PianoLearn.service.lessons.LessonService;

@RestController
@RequestMapping("/api/auth/course")
public class LessonController {

    private final LessonService lessonService;

    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @GetMapping("/lesson/{courseId}")
    public ResponseEntity<List<LessonDto>> getLessonsByCourseId(@PathVariable Integer courseId) {
        List<LessonDto> lessons = lessonService.getLessonDtosByCourseId(courseId);
        return ResponseEntity.ok(lessons);
    }
}
