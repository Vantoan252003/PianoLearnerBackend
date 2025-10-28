package com.piano.learn.PianoLearn.repository.song;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.piano.learn.PianoLearn.entity.song.Song;

@Repository
public interface SongRepository extends JpaRepository<Song, Integer> {
    List<Song> findByDifficultyLevel(String difficultyLevel);
}
