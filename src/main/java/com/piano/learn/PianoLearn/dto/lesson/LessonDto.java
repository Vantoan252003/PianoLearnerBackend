package com.piano.learn.PianoLearn.dto.lesson;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonDto {
    private Integer lessonId;
    private Integer courseId; 
    private String lessonTitle;
    private Integer lessonOrder;
    private String description;
    private String videoUrl;
    private Integer expReward;
}
