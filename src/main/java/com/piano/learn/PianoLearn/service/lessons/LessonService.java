package com.piano.learn.PianoLearn.service.lessons;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.piano.learn.PianoLearn.dto.lesson.LessonDto;
import com.piano.learn.PianoLearn.dto.lesson.LessonWithUnlockStatusDTO;
import com.piano.learn.PianoLearn.entity.auth.User;
import com.piano.learn.PianoLearn.entity.lesson.Lesson;
import com.piano.learn.PianoLearn.entity.progress.UserProgress;
import com.piano.learn.PianoLearn.repository.auth.UserRepository;
import com.piano.learn.PianoLearn.repository.lesson.LessonRepository;
import com.piano.learn.PianoLearn.repository.progress.UserProgressRepository;

@Service("lessonService")
public class LessonService {
    private final LessonRepository lessonRepository;
    
    @Autowired
    private LessonUnlockService lessonUnlockService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserProgressRepository userProgressRepository;
    
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
    
    /**
     * Lấy danh sách lesson với thông tin unlock status cho user
     * @param userId ID của user
     * @param courseId ID của course
     * @return Danh sách lesson với thông tin mở khóa và tiến độ
     */
    public List<LessonWithUnlockStatusDTO> getLessonsWithUnlockStatus(Integer userId, Integer courseId) {
        List<Lesson> lessons = getLessonsByCourseId(courseId);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        return lessons.stream()
            .map(lesson -> {
                // Kiểm tra trạng thái mở khóa
                boolean isUnlocked = lessonUnlockService.isLessonUnlocked(userId, lesson.getLessonId());
                Integer expNeeded = lessonUnlockService.getExpNeeded(userId, lesson.getLessonId());
                
                // Lấy tiến độ của user
                UserProgress progress = userProgressRepository
                    .findByUser_UserIdAndLesson_LessonId(userId, lesson.getLessonId())
                    .orElse(null);
                
                boolean isCompleted = progress != null && progress.getIsCompleted();
                Integer completionPercentage = progress != null ? progress.getCompletionPercentage() : 0;
                
                // Build DTO
                LessonWithUnlockStatusDTO dto = LessonWithUnlockStatusDTO.builder()
                    .lessonId(lesson.getLessonId())
                    .courseId(lesson.getCourse().getCourseId())
                    .lessonTitle(lesson.getLessonTitle())
                    .lessonOrder(lesson.getLessonOrder())
                    .description(lesson.getDescription())
                    .videoUrl(lesson.getVideoUrl())
                    .sheetMusicUrl(lesson.getSheetMusicUrl())
                    .durationMinutes(lesson.getDurationMinutes())
                    .expReward(lesson.getExpReward())
                    .expRequire(lesson.getExpRequire())
                    .createdAt(lesson.getCreatedAt())
                    .isUnlocked(isUnlocked)
                    .isCompleted(isCompleted)
                    .completionPercentage(completionPercentage)
                    .userCurrentExp(user.getTotalExp())
                    .expNeeded(expNeeded)
                    .build();
                
                // Nếu lesson còn khóa, thêm thông tin về yêu cầu mở khóa
                if (!isUnlocked) {
                    dto.setUnlockRequirement(lessonUnlockService.getUnlockRequirement(lesson.getLessonId()));
                }
                
                return dto;
            })
            .collect(Collectors.toList());
    }
}