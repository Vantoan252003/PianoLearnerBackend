package com.piano.learn.PianoLearn.service.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.piano.learn.PianoLearn.entity.achievement.Achievement;
import com.piano.learn.PianoLearn.repository.achievement.AchievementRepository;

@Service
public class AchievementService {
    
    @Autowired
    private AchievementRepository achievementRepository;
    
    public List<Achievement> getAllAchievements() {
        return achievementRepository.findAll();
    }
    
    public Achievement getAchievementById(Integer id) {
        return achievementRepository.findById(id).orElse(null);
    }
    
    public Achievement createAchievement(Achievement achievement) {
        return achievementRepository.save(achievement);
    }
    
    public Achievement updateAchievement(Integer id, Achievement achievement) {
        if (achievementRepository.existsById(id)) {
            achievement.setAchievementId(id);
            return achievementRepository.save(achievement);
        }
        return null;
    }
    
    public boolean deleteAchievement(Integer id) {
        if (achievementRepository.existsById(id)) {
            achievementRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
