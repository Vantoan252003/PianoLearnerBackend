package com.piano.learn.PianoLearn.repository.goal;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.piano.learn.PianoLearn.entity.goal.DailyGoal;

@Repository
public interface DailyGoalRepository extends JpaRepository<DailyGoal, Integer> {
    List<DailyGoal> findByUser_UserId(Integer userId);
    
    Optional<DailyGoal> findByUser_UserIdAndGoalDate(Integer userId, LocalDate goalDate);
    
    List<DailyGoal> findByUser_UserIdOrderByGoalDateDesc(Integer userId);
}
