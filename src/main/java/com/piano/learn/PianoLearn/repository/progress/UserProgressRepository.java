package com.piano.learn.PianoLearn.repository.progress;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.piano.learn.PianoLearn.entity.progress.UserProgress;

@Repository
public interface UserProgressRepository extends JpaRepository<UserProgress, Integer> {
    List<UserProgress> findByUser_UserId(Integer userId);
    Optional<UserProgress> findByUser_UserIdAndLesson_LessonId(Integer userId, Integer lessonId);
    
    /**
     * Get all progress with user and lesson data loaded
     */
    @Query("SELECT up FROM UserProgress up JOIN FETCH up.user JOIN FETCH up.lesson l JOIN FETCH l.course ORDER BY up.lastAccessed DESC")
    List<UserProgress> findAllWithUserAndLesson();
}
