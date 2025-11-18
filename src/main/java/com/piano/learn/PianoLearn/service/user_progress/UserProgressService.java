package com.piano.learn.PianoLearn.service.user_progress;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.piano.learn.PianoLearn.dto.progress.UpdateUserProgressRequest;
import com.piano.learn.PianoLearn.dto.progress.UserProgressResponse;
import com.piano.learn.PianoLearn.entity.auth.User;
import com.piano.learn.PianoLearn.entity.lesson.Lesson;
import com.piano.learn.PianoLearn.entity.progress.UserProgress;
import com.piano.learn.PianoLearn.repository.progress.UserProgressRepository;
import com.piano.learn.PianoLearn.service.achievement.AchievementUnlockService;

@Service("learnerUserProgressService")
public class UserProgressService {

    @Autowired
    private UserProgressRepository userProgressRepository;

    @Autowired
    private AchievementUnlockService achievementUnlockService;

    public UserProgressResponse saveOrUpdateProgress(Integer userId, UpdateUserProgressRequest request) {
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
        UserProgress savedProgress = userProgressRepository.save(progress);

        // Check for achievements if lesson completed
        if (savedProgress.getIsCompleted()) {
            achievementUnlockService.checkAndUnlockAchievements(userId);
        }

        UserProgressResponse response = new UserProgressResponse();
        response.setProgressId(savedProgress.getProgressId());
        response.setUserId(savedProgress.getUser().getUserId());
        response.setLessonId(savedProgress.getLesson().getLessonId());
        response.setIsCompleted(savedProgress.getIsCompleted());
        response.setCompletionPercentage(savedProgress.getCompletionPercentage());
        response.setTimeSpentMinutes(savedProgress.getTimeSpentMinutes());
        response.setLastAccessed(savedProgress.getLastAccessed());
        response.setCompletedAt(savedProgress.getCompletedAt());

        return response;
    }

    public List<UserProgressResponse> getUserProgress(Integer userId) {
        List<UserProgress> progresses = userProgressRepository.findByUser_UserId(userId);
        return progresses.stream().map(p -> {
            UserProgressResponse response = new UserProgressResponse();
            response.setProgressId(p.getProgressId());
            response.setUserId(p.getUser().getUserId());
            response.setLessonId(p.getLesson().getLessonId());
            response.setIsCompleted(p.getIsCompleted());
            response.setCompletionPercentage(p.getCompletionPercentage());
            response.setTimeSpentMinutes(p.getTimeSpentMinutes());
            response.setLastAccessed(p.getLastAccessed());
            response.setCompletedAt(p.getCompletedAt());
            return response;
        }).toList();
    }

    public Optional<UserProgressResponse> getUserProgressForLesson(Integer userId, Integer lessonId) {
        Optional<UserProgress> progress = userProgressRepository.findByUser_UserIdAndLesson_LessonId(userId, lessonId);
        return progress.map(p -> {
            UserProgressResponse response = new UserProgressResponse();
            response.setProgressId(p.getProgressId());
            response.setUserId(p.getUser().getUserId());
            response.setLessonId(p.getLesson().getLessonId());
            response.setIsCompleted(p.getIsCompleted());
            response.setCompletionPercentage(p.getCompletionPercentage());
            response.setTimeSpentMinutes(p.getTimeSpentMinutes());
            response.setLastAccessed(p.getLastAccessed());
            response.setCompletedAt(p.getCompletedAt());
            return response;
        });
    }
}
