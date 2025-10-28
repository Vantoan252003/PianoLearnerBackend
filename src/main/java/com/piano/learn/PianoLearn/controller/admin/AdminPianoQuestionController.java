package com.piano.learn.PianoLearn.controller.admin;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.piano.learn.PianoLearn.entity.piano_question.PianoQuestion;
import com.piano.learn.PianoLearn.service.piano_question_service.PianoQuestionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/piano-questions")
@RequiredArgsConstructor
public class AdminPianoQuestionController {
    private final PianoQuestionService pianoQuestionService;

    @GetMapping
    public List<PianoQuestion> getAll() {
        return pianoQuestionService.findAll();
    }

    @GetMapping("/{id}")
    public PianoQuestion getById(@PathVariable Long id) {
        return pianoQuestionService.findById(id);
    }

    @PostMapping
    public PianoQuestion create(@RequestBody PianoQuestion question) {
        return pianoQuestionService.save(question);
    }

    @PutMapping("/{id}")
    public PianoQuestion update(@PathVariable Long id, @RequestBody PianoQuestion updated) {
        PianoQuestion existing = pianoQuestionService.findById(id);
        if (existing == null) return null;

        existing.setNoteName(updated.getNoteName());
        existing.setMidiNumber(updated.getMidiNumber());
        existing.setDifficulty(updated.getDifficulty());
        existing.setLesson(updated.getLesson());
        return pianoQuestionService.save(existing);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        pianoQuestionService.delete(id);
    }
}
