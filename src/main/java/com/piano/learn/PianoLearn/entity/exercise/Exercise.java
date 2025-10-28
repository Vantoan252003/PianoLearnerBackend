package com.piano.learn.PianoLearn.entity.exercise;

import java.time.LocalDateTime;

import com.piano.learn.PianoLearn.entity.lesson.Lesson;

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
@Table(name = "exercises")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Exercise {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exercise_id")
    private Integer exerciseId;
    
    @ManyToOne
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;
    
    @Column(name = "exercise_title", nullable = false, length = 150)
    private String exerciseTitle;
    
    @Column(name = "exercise_type", length = 50)
    private String exerciseType;
    
    @Column(name = "difficulty", length = 50)
    private String difficulty;
    
    @Column(name = "midi_file_url", length = 255)
    private String midiFileUrl;
    
    @Column(name = "sheet_music_url", length = 255)
    private String sheetMusicUrl;
    
    @Column(name = "demo_audio_url", length = 255)
    private String demoAudioUrl;
    
    @Column(name = "target_score")
    @Builder.Default
    private Integer targetScore = 80;
    
    @Column(name = "max_score")
    @Builder.Default
    private Integer maxScore = 100;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
