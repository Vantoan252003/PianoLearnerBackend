package com.piano.learn.PianoLearn.entity.song;

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
@Table(name = "songs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Song {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "song_id")
    private Integer songId;
    
    @Column(name = "song_title", nullable = false, length = 150)
    private String songTitle;
    
    @Column(name = "artist", length = 100)
    private String artist;
    
    @Column(name = "difficulty_level", length = 50)
    private String difficultyLevel;
    
    @Column(name = "duration_seconds")
    private Integer durationSeconds;
    
    @Column(name = "thumbnail_url", length = 255)
    private String thumbnailUrl;
    
    @Column(name = "midi_file_url", length = 255)
    private String midiFileUrl;
    
    @Column(name = "sheet_music_url", length = 255)
    private String sheetMusicUrl;
    
    @Column(name = "audio_url", length = 255)
    private String audioUrl;
    
    @Column(name = "popularity_score")
    @Builder.Default
    private Integer popularityScore = 0;
    
    @Column(name = "rating")
    @Builder.Default
    private Double rating = 0.0;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
