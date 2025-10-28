package com.piano.learn.PianoLearn.entity.achievement;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "achievements")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Achievement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "achievement_id")
    private Integer achievementId;
    
    @Column(name = "achievement_name", nullable = false, length = 100)
    private String achievementName;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "icon_url", length = 255)
    private String iconUrl;
    
    @Column(name = "requirement_type", length = 50)
    private String requirementType;
    
    @Column(name = "requirement_value")
    private Integer requirementValue;
    
    @Column(name = "exp_reward")
    @Builder.Default
    private Integer expReward = 50;
}
