package com.piano.learn.PianoLearn.service.admin;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.piano.learn.PianoLearn.entity.auth.User;
import com.piano.learn.PianoLearn.entity.favorite.Favorite;
import com.piano.learn.PianoLearn.entity.sheet.SheetMusic;
import com.piano.learn.PianoLearn.entity.song.Song;
import com.piano.learn.PianoLearn.repository.auth.UserRepository;
import com.piano.learn.PianoLearn.repository.favorite.FavoriteRepository;
import com.piano.learn.PianoLearn.repository.sheet.SheetMusicRepository;
import com.piano.learn.PianoLearn.repository.song.SongRepository;
import com.piano.learn.PianoLearn.service.notification.NotificationService;

@Service
public class FavoriteService {
    
    @Autowired
    private FavoriteRepository favoriteRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private SongRepository songRepository;
    
    @Autowired
    private SheetMusicRepository sheetMusicRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    public List<Favorite> getAllFavorites() {
        return favoriteRepository.findAll();
    }
    
    public Favorite getFavoriteById(Integer id) {
        return favoriteRepository.findById(id).orElse(null);
    }
    
    public Favorite createFavorite(Favorite favorite) {
        return favoriteRepository.save(favorite);
    }
    
    public Favorite updateFavorite(Integer id, Favorite favorite) {
        if (favoriteRepository.existsById(id)) {
            favorite.setFavoriteId(id);
            return favoriteRepository.save(favorite);
        }
        return null;
    }
    
    public boolean deleteFavorite(Integer id) {
        if (favoriteRepository.existsById(id)) {
            favoriteRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    public List<Favorite> getFavoritesByUserId(Integer userId) {
        return favoriteRepository.findByUser_UserId(userId);
    }
    
    // Add favorite for a song
    public void addFavoriteSong(Integer userId, Integer songId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Song song = songRepository.findById(songId).orElseThrow(() -> new RuntimeException("Song not found"));
        
        // Check if already favorited
        Optional<Favorite> existing = favoriteRepository.findByUser_UserIdAndSong_SongId(userId, songId);
        
        if (existing.isEmpty()) {
            Favorite favorite = Favorite.builder()
                .user(user)
                .song(song)
                .sheetMusic(null)
                .build();
            favoriteRepository.save(favorite);
            
            // TODO: Gửi thông báo cho owner của bài hát (cần thêm trường uploadedBy vào Song entity)
            // notificationService.notifyOnSongFavorited(song.getUploadedBy().getUserId(), user, song);
        }
    }
    
    // Add favorite for a sheet music
    public void addFavoriteSheet(Integer userId, Integer sheetId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        SheetMusic sheetMusic = sheetMusicRepository.findById(sheetId).orElseThrow(() -> new RuntimeException("Sheet music not found"));
        
        // Check if already favorited
        Optional<Favorite> existing = favoriteRepository.findByUser_UserIdAndSheetMusic_SheetId(userId, sheetId);
        
        if (existing.isEmpty()) {
            Favorite favorite = Favorite.builder()
                .user(user)
                .song(null)
                .sheetMusic(sheetMusic)
                .build();
            favoriteRepository.save(favorite);
            
            if (sheetMusic.getUploadedBy() != null) {
                notificationService.notifyOnSheetFavorited(
                    sheetMusic.getUploadedBy().getUserId(), 
                    user, 
                    sheetMusic
                );
            }
        }
    }
    
    // Remove favorite by song
    public boolean removeFavoriteSong(Integer userId, Integer songId) {
        Optional<Favorite> favorite = favoriteRepository.findByUser_UserIdAndSong_SongId(userId, songId);
        
        if (favorite.isPresent()) {
            favoriteRepository.delete(favorite.get());
            return true;
        }
        return false;
    }
    
    // Remove favorite by sheet
    public boolean removeFavoriteSheet(Integer userId, Integer sheetId) {
        Optional<Favorite> favorite = favoriteRepository.findByUser_UserIdAndSheetMusic_SheetId(userId, sheetId);
        
        if (favorite.isPresent()) {
            favoriteRepository.delete(favorite.get());
            return true;
        }
        return false;
    }
}
