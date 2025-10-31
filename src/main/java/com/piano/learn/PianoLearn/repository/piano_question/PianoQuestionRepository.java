package com.piano.learn.PianoLearn.repository.piano_question;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.piano.learn.PianoLearn.entity.piano_question.PianoQuestion;

public interface PianoQuestionRepository extends JpaRepository<PianoQuestion, Long> {

    @Query(value = "SELECT * FROM piano_questions ORDER BY RAND() LIMIT 1", nativeQuery = true)
    PianoQuestion findRandom();
    
    @Query("SELECT p FROM PianoQuestion p LEFT JOIN FETCH p.lesson WHERE p.questionId = :id")
    Optional<PianoQuestion> findByIdWithLesson(@Param("id") Long id);
    
    List<PianoQuestion> findByLesson_LessonId (Integer lessonId);
    List<PianoQuestion> findByDifficulty(String difficulty);
}
