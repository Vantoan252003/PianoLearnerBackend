package com.piano.learn.PianoLearn.service.lessons;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.piano.learn.PianoLearn.entity.auth.User;
import com.piano.learn.PianoLearn.entity.lesson.Lesson;
import com.piano.learn.PianoLearn.repository.auth.UserRepository;
import com.piano.learn.PianoLearn.repository.lesson.LessonRepository;

@Service
public class LessonUnlockService {

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Kiểm tra xem lesson có bị khóa hay không dựa trên EXP của user
     * @param userId ID của user
     * @param lessonId ID của lesson cần kiểm tra
     * @return true nếu lesson đã mở khóa, false nếu còn khóa
     */
    public boolean isLessonUnlocked(Integer userId, Integer lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
            .orElseThrow(() -> new RuntimeException("Lesson not found"));
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Nếu lesson không yêu cầu EXP (expRequire = 0) thì luôn mở
        if (lesson.getExpRequire() == null || lesson.getExpRequire() == 0) {
            return true;
        }
        
        // Kiểm tra user có đủ EXP không
        Integer userExp = user.getTotalExp() != null ? user.getTotalExp() : 0;
        return userExp >= lesson.getExpRequire();
    }

    /**
     * Lấy thông tin về yêu cầu mở khóa của lesson
     * @param lessonId ID của lesson
     * @return Thông tin về EXP cần để mở khóa
     */
    public String getUnlockRequirement(Integer lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
            .orElseThrow(() -> new RuntimeException("Lesson not found"));
        
        if (lesson.getExpRequire() == null || lesson.getExpRequire() == 0) {
            return "No requirement - Available for everyone";
        }
        
        return "Requires " + lesson.getExpRequire() + " EXP to unlock";
    }

    /**
     * Tính EXP còn thiếu để mở khóa lesson
     * @param userId ID của user
     * @param lessonId ID của lesson
     * @return Số EXP còn thiếu (0 nếu đã đủ)
     */
    public Integer getExpNeeded(Integer userId, Integer lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
            .orElseThrow(() -> new RuntimeException("Lesson not found"));
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (lesson.getExpRequire() == null || lesson.getExpRequire() == 0) {
            return 0;
        }
        
        Integer userExp = user.getTotalExp() != null ? user.getTotalExp() : 0;
        Integer needed = lesson.getExpRequire() - userExp;
        
        return needed > 0 ? needed : 0;
    }
}
