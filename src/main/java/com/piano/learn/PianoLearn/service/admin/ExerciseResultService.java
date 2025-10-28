package com.piano.learn.PianoLearn.service.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.piano.learn.PianoLearn.entity.exercise.ExerciseResult;
import com.piano.learn.PianoLearn.repository.exercise.ExerciseResultRepository;

@Service
public class ExerciseResultService {
    
    @Autowired
    private ExerciseResultRepository exerciseResultRepository;
    
    public List<ExerciseResult> getAllExerciseResults() {
        return exerciseResultRepository.findAll();
    }
    
    public ExerciseResult getExerciseResultById(Integer id) {
        return exerciseResultRepository.findById(id).orElse(null);
    }
    
    public ExerciseResult createExerciseResult(ExerciseResult exerciseResult) {
        return exerciseResultRepository.save(exerciseResult);
    }
    
    public ExerciseResult updateExerciseResult(Integer id, ExerciseResult exerciseResult) {
        if (exerciseResultRepository.existsById(id)) {
            exerciseResult.setResultId(id);
            return exerciseResultRepository.save(exerciseResult);
        }
        return null;
    }
    
    public boolean deleteExerciseResult(Integer id) {
        if (exerciseResultRepository.existsById(id)) {
            exerciseResultRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
