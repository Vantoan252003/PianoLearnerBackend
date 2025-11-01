package com.piano.learn.PianoLearn.dto.auth;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDetailResponse {
    // Basic Info
    @JsonProperty("userId")
    private Integer userId;

    @JsonProperty("fullName")
    private String fullName;

    @JsonProperty("email")
    private String email;

    @JsonProperty("avatarUrl")
    private String avatarUrl;

    @JsonProperty("role")
    private String role;

    // Level & Experience
    @JsonProperty("levelName")
    private String levelName;

    @JsonProperty("totalExp")
    private Integer totalExp;

    @JsonProperty("streakDays")
    private Integer streakDays;

    // Learning Statistics
    @JsonProperty("totalLessonsCompleted")
    private Integer totalLessonsCompleted;

    @JsonProperty("totalLessonsTaken")
    private Integer totalLessonsTaken;

    @JsonProperty("totalLearningTimeMinutes")
    private Integer totalLearningTimeMinutes;

    @JsonProperty("averageCompletionPercentage")
    private Double averageCompletionPercentage;

    // Achievement Statistics
    @JsonProperty("totalAchievementsUnlocked")
    private Integer totalAchievementsUnlocked;

    @JsonProperty("totalAchievementExpGain")
    private Integer totalAchievementExpGain;

    @JsonProperty("achievements")
    private List<UserAchievementInfo> achievements;

    // Timestamps
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @JsonProperty("lastLogin")
    private LocalDateTime lastLogin;

    @JsonProperty("lastPracticeDate")
    private LocalDateTime lastPracticeDate;

    // Nested DTO for achievements
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserAchievementInfo {
        @JsonProperty("userAchievementId")
        private Integer userAchievementId;

        @JsonProperty("achievementId")
        private Integer achievementId;

        @JsonProperty("achievementName")
        private String achievementName;

        @JsonProperty("description")
        private String description;

        @JsonProperty("iconUrl")
        private String iconUrl;

        @JsonProperty("requirementType")
        private String requirementType;

        @JsonProperty("requirementValue")
        private Integer requirementValue;

        @JsonProperty("expReward")
        private Integer expReward;

        @JsonProperty("unlockedAt")
        private LocalDateTime unlockedAt;
    }
}
