package com.piano.learn.PianoLearn.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRankingInfo {
    
    @JsonProperty("userId")
    private Integer userId;
    
    @JsonProperty("fullName")
    private String fullName;
    
    @JsonProperty("email")
    private String email;
    
    @JsonProperty("avatarUrl")
    private String avatarUrl;
    
    @JsonProperty("levelName")
    private String levelName;
    
    @JsonProperty("totalExp")
    private Integer totalExp;
    
    @JsonProperty("streakDays")
    private Integer streakDays;
    
    @JsonProperty("totalLessonsCompleted")
    private Integer totalLessonsCompleted;
    
    @JsonProperty("totalAchievementsUnlocked")
    private Integer totalAchievementsUnlocked;
    
    @JsonProperty("totalLearningTimeMinutes")
    private Integer totalLearningTimeMinutes;
    
    @JsonProperty("ranking")
    private Integer ranking;
}
