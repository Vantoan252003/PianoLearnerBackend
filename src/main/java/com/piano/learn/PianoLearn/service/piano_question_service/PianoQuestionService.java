package com.piano.learn.PianoLearn.service.piano_question_service;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.piano.learn.PianoLearn.dto.piano_question.PianoQuestionRequest;
import com.piano.learn.PianoLearn.entity.piano_question.PianoQuestion;
import com.piano.learn.PianoLearn.repository.piano_question.PianoQuestionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PianoQuestionService {
    private final PianoQuestionRepository pianoQuestionRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<PianoQuestion> findAll() {
        return pianoQuestionRepository.findAll();
    }

    public PianoQuestion findById(Long id) {
        return pianoQuestionRepository.findByIdWithLesson(id).orElse(null);
    }
     public List<PianoQuestion> getPianoQuestionsByLessonId (Integer lessonId) {
        return pianoQuestionRepository.findByLesson_LessonId(lessonId);
    }

    public List<PianoQuestionRequest> getPianoQuestionsByLessonIdAsDto(Integer lessonId) {
        List<PianoQuestion> questions = pianoQuestionRepository.findByLesson_LessonId(lessonId);
        return questions.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private PianoQuestionRequest mapToDto(PianoQuestion question) {
        PianoQuestionRequest dto = new PianoQuestionRequest();
        dto.setLessonId(question.getLesson().getLessonId().longValue());
        try {
            List<Integer> midiList = objectMapper.readValue(question.getMidiNumbers(), new TypeReference<List<Integer>>() {});
            dto.setMidiNumbers(midiList);
        } catch (Exception e) {
            dto.setMidiNumbers(List.of()); // default empty list
        }
        try {
            List<List<Integer>> chordList = objectMapper.readValue(question.getChord(), new TypeReference<List<List<Integer>>>() {});
            dto.setChord(chordList);
        } catch (Exception e) {
            dto.setChord(List.of()); // default empty list
        }
        dto.setQuestionCount(question.getQuestionCount());
        dto.setDifficulty(question.getDifficulty());
        return dto;
    }

    public PianoQuestion save(PianoQuestion question) {
        return pianoQuestionRepository.save(question);
    }

    public void delete(Long id) {
        pianoQuestionRepository.deleteById(id);
    }
}
