package com.piano.learn.PianoLearn.controller.sheet_music;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.piano.learn.PianoLearn.dto.sheet.CreateSheetRatingRequest;
import com.piano.learn.PianoLearn.dto.sheet.SheetRatingDTO;
import com.piano.learn.PianoLearn.service.UserService;
import com.piano.learn.PianoLearn.service.sheet_music.SheetRatingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class SheetRatingController {
    
    @Autowired
    private SheetRatingService sheetRatingService;
    
    @Autowired
    private UserService userService;
    
    /**
     * GET /api/sheets/{sheetId}/ratings
     * Lấy tất cả ratings của một sheet music (public, không cần auth)
     */
    @GetMapping("/auth/sheets/{sheetId}/ratings")
    public ResponseEntity<List<SheetRatingDTO>> getRatingsBySheetId(@PathVariable Integer sheetId) {
        List<SheetRatingDTO> ratings = sheetRatingService.getRatingsBySheetId(sheetId);
        return ResponseEntity.ok(ratings);
    }
    
    /**
     * POST /api/auth/sheets/{sheetId}/rating
     * Tạo hoặc cập nhật rating của user cho sheet music (cần auth)
     */
    @PostMapping("/auth/sheets/{sheetId}/ratings")
    public ResponseEntity<SheetRatingDTO> createOrUpdateRating(
            @PathVariable Integer sheetId,
            @Valid @RequestBody CreateSheetRatingRequest request) {
        
        // Lấy userId từ authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Integer userId = userService.findUserByEmail(email).getUserId();
        
        SheetRatingDTO rating = sheetRatingService.createOrUpdateRating(userId, sheetId, request);
        return ResponseEntity.ok(rating);
    }
    
  
    @PutMapping("/auth/sheets/{sheetId}/ratings")
    public ResponseEntity<SheetRatingDTO> updateRating(
            @PathVariable Integer sheetId,
            @Valid @RequestBody CreateSheetRatingRequest request) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Integer userId = userService.findUserByEmail(email).getUserId();
        
        SheetRatingDTO rating = sheetRatingService.createOrUpdateRating(userId, sheetId, request);
        return ResponseEntity.ok(rating);
    }
    

    @GetMapping("/auth/sheets/{sheetId}/my-rating")
    public ResponseEntity<SheetRatingDTO> getMyRating(@PathVariable Integer sheetId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Integer userId = userService.findUserByEmail(email).getUserId();
        
        return sheetRatingService.getUserRatingForSheet(userId, sheetId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
 
    @DeleteMapping("/auth/sheets/{sheetId}/ratings")
    public ResponseEntity<String> deleteRating(@PathVariable Integer sheetId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Integer userId = userService.findUserByEmail(email).getUserId();
        
        boolean deleted = sheetRatingService.deleteRating(userId, sheetId);
        if (deleted) {
            return ResponseEntity.ok("Rating đã được xóa");
        }
        return ResponseEntity.notFound().build();
    }
}
