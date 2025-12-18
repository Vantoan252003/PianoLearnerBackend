package com.piano.learn.PianoLearn.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LessonGuideRequest {
    private Integer lessonId;
    private String content;
}
