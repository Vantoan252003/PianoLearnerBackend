package com.piano.learn.PianoLearn.dto.progress;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class UserProgressResponse {
    private Integer progressId;
    private Integer userId;
    private Integer lessonId;
    private Boolean isCompleted;
    private Integer completionPercentage;
    private Integer timeSpentMinutes;
    private LocalDateTime lastAccessed;
    private LocalDateTime completedAt;
}