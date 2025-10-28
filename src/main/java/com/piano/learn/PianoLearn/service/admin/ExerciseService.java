package com.piano.learn.PianoLearn.service.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.piano.learn.PianoLearn.entity.exercise.Exercise;
import com.piano.learn.PianoLearn.repository.exercise.ExerciseRepository;

@Service
public class ExerciseService {
    
    @Autowired
    private ExerciseRepository exerciseRepository;
    
    public List<Exercise> getAllExercises() {
        return exerciseRepository.findAll();
    }
    
    public Exercise getExerciseById(Integer id) {
        return exerciseRepository.findById(id).orElse(null);
    }
    
    public Exercise createExercise(Exercise exercise) {
        return exerciseRepository.save(exercise);
    }
    
    public Exercise updateExercise(Integer id, Exercise exercise) {
        if (exerciseRepository.existsById(id)) {
            exercise.setExerciseId(id);
            return exerciseRepository.save(exercise);
        }
        return null;
    }
    
    public boolean deleteExercise(Integer id) {
        if (exerciseRepository.existsById(id)) {
            exerciseRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
