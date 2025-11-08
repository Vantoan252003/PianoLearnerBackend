package com.piano.learn.PianoLearn.controller.song;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.piano.learn.PianoLearn.entity.song.Song;
import com.piano.learn.PianoLearn.service.admin.SongService;

@RestController
@RequestMapping("/api/songs")
public class SongController {
    
    @Autowired
    private SongService songService;
    
    @GetMapping
    public List<Song> getAllSongs() {
        return songService.getAllSongs();
    }
}
