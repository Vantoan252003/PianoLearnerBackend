package com.piano.learn.PianoLearn.controller.admin;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.piano.learn.PianoLearn.dto.song.SongRatingDTO;
import com.piano.learn.PianoLearn.entity.song.Song;
import com.piano.learn.PianoLearn.entity.song.SongRating;
import com.piano.learn.PianoLearn.repository.song.SongRatingRepository;
import com.piano.learn.PianoLearn.repository.song.SongRepository;

@RestController
@RequestMapping("/api/admin/song-ratings")
public class AdminSongRatingController {
    
    @Autowired
    private SongRatingRepository songRatingRepository;
    
    @Autowired
    private SongRepository songRepository;
    
    /**
     * Admin xóa rating bất kỳ
     */
    @DeleteMapping("/{ratingId}")
    public ResponseEntity<String> deleteRating(@PathVariable Integer ratingId) {
        if (songRatingRepository.existsById(ratingId)) {
            songRatingRepository.deleteById(ratingId);
            return ResponseEntity.ok("Rating deleted successfully");
        }
        return ResponseEntity.notFound().build();
    }
    
    /**
     * Admin lấy tất cả ratings của một user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SongRatingDTO>> getRatingsByUserId(@PathVariable Integer userId) {
        List<SongRating> ratings = songRatingRepository.findByUser_UserId(userId);
        List<SongRatingDTO> dtos = ratings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    
    /**
     * Admin lấy tất cả ratings của một bài hát
     */
    @GetMapping("/song/{songId}")
    public ResponseEntity<List<SongRatingDTO>> getRatingsBySongId(@PathVariable Integer songId) {
        List<SongRating> ratings = songRatingRepository.findBySong_SongIdOrderByCreatedAtDesc(songId);
        List<SongRatingDTO> dtos = ratings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    
    /**
     * Admin force update tất cả rating trung bình cho tất cả songs
     */
    @PostMapping("/recalculate-all")
    public ResponseEntity<String> recalculateAllSongRatings() {
        List<Song> allSongs = songRepository.findAll();
        int updated = 0;
        
        for (Song song : allSongs) {
            Double avgRating = songRatingRepository.getAverageRatingBySongId(song.getSongId());
            song.setRating(avgRating != null ? avgRating : 0.0);
            songRepository.save(song);
            updated++;
        }
        
        return ResponseEntity.ok("Successfully updated ratings for " + updated + " songs");
    }
    
    private SongRatingDTO convertToDTO(SongRating rating) {
        return SongRatingDTO.builder()
                .songRatingId(rating.getSongRatingId())
                .userId(rating.getUser().getUserId())
                .username(rating.getUser().getFullName())
                .userEmail(rating.getUser().getEmail())
                .songId(rating.getSong().getSongId())
                .songTitle(rating.getSong().getSongTitle())
                .rating(rating.getRating())
                .comment(rating.getComment())
                .createdAt(rating.getCreatedAt())
                .updatedAt(rating.getUpdatedAt())
                .build();
    }
}
