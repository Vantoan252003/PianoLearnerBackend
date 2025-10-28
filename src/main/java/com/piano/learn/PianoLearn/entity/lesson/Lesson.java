package com.piano.learn.PianoLearn.entity.lesson;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.piano.learn.PianoLearn.entity.courses.Courses;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "lessons")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Lesson {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lesson_id")
    private Integer lessonId;
    
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Courses course;
    
    @Column(name = "lesson_title", nullable = false, length = 150)
    private String lessonTitle;
    
    @Column(name = "lesson_order", nullable = false)
    private Integer lessonOrder;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "video_url", length = 255)
    private String videoUrl;
    
    @Column(name = "sheet_music_url", length = 255)
    private String sheetMusicUrl;
    
    @Column(name = "duration_minutes")
    private Integer durationMinutes;
    
    @Column(name = "exp_reward")
    @Builder.Default
    private Integer expReward = 10;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
