package com.piano.learn.PianoLearn.controller.song;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.piano.learn.PianoLearn.entity.song.Song;
import com.piano.learn.PianoLearn.repository.song.SongRepository;
import com.piano.learn.PianoLearn.service.admin.SongService;

@RestController
@RequestMapping("/api/songs")
public class SongController {
    
    @Autowired
    private SongService songService;
    
    @Autowired
    private SongRepository songRepository;
    
    /**
     * GET /api/songs - Lấy tất cả bài hát
     */
    @GetMapping
    public List<Song> getAllSongs() {
        return songService.getAllSongs();
    }
    
    /**
     * GET /api/songs/search - Tìm kiếm bài hát
     * Params: songTitle, artist, difficultyLevel (optional)
     */
    @GetMapping("/search")
    public ResponseEntity<List<Song>> searchSongs(
            @RequestParam(required = false) String songTitle,
            @RequestParam(required = false) String artist,
            @RequestParam(required = false) String difficultyLevel
    ) {
        List<Song> results = songRepository.searchSongs(songTitle, artist, difficultyLevel);
        return ResponseEntity.ok(results);
    }
}
