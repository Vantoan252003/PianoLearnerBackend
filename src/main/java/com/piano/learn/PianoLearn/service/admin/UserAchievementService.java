package com.piano.learn.PianoLearn.service.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.piano.learn.PianoLearn.entity.achievement.UserAchievement;
import com.piano.learn.PianoLearn.repository.achievement.UserAchievementRepository;

@Service
public class UserAchievementService {
    
    @Autowired
    private UserAchievementRepository userAchievementRepository;
    
    public List<UserAchievement> getAllUserAchievements() {
        return userAchievementRepository.findAll();
    }
    
    public UserAchievement getUserAchievementById(Integer id) {
        return userAchievementRepository.findById(id).orElse(null);
    }
    
    public UserAchievement createUserAchievement(UserAchievement userAchievement) {
        return userAchievementRepository.save(userAchievement);
    }
    
    public UserAchievement updateUserAchievement(Integer id, UserAchievement userAchievement) {
        if (userAchievementRepository.existsById(id)) {
            userAchievement.setUserAchievementId(id);
            return userAchievementRepository.save(userAchievement);
        }
        return null;
    }
    
    public boolean deleteUserAchievement(Integer id) {
        if (userAchievementRepository.existsById(id)) {
            userAchievementRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
