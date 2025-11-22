package com.piano.learn.PianoLearn.dto.goal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PracticeCompleteResponse {
    private Integer expEarned;
    private Integer totalExp;
    private Integer newLevel;
    private Boolean goalCompleted;
    private String message;
    private DailyGoalResponse todayGoal;
}
