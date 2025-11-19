package com.piano.learn.PianoLearn.controller.lessson;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.piano.learn.PianoLearn.dto.lesson.LessonWithUnlockStatusDTO;
import com.piano.learn.PianoLearn.service.UserService;
import com.piano.learn.PianoLearn.service.lessons.LessonService;

@RestController
@RequestMapping("/api/auth/course")
public class LessonController {

    private final LessonService lessonService;
    private final UserService userService;

    public LessonController(LessonService lessonService, UserService userService) {
        this.lessonService = lessonService;
        this.userService = userService;
    }
    @GetMapping("/lesson/{courseId}/with-status")
    public ResponseEntity<List<LessonWithUnlockStatusDTO>> getLessonsWithUnlockStatus(@PathVariable Integer courseId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Integer userId = userService.findUserByEmail(email).getUserId();
        
        List<LessonWithUnlockStatusDTO> lessons = lessonService.getLessonsWithUnlockStatus(userId, courseId);
        return ResponseEntity.ok(lessons);
    }
}
