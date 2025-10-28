package com.piano.learn.PianoLearn.entity.progress;

import java.time.LocalDateTime;

import com.piano.learn.PianoLearn.entity.auth.User;
import com.piano.learn.PianoLearn.entity.lesson.Lesson;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_progress", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "lesson_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProgress {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "progress_id")
    private Integer progressId;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;
    
    @Column(name = "is_completed")
    @Builder.Default
    private Boolean isCompleted = false;
    
    @Column(name = "completion_percentage")
    @Builder.Default
    private Integer completionPercentage = 0;
    
    @Column(name = "time_spent_minutes")
    @Builder.Default
    private Integer timeSpentMinutes = 0;
    
    @Column(name = "last_accessed")
    private LocalDateTime lastAccessed;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
}
