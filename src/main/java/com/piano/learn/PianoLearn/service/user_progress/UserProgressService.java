package com.piano.learn.PianoLearn.service.user_progress;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.piano.learn.PianoLearn.dto.progress.UpdateUserProgressRequest;
import com.piano.learn.PianoLearn.entity.auth.User;
import com.piano.learn.PianoLearn.entity.lesson.Lesson;
import com.piano.learn.PianoLearn.entity.progress.UserProgress;
import com.piano.learn.PianoLearn.repository.progress.UserProgressRepository;

@Service("learnerUserProgressService")
public class UserProgressService {

    @Autowired
    private UserProgressRepository userProgressRepository;

    public UserProgress saveOrUpdateProgress(Integer userId, UpdateUserProgressRequest request) {
        Optional<UserProgress> existingProgress = userProgressRepository.findByUser_UserIdAndLesson_LessonId(
            userId, request.getLessonId());

        UserProgress progress;
        if (existingProgress.isPresent()) {
            progress = existingProgress.get();
            // Cập nhật các trường
            if (request.getCompletionPercentage() != null) {
                progress.setCompletionPercentage(request.getCompletionPercentage());
            }
            if (request.getTimeSpentMinutes() != null) {
                progress.setTimeSpentMinutes(progress.getTimeSpentMinutes() + request.getTimeSpentMinutes());
            }
            if (request.getIsCompleted() != null) {
                progress.setIsCompleted(request.getIsCompleted());
                if (request.getIsCompleted()) {
                    progress.setCompletedAt(LocalDateTime.now());
                }
            }
        } else {
            // Tạo mới
            progress = UserProgress.builder()
                .user(User.builder().userId(userId).build())
                .lesson(Lesson.builder().lessonId(request.getLessonId()).build())
                .completionPercentage(request.getCompletionPercentage() != null ? request.getCompletionPercentage() : 0)
                .timeSpentMinutes(request.getTimeSpentMinutes() != null ? request.getTimeSpentMinutes() : 0)
                .isCompleted(request.getIsCompleted() != null ? request.getIsCompleted() : false)
                .build();
            if (progress.getIsCompleted()) {
                progress.setCompletedAt(LocalDateTime.now());
            }
        }

        progress.setLastAccessed(LocalDateTime.now());
        return userProgressRepository.save(progress);
    }

    public List<UserProgress> getUserProgress(Integer userId) {
        return userProgressRepository.findByUser_UserId(userId);
    }

    public Optional<UserProgress> getUserProgressForLesson(Integer userId, Integer lessonId) {
        return userProgressRepository.findByUser_UserIdAndLesson_LessonId(userId, lessonId);
    }
}
