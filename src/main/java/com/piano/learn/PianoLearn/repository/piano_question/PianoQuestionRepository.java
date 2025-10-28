package com.piano.learn.PianoLearn.repository.piano_question;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.piano.learn.PianoLearn.entity.piano_question.PianoQuestion;

public interface PianoQuestionRepository extends JpaRepository<PianoQuestion, Long> {

    @Query(value = "SELECT * FROM piano_questions ORDER BY RAND() LIMIT 1", nativeQuery = true)
    PianoQuestion findRandom();

    List<PianoQuestion> findByDifficulty(String difficulty);
}
