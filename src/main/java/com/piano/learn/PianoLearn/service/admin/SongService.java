package com.piano.learn.PianoLearn.service.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.piano.learn.PianoLearn.entity.song.Song;
import com.piano.learn.PianoLearn.repository.song.SongRepository;

@Service
public class SongService {
    
    @Autowired
    private SongRepository songRepository;
    
    public List<Song> getAllSongs() {
        return songRepository.findAll();
    }
    
    public Song getSongById(Integer id) {
        return songRepository.findById(id).orElse(null);
    }
    
    public Song createSong(Song song) {
        return songRepository.save(song);
    }
    
    public Song updateSong(Integer id, Song song) {
        if (songRepository.existsById(id)) {
            song.setSongId(id);
            return songRepository.save(song);
        }
        return null;
    }
    
    public boolean deleteSong(Integer id) {
        if (songRepository.existsById(id)) {
            songRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
