package com.piano.learn.PianoLearn.dto.goal;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyGoalResponse {
    private Integer goalId;
    private LocalDate goalDate;
    private Integer targetMinutes;
    private Integer completedMinutes;
    private Integer remainingMinutes;
    private Double completionPercentage;
    private Boolean isCompleted;
    private String message;
    private Integer streakDays;
}
