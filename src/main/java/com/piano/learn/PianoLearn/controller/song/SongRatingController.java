package com.piano.learn.PianoLearn.controller.song;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.piano.learn.PianoLearn.dto.song.CreateSongRatingRequest;
import com.piano.learn.PianoLearn.dto.song.SongRatingDTO;
import com.piano.learn.PianoLearn.service.UserService;
import com.piano.learn.PianoLearn.service.song.SongRatingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class SongRatingController {
    
    @Autowired
    private SongRatingService songRatingService;
    
    @Autowired
    private UserService userService;
    
    /**
     * GET /api/songs/{songId}/ratings
     * Lấy tất cả ratings của một bài hát (public, không cần auth)
     */
    @GetMapping("/songs/{songId}/ratings")
    public ResponseEntity<List<SongRatingDTO>> getRatingsBySongId(@PathVariable Integer songId) {
        List<SongRatingDTO> ratings = songRatingService.getRatingsBySongId(songId);
        return ResponseEntity.ok(ratings);
    }
    
    /**
     * POST /api/auth/songs/{songId}/rating
     * Tạo hoặc cập nhật rating của user cho bài hát (cần auth)
     */
    @PostMapping("/auth/songs/{songId}/rating")
    public ResponseEntity<SongRatingDTO> createOrUpdateRating(
            @PathVariable Integer songId,
            @Valid @RequestBody CreateSongRatingRequest request) {
        
        // Lấy userId từ authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Integer userId = userService.findUserByEmail(email).getUserId();
        
        SongRatingDTO rating = songRatingService.createOrUpdateRating(userId, songId, request);
        return ResponseEntity.ok(rating);
    }
    
    /**
     * PUT /api/auth/songs/{songId}/rating
     * Alias cho POST - cập nhật rating (cần auth)
     */
    @PutMapping("/auth/songs/{songId}/rating")
    public ResponseEntity<SongRatingDTO> updateRating(
            @PathVariable Integer songId,
            @Valid @RequestBody CreateSongRatingRequest request) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Integer userId = userService.findUserByEmail(email).getUserId();
        
        SongRatingDTO rating = songRatingService.createOrUpdateRating(userId, songId, request);
        return ResponseEntity.ok(rating);
    }
    
    /**
     * GET /api/auth/songs/{songId}/my-rating
     * Lấy rating của user hiện tại cho bài hát (cần auth)
     */
    @GetMapping("/auth/songs/{songId}/my-rating")
    public ResponseEntity<SongRatingDTO> getMyRating(@PathVariable Integer songId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Integer userId = userService.findUserByEmail(email).getUserId();
        
        return songRatingService.getUserRatingForSong(userId, songId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * DELETE /api/auth/songs/{songId}/rating
     * Xóa rating của user cho bài hát (cần auth)
     */
    @DeleteMapping("/auth/songs/{songId}/rating")
    public ResponseEntity<String> deleteRating(@PathVariable Integer songId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Integer userId = userService.findUserByEmail(email).getUserId();
        
        boolean deleted = songRatingService.deleteRating(userId, songId);
        if (deleted) {
            return ResponseEntity.ok("Rating đã được xóa");
        }
        return ResponseEntity.notFound().build();
    }
}
