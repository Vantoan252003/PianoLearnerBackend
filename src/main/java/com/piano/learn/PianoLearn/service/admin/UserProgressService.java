package com.piano.learn.PianoLearn.service.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.piano.learn.PianoLearn.entity.progress.UserProgress;
import com.piano.learn.PianoLearn.repository.progress.UserProgressRepository;

@Service
public class UserProgressService {
    
    @Autowired
    private UserProgressRepository userProgressRepository;
    
    public List<UserProgress> getAllUserProgress() {
        return userProgressRepository.findAllWithUserAndLesson();
    }
    
    public UserProgress getUserProgressById(Integer id) {
        return userProgressRepository.findById(id).orElse(null);
    }
    
    public UserProgress createUserProgress(UserProgress userProgress) {
        return userProgressRepository.save(userProgress);
    }
    
    public UserProgress updateUserProgress(Integer id, UserProgress userProgress) {
        if (userProgressRepository.existsById(id)) {
            userProgress.setProgressId(id);
            return userProgressRepository.save(userProgress);
        }
        return null;
    }
    
    public boolean deleteUserProgress(Integer id) {
        if (userProgressRepository.existsById(id)) {
            userProgressRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    public List<UserProgress> getUserProgressByUserId(Integer userId) {
        return userProgressRepository.findByUser_UserId(userId);
    }
}
