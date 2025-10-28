package com.piano.learn.PianoLearn.entity.exercise;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.piano.learn.PianoLearn.entity.auth.User;

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
@Table(name = "exercise_results")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseResult {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_id")
    private Integer resultId;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;
    
    @Column(name = "score", nullable = false)
    private Integer score;
    
    @Column(name = "accuracy_percent", precision = 5, scale = 2)
    private BigDecimal accuracyPercent;
    
    @Column(name = "timing_accuracy", precision = 5, scale = 2)
    private BigDecimal timingAccuracy;
    
    @Column(name = "notes_correct")
    private Integer notesCorrect;
    
    @Column(name = "notes_total")
    private Integer notesTotal;
    
    @Column(name = "is_passed")
    @Builder.Default
    private Boolean isPassed = false;
    
    @Column(name = "played_at")
    private LocalDateTime playedAt;
    
    @PrePersist
    protected void onCreate() {
        if (playedAt == null) {
            playedAt = LocalDateTime.now();
        }
    }
}
