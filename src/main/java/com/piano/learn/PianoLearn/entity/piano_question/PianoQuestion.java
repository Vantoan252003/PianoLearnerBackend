package com.piano.learn.PianoLearn.entity.piano_question;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.piano.learn.PianoLearn.entity.lesson.Lesson;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "piano_questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PianoQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id")
    private Lesson lesson; 

    @Column(nullable = false, columnDefinition = "json") 
    private String midiNumbers;

    @Column(nullable = true, columnDefinition = "json")
    private String chord;

    @Column(name = "question_count", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer questionCount;

    @Column(length = 50)
    private String difficulty;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
