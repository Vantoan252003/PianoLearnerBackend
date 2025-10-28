package com.piano.learn.PianoLearn.entity.practice;

import java.time.LocalDate;

import com.piano.learn.PianoLearn.entity.auth.User;
import com.piano.learn.PianoLearn.entity.exercise.Exercise;
import com.piano.learn.PianoLearn.entity.song.Song;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "practice_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PracticeSession {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    private Integer sessionId;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "song_id")
    private Song song;
    
    @ManyToOne
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;
    
    @Column(name = "practice_duration_minutes", nullable = false)
    private Integer practiceDurationMinutes;
    
    @Column(name = "session_date", nullable = false)
    private LocalDate sessionDate;
    
    @Column(name = "exp_earned")
    @Builder.Default
    private Integer expEarned = 0;
    
    @Column(name = "notes_played")
    @Builder.Default
    private Integer notesPlayed = 0;
}
