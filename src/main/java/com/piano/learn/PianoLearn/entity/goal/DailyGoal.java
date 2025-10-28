package com.piano.learn.PianoLearn.entity.goal;

import java.time.LocalDate;

import com.piano.learn.PianoLearn.entity.auth.User;

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
@Table(name = "daily_goals", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "goal_date"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyGoal {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "goal_id")
    private Integer goalId;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "goal_date", nullable = false)
    private LocalDate goalDate;
    
    @Column(name = "target_minutes")
    @Builder.Default
    private Integer targetMinutes = 30;
    
    @Column(name = "completed_minutes")
    @Builder.Default
    private Integer completedMinutes = 0;
    
    @Column(name = "is_completed")
    @Builder.Default
    private Boolean isCompleted = false;
}
