package com.piano.learn.PianoLearn.controller.admin;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.piano.learn.PianoLearn.dto.sheet.SheetRatingDTO;
import com.piano.learn.PianoLearn.entity.sheet.SheetMusic;
import com.piano.learn.PianoLearn.entity.sheet.SheetRating;
import com.piano.learn.PianoLearn.repository.sheet.SheetRatingRepository;
import com.piano.learn.PianoLearn.repository.sheet.SheetMusicRepository;

@RestController
@RequestMapping("/api/admin/sheet-ratings")
public class AdminSheetRatingController {
    
    @Autowired
    private SheetRatingRepository sheetRatingRepository;
    
    @Autowired
    private SheetMusicRepository sheetMusicRepository;
    
    /**
     * Admin lấy tất cả sheet ratings
     */
    @GetMapping
    public ResponseEntity<List<SheetRatingDTO>> getAllSheetRatings() {
        List<SheetRating> ratings = sheetRatingRepository.findAllWithDetails();
        List<SheetRatingDTO> dtos = ratings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    
    /**
     * Admin xóa rating bất kỳ
     */
    @DeleteMapping("/{ratingId}")
    public ResponseEntity<String> deleteRating(@PathVariable Integer ratingId) {
        if (sheetRatingRepository.existsById(ratingId)) {
            sheetRatingRepository.deleteById(ratingId);
            return ResponseEntity.ok("Rating deleted successfully");
        }
        return ResponseEntity.notFound().build();
    }
    
    /**
     * Admin lấy tất cả ratings của một sheet music
     */
    @GetMapping("/sheet/{sheetId}")
    public ResponseEntity<List<SheetRatingDTO>> getRatingsBySheetId(@PathVariable Integer sheetId) {
        List<SheetRating> ratings = sheetRatingRepository.findBySheetMusic_SheetIdOrderByCreatedAtDesc(sheetId);
        List<SheetRatingDTO> dtos = ratings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    
    /**
     * Admin force update tất cả rating trung bình cho tất cả sheet music
     */
    @PostMapping("/recalculate-all")
    public ResponseEntity<String> recalculateAllSheetRatings() {
        List<SheetMusic> allSheets = sheetMusicRepository.findAll();
        int updated = 0;
        
        for (SheetMusic sheet : allSheets) {
            Double avgRating = sheetRatingRepository.getAverageRatingBySheetId(sheet.getSheetId());
            sheet.setRating(avgRating != null ? avgRating : 0.0);
            sheetMusicRepository.save(sheet);
            updated++;
        }
        
        return ResponseEntity.ok("Successfully updated ratings for " + updated + " sheet music");
    }
    
    private SheetRatingDTO convertToDTO(SheetRating rating) {
        return SheetRatingDTO.builder()
                .sheetRatingId(rating.getSheetRatingId())
                .userId(rating.getUser().getUserId())
                .username(rating.getUser().getFullName())
                .userEmail(rating.getUser().getEmail())
                .sheetId(rating.getSheetMusic().getSheetId())
                .sheetTitle(rating.getSheetMusic().getTitle())
                .rating(rating.getRating())
                .comment(rating.getComment())
                .createdAt(rating.getCreatedAt())
                .updatedAt(rating.getUpdatedAt())
                .build();
    }
}
