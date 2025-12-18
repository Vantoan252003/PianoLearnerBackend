package com.piano.learn.PianoLearn.repository.lesson;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.piano.learn.PianoLearn.entity.lesson.LessonGuide;

@Repository
public interface LessonGuideRepository extends JpaRepository<LessonGuide, Integer> {
    Optional<LessonGuide> findByLesson_LessonId(Integer lessonId);
}
