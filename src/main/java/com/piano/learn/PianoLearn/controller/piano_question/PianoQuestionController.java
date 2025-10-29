package com.piano.learn.PianoLearn.controller.piano_question;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.piano.learn.PianoLearn.dto.piano_question.PianoQuestionRequest;
import com.piano.learn.PianoLearn.service.piano_question_service.PianoQuestionService;

@RestController
@RequestMapping("/api/auth/piano-question")
public class PianoQuestionController {
    private final PianoQuestionService pianoQuestionService;
    public PianoQuestionController(PianoQuestionService pianoQuestionService){
        this.pianoQuestionService = pianoQuestionService;
    }

    @GetMapping("/{lessonId}")
    public ResponseEntity<List<PianoQuestionRequest>> getPianoQuestionByLessonId(@PathVariable Integer lessonId){
        List<PianoQuestionRequest> pianoRequest = pianoQuestionService.getPianoQuestionsByLessonIdAsDto(lessonId);
        return ResponseEntity.ok(pianoRequest);
    }  

}
