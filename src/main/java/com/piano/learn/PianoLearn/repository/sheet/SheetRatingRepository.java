package com.piano.learn.PianoLearn.repository.sheet;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.piano.learn.PianoLearn.entity.sheet.SheetRating;

@Repository
public interface SheetRatingRepository extends JpaRepository<SheetRating, Integer> {
    
    /**
     * Lấy tất cả ratings của một sheet music, sắp xếp theo ngày tạo mới nhất
     * Join fetch để load User và SheetMusic luôn
     */
    @Query("SELECT sr FROM SheetRating sr JOIN FETCH sr.user JOIN FETCH sr.sheetMusic WHERE sr.sheetMusic.sheetId = :sheetId ORDER BY sr.createdAt DESC")
    List<SheetRating> findBySheetMusic_SheetIdOrderByCreatedAtDesc(@Param("sheetId") Integer sheetId);
    
    /**
     * Kiểm tra user đã rating sheet music này chưa
     */
    @Query("SELECT sr FROM SheetRating sr JOIN FETCH sr.user JOIN FETCH sr.sheetMusic WHERE sr.user.userId = :userId AND sr.sheetMusic.sheetId = :sheetId")
    Optional<SheetRating> findByUser_UserIdAndSheetMusic_SheetId(@Param("userId") Integer userId, @Param("sheetId") Integer sheetId);
    
    /**
     * Tính rating trung bình của một sheet music
     */
    @Query("SELECT AVG(sr.rating) FROM SheetRating sr WHERE sr.sheetMusic.sheetId = :sheetId")
    Double getAverageRatingBySheetId(@Param("sheetId") Integer sheetId);
    
    /**
     * Đếm số lượng ratings của một sheet music
     */
    @Query("SELECT COUNT(sr) FROM SheetRating sr WHERE sr.sheetMusic.sheetId = :sheetId")
    Long countBySheetId(@Param("sheetId") Integer sheetId);
    
    /**
     * Lấy tất cả ratings với join fetch
     */
    @Query("SELECT sr FROM SheetRating sr JOIN FETCH sr.user JOIN FETCH sr.sheetMusic ORDER BY sr.createdAt DESC")
    List<SheetRating> findAllWithDetails();
}
