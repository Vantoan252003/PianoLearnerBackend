package com.piano.learn.PianoLearn.entity.achievement;

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
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_achievements", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "achievement_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAchievement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_achievement_id")
    private Integer userAchievementId;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "achievement_id", nullable = false)
    private Achievement achievement;
    
    @Column(name = "unlocked_at")
    private LocalDateTime unlockedAt;
    
    @PrePersist
    protected void onCreate() {
        if (unlockedAt == null) {
            unlockedAt = LocalDateTime.now();
        }
    }
}
