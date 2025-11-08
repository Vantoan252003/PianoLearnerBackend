package com.piano.learn.PianoLearn.dto.piano_question;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PianoQuestionRequest {
    @NotNull
    private Long lessonId;
    private List<Integer> midiNumbers;
    private List<List<Integer>> chord;
    private Integer questionCount;
    private String difficulty;
}







