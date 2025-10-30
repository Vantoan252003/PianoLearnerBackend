package com.piano.learn.PianoLearn.controller.user_progress;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.piano.learn.PianoLearn.dto.progress.UpdateUserProgressRequest;
import com.piano.learn.PianoLearn.entity.auth.User;
import com.piano.learn.PianoLearn.entity.progress.UserProgress;
import com.piano.learn.PianoLearn.service.UserService;
import com.piano.learn.PianoLearn.service.user_progress.UserProgressService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth/user-progress")
public class UserProgressController {

    @Autowired
    @Qualifier("learnerUserProgressService")
    private UserProgressService userProgressService;

    @Autowired
    private UserService userService;

    @PostMapping("/update")
    public ResponseEntity<UserProgress> updateProgress(@Valid @RequestBody UpdateUserProgressRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User currentUser = userService.findUserByEmail(email);
        Integer userId = currentUser.getUserId();

        UserProgress updatedProgress = userProgressService.saveOrUpdateProgress(userId, request);
        return ResponseEntity.ok(updatedProgress);
    }

    @GetMapping("/user")
    public ResponseEntity<List<UserProgress>> getUserProgress() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User currentUser = userService.findUserByEmail(email);
        Integer userId = currentUser.getUserId();

        List<UserProgress> progressList = userProgressService.getUserProgress(userId);
        return ResponseEntity.ok(progressList);
    }

    @GetMapping("/lesson/{lessonId}")
    public ResponseEntity<UserProgress> getUserProgressForLesson(@PathVariable Integer lessonId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User currentUser = userService.findUserByEmail(email);
        Integer userId = currentUser.getUserId();

        Optional<UserProgress> progress = userProgressService.getUserProgressForLesson(userId, lessonId);
        return progress.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}
