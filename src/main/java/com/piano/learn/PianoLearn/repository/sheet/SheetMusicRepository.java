package com.piano.learn.PianoLearn.repository.sheet;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.piano.learn.PianoLearn.entity.sheet.SheetMusic;

@Repository
public interface SheetMusicRepository extends JpaRepository<SheetMusic, Integer> {
    
    List<SheetMusic> findByDifficultyLevel(String difficultyLevel);
    
    List<SheetMusic> findByComposerContainingIgnoreCase(String composer);
    
    List<SheetMusic> findByTitleContainingIgnoreCase(String title);
    
    @Query("SELECT s FROM SheetMusic s WHERE " +
           "(:title IS NULL OR LOWER(s.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:composer IS NULL OR LOWER(s.composer) LIKE LOWER(CONCAT('%', :composer, '%'))) AND " +
           "(:difficultyLevel IS NULL OR s.difficultyLevel = :difficultyLevel) AND " +
           "(:isPublic IS NULL OR s.isPublic = :isPublic)")
    List<SheetMusic> searchSheetMusic(
        @Param("title") String title,
        @Param("composer") String composer,
        @Param("difficultyLevel") String difficultyLevel,
        @Param("isPublic") Boolean isPublic
    );
    
    List<SheetMusic> findByIsPublicTrue();
    
    // Tìm tất cả sheet music của một user
    List<SheetMusic> findByUploadedBy_UserId(Integer userId);
}
