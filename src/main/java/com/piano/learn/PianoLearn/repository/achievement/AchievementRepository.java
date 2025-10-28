package com.piano.learn.PianoLearn.repository.achievement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.piano.learn.PianoLearn.entity.achievement.Achievement;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Integer> {
}
