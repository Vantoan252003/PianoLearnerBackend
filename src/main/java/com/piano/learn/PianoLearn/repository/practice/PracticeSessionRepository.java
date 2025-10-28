package com.piano.learn.PianoLearn.repository.practice;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.piano.learn.PianoLearn.entity.practice.PracticeSession;

@Repository
public interface PracticeSessionRepository extends JpaRepository<PracticeSession, Integer> {
    List<PracticeSession> findByUser_UserId(Integer userId);
}
