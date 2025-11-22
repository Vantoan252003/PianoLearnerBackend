package com.piano.learn.PianoLearn.entity.courses;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table (name = "courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Courses {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "course_id")
    private Integer courseId;

    @Column (name = "course_name")
    private String courseName;
    @Column (name = "description")
    private String description;
    @Column (name = "thumbnail_url")
    private String thumbnailUrl;
    @Column (name = "difficulty_level")
    private String difficultyLevel;
    @Column (name = "duration_weeks")
    private Integer durationWeeks;
    @Column (name = "total_lessons")
    private Integer totalLessons;
    @Column (name = "create_at")
    private LocalDateTime createAt;

    @PrePersist
    void onCreate() {
        if (createAt != null) {
            this.createAt = LocalDateTime.now();
        }
    }

}
