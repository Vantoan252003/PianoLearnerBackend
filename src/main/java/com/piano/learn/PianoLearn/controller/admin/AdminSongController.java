package com.piano.learn.PianoLearn.controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.piano.learn.PianoLearn.entity.song.Song;
import com.piano.learn.PianoLearn.service.admin.SongService;

@RestController
@RequestMapping("/api/admin/songs")
public class AdminSongController {
    
    @Autowired
    private SongService songService;
    
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
    public ResponseEntity<Song> createSong(@RequestBody Song song) {
        Song saved = songService.createSong(song);
        return ResponseEntity.ok(saved);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Song> updateSong(@PathVariable Integer id, @RequestBody Song song) {
        Song updated = songService.updateSong(id, song);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSong(@PathVariable Integer id) {
        boolean deleted = songService.deleteSong(id);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}
