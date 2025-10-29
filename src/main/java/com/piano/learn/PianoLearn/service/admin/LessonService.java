package com.piano.learn.PianoLearn.service.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.piano.learn.PianoLearn.entity.lesson.Lesson;
import com.piano.learn.PianoLearn.repository.lesson.LessonRepository;

@Service("adminLessonService")
public class LessonService {
    
    @Autowired
    private LessonRepository lessonRepository;
    
    public List<Lesson> getAllLessons() {
        return lessonRepository.findAll();
    }
    
    public Lesson getLessonById(Integer id) {
        return lessonRepository.findById(id).orElse(null);
    }
    
    public Lesson createLesson(Lesson lesson) {
        return lessonRepository.save(lesson);
    }
    
    public Lesson updateLesson(Integer id, Lesson lesson) {
        if (lessonRepository.existsById(id)) {
            lesson.setLessonId(id);
            return lessonRepository.save(lesson);
        }
        return null;
    }
    
    public boolean deleteLesson(Integer id) {
        if (lessonRepository.existsById(id)) {
            lessonRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
