package com.piano.learn.PianoLearn.service.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.piano.learn.PianoLearn.entity.favorite.Favorite;
import com.piano.learn.PianoLearn.repository.favorite.FavoriteRepository;

@Service
public class FavoriteService {
    
    @Autowired
    private FavoriteRepository favoriteRepository;
    
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
}
