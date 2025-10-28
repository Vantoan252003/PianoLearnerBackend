package com.piano.learn.PianoLearn.repository.goal;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.piano.learn.PianoLearn.entity.goal.DailyGoal;

@Repository
public interface DailyGoalRepository extends JpaRepository<DailyGoal, Integer> {
    List<DailyGoal> findByUser_UserId(Integer userId);
}
