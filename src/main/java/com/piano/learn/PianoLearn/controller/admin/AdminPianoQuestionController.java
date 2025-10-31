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

import com.piano.learn.PianoLearn.dto.piano_question.PianoQuestionRequest;
import com.piano.learn.PianoLearn.entity.lesson.Lesson;
import com.piano.learn.PianoLearn.entity.piano_question.PianoQuestion;
import com.piano.learn.PianoLearn.repository.lesson.LessonRepository;
import com.piano.learn.PianoLearn.service.piano_question_service.PianoQuestionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/piano-questions")
@RequiredArgsConstructor
public class AdminPianoQuestionController {
    private final PianoQuestionService pianoQuestionService;
    private final LessonRepository lessonRepository;

    @GetMapping
    public List<PianoQuestion> getAll() {
        return pianoQuestionService.findAll();
    }

    @GetMapping("/{id}")
    public PianoQuestion getById(@PathVariable Long id) {
        return pianoQuestionService.findById(id);
    }

    @PostMapping
    public PianoQuestion create(@Valid @RequestBody PianoQuestionRequest req) {
        Lesson lesson = lessonRepository.findById(req.getLessonId().intValue()).orElse(null);
        if (lesson == null) {
            throw new IllegalArgumentException("Lesson with id " + req.getLessonId() + " not found");
        }
        PianoQuestion q = PianoQuestion.builder()
            .lesson(lesson)
            .midiNumbers(toJsonArray(req.getMidiNumbers()))
            .chord(toJsonArrayOfArrays(req.getChord()))
            .questionCount(req.getQuestionCount())
            .difficulty(req.getDifficulty())
            .build();
        return pianoQuestionService.save(q);
    }

    @PutMapping("/{id}")
    public PianoQuestion update(@PathVariable Long id, @RequestBody PianoQuestionRequest req) {
        PianoQuestion existing = pianoQuestionService.findById(id);
        if (existing == null) return null;

        existing.setMidiNumbers(toJsonArray(req.getMidiNumbers()));
        existing.setChord(toJsonArrayOfArrays(req.getChord()));
        existing.setQuestionCount(req.getQuestionCount());
        existing.setDifficulty(req.getDifficulty());
        if (req.getLessonId() != null) {
            Lesson lesson = lessonRepository.findById(req.getLessonId().intValue()).orElse(null);
            existing.setLesson(lesson);
        }
        return pianoQuestionService.save(existing);
    }

    private String toJsonArray(java.util.List<Integer> nums) {
        if (nums == null) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < nums.size(); i++) {
            if (i > 0) sb.append(',');
            sb.append(nums.get(i));
        }
        sb.append(']');
        return sb.toString();
    }

    private String toJsonArrayOfArrays(java.util.List<java.util.List<Integer>> chords) {
        if (chords == null) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < chords.size(); i++) {
            if (i > 0) sb.append(',');
            sb.append('[');
            java.util.List<Integer> inner = chords.get(i);
            for (int j = 0; j < inner.size(); j++) {
                if (j > 0) sb.append(',');
                sb.append(inner.get(j));
            }
            sb.append(']');
        }
        sb.append(']');
        return sb.toString();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        pianoQuestionService.delete(id);
    }
}
