package com.piano.learn.PianoLearn.controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.piano.learn.PianoLearn.entity.song.Song;
import com.piano.learn.PianoLearn.service.admin.SongService;
import com.piano.learn.PianoLearn.service.common.CloudinaryService;

@RestController
@RequestMapping("/api/admin/songs")
public class AdminSongController {
    
    @Autowired
    private SongService songService;
    
    @Autowired
    private CloudinaryService cloudinaryService;
    
    @GetMapping
    public ResponseEntity<List<Song>> getAllSongs() {
        return ResponseEntity.ok(songService.getAllSongs());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Song> getSongById(@PathVariable Integer id) {
        Song song = songService.getSongById(id);
        return song != null ? ResponseEntity.ok(song) : ResponseEntity.notFound().build();
    }
    
    @PostMapping
    public ResponseEntity<Song> createSong(
            @RequestParam("songTitle") String songTitle,
            @RequestParam("artist") String artist,
            @RequestParam("difficultyLevel") String difficultyLevel,
            @RequestParam(value = "durationSeconds", required = false) Integer durationSeconds,
            @RequestParam(value = "popularityScore", defaultValue = "0") Integer popularityScore,
            @RequestParam(value = "thumbnailUrl", required = false) String thumbnailUrl,
            @RequestParam(value = "midiFile", required = false) MultipartFile midiFile,
            @RequestParam(value = "sheetMusicFile", required = false) MultipartFile sheetMusicFile,
            @RequestParam(value = "audioUrl", required = false) String audioUrl) throws Exception {
        
        Song song = Song.builder()
                .songTitle(songTitle)
                .artist(artist)
                .difficultyLevel(difficultyLevel)
                .durationSeconds(durationSeconds)
                .popularityScore(popularityScore)
                .thumbnailUrl(thumbnailUrl)
                .audioUrl(audioUrl)
                .build();
        
        if (midiFile != null && !midiFile.isEmpty()) {
            String midiUrl = cloudinaryService.uploadFile(midiFile);
            song.setMidiFileUrl(midiUrl);
        }
        
        if (sheetMusicFile != null && !sheetMusicFile.isEmpty()) {
            String sheetUrl = cloudinaryService.uploadFile(sheetMusicFile);
            song.setSheetMusicUrl(sheetUrl);
        }
        
        Song saved = songService.createSong(song);
        return ResponseEntity.ok(saved);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Song> updateSong(
            @PathVariable Integer id,
            @RequestParam("songTitle") String songTitle,
            @RequestParam("artist") String artist,
            @RequestParam("difficultyLevel") String difficultyLevel,
            @RequestParam(value = "durationSeconds", required = false) Integer durationSeconds,
            @RequestParam(value = "popularityScore", defaultValue = "0") Integer popularityScore,
            @RequestParam(value = "thumbnailUrl", required = false) String thumbnailUrl,
            @RequestParam(value = "midiFile", required = false) MultipartFile midiFile,
            @RequestParam(value = "sheetMusicFile", required = false) MultipartFile sheetMusicFile,
            @RequestParam(value = "audioUrl", required = false) String audioUrl) throws Exception {
        
        Song existingSong = songService.getSongById(id);
        if (existingSong == null) {
            return ResponseEntity.notFound().build();
        }
        
        existingSong.setSongTitle(songTitle);
        existingSong.setArtist(artist);
        existingSong.setDifficultyLevel(difficultyLevel);
        existingSong.setDurationSeconds(durationSeconds);
        existingSong.setPopularityScore(popularityScore);
        existingSong.setThumbnailUrl(thumbnailUrl);
        existingSong.setAudioUrl(audioUrl);
        
        if (midiFile != null && !midiFile.isEmpty()) {
            String midiUrl = cloudinaryService.uploadFile(midiFile);
            existingSong.setMidiFileUrl(midiUrl);
        }
        
        if (sheetMusicFile != null && !sheetMusicFile.isEmpty()) {
            String sheetUrl = cloudinaryService.uploadFile(sheetMusicFile);
            existingSong.setSheetMusicUrl(sheetUrl);
        }
        
        Song updated = songService.updateSong(id, existingSong);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSong(@PathVariable Integer id) {
        boolean deleted = songService.deleteSong(id);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}
