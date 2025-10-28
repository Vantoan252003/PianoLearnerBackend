package com.piano.learn.PianoLearn.repository.progress;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.piano.learn.PianoLearn.entity.progress.UserProgress;

@Repository
public interface UserProgressRepository extends JpaRepository<UserProgress, Integer> {
    List<UserProgress> findByUser_UserId(Integer userId);
    Optional<UserProgress> findByUser_UserIdAndLesson_LessonId(Integer userId, Integer lessonId);
}
