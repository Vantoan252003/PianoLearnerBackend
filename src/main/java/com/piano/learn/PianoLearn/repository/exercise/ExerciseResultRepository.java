package com.piano.learn.PianoLearn.repository.exercise;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.piano.learn.PianoLearn.entity.exercise.ExerciseResult;

@Repository
public interface ExerciseResultRepository extends JpaRepository<ExerciseResult, Integer> {
    List<ExerciseResult> findByUser_UserId(Integer userId);
    List<ExerciseResult> findByExercise_ExerciseId(Integer exerciseId);
}
