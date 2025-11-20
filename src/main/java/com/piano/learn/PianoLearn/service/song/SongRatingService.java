package com.piano.learn.PianoLearn.service.song;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.piano.learn.PianoLearn.dto.song.CreateSongRatingRequest;
import com.piano.learn.PianoLearn.dto.song.SongRatingDTO;
import com.piano.learn.PianoLearn.entity.auth.User;
import com.piano.learn.PianoLearn.entity.song.Song;
import com.piano.learn.PianoLearn.entity.song.SongRating;
import com.piano.learn.PianoLearn.repository.song.SongRatingRepository;
import com.piano.learn.PianoLearn.repository.song.SongRepository;
import com.piano.learn.PianoLearn.service.UserService;

@Service
public class SongRatingService {
    
    @Autowired
    private SongRatingRepository songRatingRepository;
    
    @Autowired
    private SongRepository songRepository;
    
    /**
     * Tạo hoặc cập nhật rating của user cho bài hát
     */
    @Transactional
    public SongRatingDTO createOrUpdateRating(Integer userId, Integer songId, CreateSongRatingRequest request) {
        // Kiểm tra song có tồn tại không
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("Bài hát không tồn tại"));
        
        // Kiểm tra user đã rating bài hát này chưa
        Optional<SongRating> existingRating = songRatingRepository.findByUser_UserIdAndSong_SongId(userId, songId);
        
        SongRating songRating;
        if (existingRating.isPresent()) {
            // Update rating cũ
            songRating = existingRating.get();
            songRating.setRating(request.getRating());
            songRating.setComment(request.getComment());
            songRating.setUpdatedAt(LocalDateTime.now());
        } else {
            // Tạo rating mới
            User user = User.builder().userId(userId).build();
            songRating = SongRating.builder()
                    .user(user)
                    .song(song)
                    .rating(request.getRating())
                    .comment(request.getComment())
                    .build();
        }
        
        SongRating saved = songRatingRepository.save(songRating);
        
        // Cập nhật average rating cho bài hát
        updateSongAverageRating(songId);
        
        return convertToDTO(saved);
    }
    
    /**
     * Lấy tất cả ratings của một bài hát
     */
    public List<SongRatingDTO> getRatingsBySongId(Integer songId) {
        List<SongRating> ratings = songRatingRepository.findBySong_SongIdOrderByCreatedAtDesc(songId);
        return ratings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy rating của user cho một bài hát (nếu có)
     */
    public Optional<SongRatingDTO> getUserRatingForSong(Integer userId, Integer songId) {
        return songRatingRepository.findByUser_UserIdAndSong_SongId(userId, songId)
                .map(this::convertToDTO);
    }
    
    /**
     * Xóa rating
     */
    @Transactional
    public boolean deleteRating(Integer userId, Integer songId) {
        Optional<SongRating> rating = songRatingRepository.findByUser_UserIdAndSong_SongId(userId, songId);
        if (rating.isPresent()) {
            songRatingRepository.delete(rating.get());
            updateSongAverageRating(songId);
            return true;
        }
        return false;
    }
    
    /**
     * Cập nhật average rating cho bài hát
     */
    private void updateSongAverageRating(Integer songId) {
        Double averageRating = songRatingRepository.getAverageRatingBySongId(songId);
        Song song = songRepository.findById(songId).orElse(null);
        if (song != null) {
            song.setRating(averageRating != null ? averageRating : 0.0);
            songRepository.save(song);
        }
    }
    
    /**
     * Convert entity sang DTO
     */
    private SongRatingDTO convertToDTO(SongRating rating) {
        // Load full user entity để lấy thông tin
        User user = rating.getUser();
        return SongRatingDTO.builder()
                .songRatingId(rating.getSongRatingId())
                .userId(user.getUserId())
                .username(user.getFullName())
                .userEmail(user.getEmail())
                .songId(rating.getSong().getSongId())
                .songTitle(rating.getSong().getSongTitle())
                .rating(rating.getRating())
                .comment(rating.getComment())
                .createdAt(rating.getCreatedAt())
                .updatedAt(rating.getUpdatedAt())
                .build();
    }
}
