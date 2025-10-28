package com.piano.learn.PianoLearn.service.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.piano.learn.PianoLearn.entity.practice.PracticeSession;
import com.piano.learn.PianoLearn.repository.practice.PracticeSessionRepository;

@Service
public class PracticeSessionService {
    
    @Autowired
    private PracticeSessionRepository practiceSessionRepository;
    
    public List<PracticeSession> getAllPracticeSessions() {
        return practiceSessionRepository.findAll();
    }
    
    public PracticeSession getPracticeSessionById(Integer id) {
        return practiceSessionRepository.findById(id).orElse(null);
    }
    
    public PracticeSession createPracticeSession(PracticeSession practiceSession) {
        return practiceSessionRepository.save(practiceSession);
    }
    
    public PracticeSession updatePracticeSession(Integer id, PracticeSession practiceSession) {
        if (practiceSessionRepository.existsById(id)) {
            practiceSession.setSessionId(id);
            return practiceSessionRepository.save(practiceSession);
        }
        return null;
    }
    
    public boolean deletePracticeSession(Integer id) {
        if (practiceSessionRepository.existsById(id)) {
            practiceSessionRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
