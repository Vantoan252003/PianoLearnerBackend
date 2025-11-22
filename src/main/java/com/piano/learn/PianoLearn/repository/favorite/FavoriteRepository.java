package com.piano.learn.PianoLearn.repository.favorite;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.piano.learn.PianoLearn.entity.favorite.Favorite;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {
    List<Favorite> findByUser_UserId(Integer userId);
    Optional<Favorite> findByUser_UserIdAndSong_SongId(Integer userId, Integer songId);
    Optional<Favorite> findByUser_UserIdAndSheetMusic_SheetId(Integer userId, Integer sheetId);
}
