package com.piano.learn.PianoLearn.repository.song;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.piano.learn.PianoLearn.entity.song.SongRating;

@Repository
public interface SongRatingRepository extends JpaRepository<SongRating, Integer> {
    
    /**
     * Lấy tất cả ratings của một bài hát, sắp xếp theo ngày tạo mới nhất
     * Join fetch để load User và Song luôn
     */
    @Query("SELECT sr FROM SongRating sr JOIN FETCH sr.user JOIN FETCH sr.song WHERE sr.song.songId = :songId ORDER BY sr.createdAt DESC")
    List<SongRating> findBySong_SongIdOrderByCreatedAtDesc(@Param("songId") Integer songId);
    
    /**
     * Kiểm tra user đã rating bài hát này chưa
     */
    @Query("SELECT sr FROM SongRating sr JOIN FETCH sr.user JOIN FETCH sr.song WHERE sr.user.userId = :userId AND sr.song.songId = :songId")
    Optional<SongRating> findByUser_UserIdAndSong_SongId(@Param("userId") Integer userId, @Param("songId") Integer songId);
    
    /**
     * Tính rating trung bình của một bài hát
     */
    @Query("SELECT AVG(sr.rating) FROM SongRating sr WHERE sr.song.songId = :songId")
    Double getAverageRatingBySongId(@Param("songId") Integer songId);
    
    /**
     * Đếm số lượng ratings của một bài hát
     */
    Long countBySong_SongId(Integer songId);
    
    /**
     * Lấy tất cả ratings của một user
     */
    @Query("SELECT sr FROM SongRating sr JOIN FETCH sr.user JOIN FETCH sr.song WHERE sr.user.userId = :userId ORDER BY sr.createdAt DESC")
    List<SongRating> findByUser_UserId(@Param("userId") Integer userId);
}
