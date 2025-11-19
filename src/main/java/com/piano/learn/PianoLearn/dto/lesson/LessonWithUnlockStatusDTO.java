package com.piano.learn.PianoLearn.dto.lesson;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonWithUnlockStatusDTO {
    // Thông tin cơ bản của lesson
    private Integer lessonId;
    private Integer courseId;
    private String lessonTitle;
    private Integer lessonOrder;
    private String description;
    private String videoUrl;
    private String sheetMusicUrl;
    private Integer durationMinutes;
    private Integer expReward;
    private Integer expRequire;
    private LocalDateTime createdAt;
    
    // Thông tin về trạng thái mở khóa
    private Boolean isUnlocked;
    private Boolean isCompleted;
    private Integer completionPercentage;
    
    // Thông tin về yêu cầu mở khóa
    private Integer userCurrentExp;
    private Integer expNeeded;
    private String unlockRequirement;
}
