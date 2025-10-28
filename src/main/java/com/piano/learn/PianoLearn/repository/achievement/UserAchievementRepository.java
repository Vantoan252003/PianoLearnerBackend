package com.piano.learn.PianoLearn.repository.achievement;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.piano.learn.PianoLearn.entity.achievement.UserAchievement;

@Repository
public interface UserAchievementRepository extends JpaRepository<UserAchievement, Integer> {
    List<UserAchievement> findByUser_UserId(Integer userId);
}
