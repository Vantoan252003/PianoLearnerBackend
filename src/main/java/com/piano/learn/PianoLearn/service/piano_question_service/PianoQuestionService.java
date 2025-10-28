package com.piano.learn.PianoLearn.service.piano_question_service;


import java.util.List;

import org.springframework.stereotype.Service;

import com.piano.learn.PianoLearn.entity.piano_question.PianoQuestion;
import com.piano.learn.PianoLearn.repository.piano_question.PianoQuestionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PianoQuestionService {
    private final PianoQuestionRepository pianoQuestionRepository;

    public List<PianoQuestion> findAll() {
        return pianoQuestionRepository.findAll();
    }

    public PianoQuestion findById(Long id) {
        return pianoQuestionRepository.findById(id).orElse(null);
    }

    public PianoQuestion save(PianoQuestion question) {
        return pianoQuestionRepository.save(question);
    }

    public void delete(Long id) {
        pianoQuestionRepository.deleteById(id);
    }
}
