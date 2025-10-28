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

import com.piano.learn.PianoLearn.entity.favorite.Favorite;
import com.piano.learn.PianoLearn.service.admin.FavoriteService;

@RestController
@RequestMapping("/api/admin/favorites")
public class AdminFavoriteController {
    
    @Autowired
    private FavoriteService favoriteService;
    
    @GetMapping
    public ResponseEntity<List<Favorite>> getAllFavorites() {
        return ResponseEntity.ok(favoriteService.getAllFavorites());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Favorite> getFavoriteById(@PathVariable Integer id) {
        Favorite favorite = favoriteService.getFavoriteById(id);
        return favorite != null ? ResponseEntity.ok(favorite) : ResponseEntity.notFound().build();
    }
    
    @PostMapping
    public ResponseEntity<Favorite> createFavorite(@RequestBody Favorite favorite) {
        Favorite saved = favoriteService.createFavorite(favorite);
        return ResponseEntity.ok(saved);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Favorite> updateFavorite(@PathVariable Integer id, @RequestBody Favorite favorite) {
        Favorite updated = favoriteService.updateFavorite(id, favorite);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFavorite(@PathVariable Integer id) {
        boolean deleted = favoriteService.deleteFavorite(id);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}
