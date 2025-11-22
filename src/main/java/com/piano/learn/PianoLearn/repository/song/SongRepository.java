package com.piano.learn.PianoLearn.repository.song;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.piano.learn.PianoLearn.entity.song.Song;

@Repository
public interface SongRepository extends JpaRepository<Song, Integer> {
    
    List<Song> findByDifficultyLevel(String difficultyLevel);
    
    List<Song> findBySongTitleContainingIgnoreCase(String songTitle);
    
    List<Song> findByArtistContainingIgnoreCase(String artist);
    
    @Query("SELECT s FROM Song s WHERE " +
           "(:songTitle IS NULL OR LOWER(s.songTitle) LIKE LOWER(CONCAT('%', :songTitle, '%'))) AND " +
           "(:artist IS NULL OR LOWER(s.artist) LIKE LOWER(CONCAT('%', :artist, '%'))) AND " +
           "(:difficultyLevel IS NULL OR s.difficultyLevel = :difficultyLevel)")
    List<Song> searchSongs(
        @Param("songTitle") String songTitle,
        @Param("artist") String artist,
        @Param("difficultyLevel") String difficultyLevel
    );
}
