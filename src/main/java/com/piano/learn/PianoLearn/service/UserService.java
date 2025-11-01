package com.piano.learn.PianoLearn.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.piano.learn.PianoLearn.dto.auth.RegisterRequest;
import com.piano.learn.PianoLearn.dto.auth.UserDetailResponse;
import com.piano.learn.PianoLearn.entity.achievement.UserAchievement;
import com.piano.learn.PianoLearn.entity.auth.User;
import com.piano.learn.PianoLearn.entity.progress.UserProgress;
import com.piano.learn.PianoLearn.repository.achievement.UserAchievementRepository;
import com.piano.learn.PianoLearn.repository.auth.UserRepository;
import com.piano.learn.PianoLearn.repository.progress.UserProgressRepository;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserProgressRepository userProgressRepository;
    
    @Autowired
    private UserAchievementRepository userAchievementRepository;
    public User registerUser (RegisterRequest registerRequest, PasswordEncoder encoder ){
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()){
            throw  new RuntimeException("Username has existed");
        }
        User user = new User();
        user.setFullName(registerRequest.getFullName());
        user.setEmail(registerRequest.getEmail());
        user.setPasswordHash(encoder.encode(registerRequest.getPassword()));
        user.setLevelName(registerRequest.getLevelName());
        user.setAvatarUrl(null);         
        user.setTotalExp(0);
        user.setStreakDays(0);
        user.setCreatedAt(java.time.LocalDateTime.now());
        user.setLastLogin(null);
        return userRepository.save(user);
    }
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User u = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email not found"));

        return org.springframework.security.core.userdetails.User
                .withUsername(u.getEmail())
                .password(u.getPasswordHash())
                .authorities(u.getRole().name())  // Use actual role from database
                .build();
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email not found"));
    }

    public boolean checkEmailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public UserDetailResponse getUserDetailInfo(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Get all user progress records
        List<UserProgress> progressList = userProgressRepository.findByUser_UserId(userId);

        // Calculate learning statistics
        int totalLessonsCompleted = (int) progressList.stream()
                .filter(UserProgress::getIsCompleted)
                .count();

        int totalLearningTimeMinutes = 0;
        for (UserProgress progress : progressList) {
            Integer timeSpent = progress.getTimeSpentMinutes();
            totalLearningTimeMinutes += (timeSpent == null ? 0 : timeSpent);
        }

        double averageCompletionPercentage = 0;
        if (!progressList.isEmpty()) {
            int totalCompletionPercentage = 0;
            for (UserProgress progress : progressList) {
                Integer completionPercentage = progress.getCompletionPercentage();
                totalCompletionPercentage += (completionPercentage == null ? 0 : completionPercentage);
            }
            averageCompletionPercentage = (double) totalCompletionPercentage / progressList.size();
        }

        // Get all user achievements
        List<UserAchievement> userAchievements = userAchievementRepository.findByUser_UserId(userId);

        int totalAchievementExpGain = 0;
        for (UserAchievement ua : userAchievements) {
            Integer expReward = ua.getAchievement().getExpReward();
            totalAchievementExpGain += (expReward == null ? 0 : expReward);
        }

        // Convert achievements to DTO
        List<UserDetailResponse.UserAchievementInfo> achievementList = userAchievements.stream()
                .map(ua -> UserDetailResponse.UserAchievementInfo.builder()
                        .userAchievementId(ua.getUserAchievementId())
                        .achievementId(ua.getAchievement().getAchievementId())
                        .achievementName(ua.getAchievement().getAchievementName())
                        .description(ua.getAchievement().getDescription())
                        .iconUrl(ua.getAchievement().getIconUrl())
                        .requirementType(ua.getAchievement().getRequirementType())
                        .requirementValue(ua.getAchievement().getRequirementValue())
                        .expReward(ua.getAchievement().getExpReward())
                        .unlockedAt(ua.getUnlockedAt())
                        .build())
                .collect(Collectors.toList());

        return UserDetailResponse.builder()
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole().name())
                .levelName(user.getLevelName())
                .totalExp(user.getTotalExp())
                .streakDays(user.getStreakDays())
                .totalLessonsCompleted(totalLessonsCompleted)
                .totalLessonsTaken(progressList.size())
                .totalLearningTimeMinutes(totalLearningTimeMinutes)
                .averageCompletionPercentage(averageCompletionPercentage)
                .totalAchievementsUnlocked(userAchievements.size())
                .totalAchievementExpGain(totalAchievementExpGain)
                .achievements(achievementList)
                .createdAt(user.getCreatedAt())
                .lastLogin(user.getLastLogin())
                .lastPracticeDate(user.getLastPraceticeDate())
                .build();
    }

}
