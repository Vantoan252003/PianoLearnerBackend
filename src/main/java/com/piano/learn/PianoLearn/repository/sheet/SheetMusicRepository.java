package com.piano.learn.PianoLearn.repository.sheet;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.piano.learn.PianoLearn.entity.sheet.SheetMusic;

@Repository
public interface SheetMusicRepository extends JpaRepository<SheetMusic, Integer> {

}
