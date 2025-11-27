package com.piano.learn.PianoLearn.controller.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.piano.learn.PianoLearn.entity.favorite.Favorite;
import com.piano.learn.PianoLearn.service.UserService;
import com.piano.learn.PianoLearn.service.admin.FavoriteService;

@RestController
@RequestMapping("/api/user/favorites")
public class UserFavoriteController {
    
    @Autowired
    private FavoriteService favoriteService;
    
    @Autowired
    private UserService userService;
    
    /**
     * GET /api/user/favorites
     * Lấy danh sách yêu thích của user
     */
    @GetMapping
    public ResponseEntity<List<Favorite>> getMyFavorites() {
        Integer userId = getCurrentUserId();
        List<Favorite> favorites = favoriteService.getFavoritesByUserId(userId);
        return ResponseEntity.ok(favorites);
    }
    
    /**
     * POST /api/user/favorites/song/{songId}
     * Thêm bài hát vào yêu thích
     */
    @PostMapping("/song/{songId}")
    public ResponseEntity<String> addFavoriteSong(@PathVariable Integer songId) {
        Integer userId = getCurrentUserId();
        favoriteService.addFavoriteSong(userId, songId);
        return ResponseEntity.ok("Song added to favorites successfully");
    }
    
    /**
     * POST /api/user/favorites/sheet/{sheetId}
     * Thêm sheet music vào yêu thích
     */
    @PostMapping("/sheet/{sheetId}")
    public ResponseEntity<String> addFavoriteSheet(@PathVariable Integer sheetId) {
        Integer userId = getCurrentUserId();
        favoriteService.addFavoriteSheet(userId, sheetId);
        return ResponseEntity.ok("Sheet music added to favorites successfully");
    }
    
    /**
     * DELETE /api/user/favorites/song/{songId}
     * Hủy yêu thích một bài hát
     */
    @DeleteMapping("/song/{songId}")
    public ResponseEntity<String> removeFavoriteSong(@PathVariable Integer songId) {
        Integer userId = getCurrentUserId();
        boolean removed = favoriteService.removeFavoriteSong(userId, songId);
        if (removed) {
            return ResponseEntity.ok("Song removed from favorites successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * DELETE /api/user/favorites/sheet/{sheetId}
     * Hủy yêu thích một sheet music
     */
    @DeleteMapping("/sheet/{sheetId}")
    public ResponseEntity<String> removeFavoriteSheet(@PathVariable Integer sheetId) {
        Integer userId = getCurrentUserId();
        boolean removed = favoriteService.removeFavoriteSheet(userId, sheetId);
        if (removed) {
            return ResponseEntity.ok("Sheet music removed from favorites successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    private Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userService.findUserByEmail(email).getUserId();
    }
}