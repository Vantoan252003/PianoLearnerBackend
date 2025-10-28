package com.piano.learn.PianoLearn.repository.exercise;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.piano.learn.PianoLearn.entity.exercise.Exercise;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Integer> {
    List<Exercise> findByLesson_LessonId(Integer lessonId);
}
