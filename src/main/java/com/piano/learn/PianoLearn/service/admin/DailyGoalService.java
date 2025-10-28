package com.piano.learn.PianoLearn.service.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.piano.learn.PianoLearn.entity.goal.DailyGoal;
import com.piano.learn.PianoLearn.repository.goal.DailyGoalRepository;

@Service
public class DailyGoalService {
    
    @Autowired
    private DailyGoalRepository dailyGoalRepository;
    
    public List<DailyGoal> getAllDailyGoals() {
        return dailyGoalRepository.findAll();
    }
    
    public DailyGoal getDailyGoalById(Integer id) {
        return dailyGoalRepository.findById(id).orElse(null);
    }
    
    public DailyGoal createDailyGoal(DailyGoal dailyGoal) {
        return dailyGoalRepository.save(dailyGoal);
    }
    
    public DailyGoal updateDailyGoal(Integer id, DailyGoal dailyGoal) {
        if (dailyGoalRepository.existsById(id)) {
            dailyGoal.setGoalId(id);
            return dailyGoalRepository.save(dailyGoal);
        }
        return null;
    }
    
    public boolean deleteDailyGoal(Integer id) {
        if (dailyGoalRepository.existsById(id)) {
            dailyGoalRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
