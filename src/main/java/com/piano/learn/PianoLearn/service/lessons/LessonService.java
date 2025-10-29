package com.piano.learn.PianoLearn.service.lessons;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.piano.learn.PianoLearn.dto.lesson.LessonDto;
import com.piano.learn.PianoLearn.entity.lesson.Lesson;
import com.piano.learn.PianoLearn.repository.lesson.LessonRepository;

@Service("lessonService")
public class LessonService {
    private final LessonRepository lessonRepository;
    public LessonService(LessonRepository lessonRepository){
        this.lessonRepository = lessonRepository;
    }

    public List<Lesson> getAllLessons(){
        return lessonRepository.findAll();
    }

    public List<Lesson> getLessonsByCourseId(Integer courseId) {
        return lessonRepository.findByCourse_CourseId(courseId);
    }
    public LessonDto toDto(Lesson lesson){
        if(lesson == null) return null;
        LessonDto dto = LessonDto.builder()
            .lessonId(lesson.getLessonId())
            .courseId(lesson.getCourse() != null ? lesson.getCourse().getCourseId() : null)
            .lessonTitle(lesson.getLessonTitle())
            .lessonOrder(lesson.getLessonOrder())
            .description(lesson.getDescription())
            .videoUrl(lesson.getVideoUrl())
            .expReward(lesson.getExpReward())
            .build();
        return dto;
    }

    public List<LessonDto> getLessonDtosByCourseId(Integer courseId){
        List<Lesson> lessons = getLessonsByCourseId(courseId);
        return lessons.stream().map(this::toDto).collect(Collectors.toList());
    }
}