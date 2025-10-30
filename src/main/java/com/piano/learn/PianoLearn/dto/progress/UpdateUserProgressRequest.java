package com.piano.learn.PianoLearn.dto.progress;

import lombok.Data;

@Data
public class UpdateUserProgressRequest {
    private Integer lessonId;
    private Integer completionPercentage;
    private Integer timeSpentMinutes;
    private Boolean isCompleted;
}