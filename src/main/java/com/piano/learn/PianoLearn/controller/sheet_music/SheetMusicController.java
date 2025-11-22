package com.piano.learn.PianoLearn.controller.sheet_music;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.piano.learn.PianoLearn.entity.sheet.SheetMusic;
import com.piano.learn.PianoLearn.entity.auth.User;
import com.piano.learn.PianoLearn.repository.sheet.SheetMusicRepository;
import com.piano.learn.PianoLearn.service.UploadService;
import com.piano.learn.PianoLearn.service.UserService;

@RestController
@RequestMapping("/api/auth/sheet-music")
public class SheetMusicController {
    
    @Autowired
    private SheetMusicRepository sheetMusicRepository;
    
    @Autowired
    private UploadService uploadService;
    
    @Autowired
    private UserService userService;
    
    /**
     * GET /api/sheet-music - Lấy tất cả sheet music công khai
     */
    @GetMapping
    public ResponseEntity<List<SheetMusic>> getAllPublicSheetMusic() {
        return ResponseEntity.ok(sheetMusicRepository.findByIsPublicTrue());
    }
    
    /**
     * GET /api/sheet-music/search - Tìm kiếm sheet music
     * Params: title, composer, difficultyLevel (optional)
     */
    @GetMapping("/search")
    public ResponseEntity<List<SheetMusic>> searchSheetMusic(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String composer,
            @RequestParam(required = false) String difficultyLevel
    ) {
        List<SheetMusic> results = sheetMusicRepository.searchSheetMusic(
            title, 
            composer, 
            difficultyLevel, 
            true  // chỉ lấy public sheets
        );
        return ResponseEntity.ok(results);
    }
    
    /**
     * POST /api/sheet-music - Upload sheet music
     * multipart form-data: file, title, composer (optional), description (optional), difficultyLevel (optional)
     */
    @PostMapping
    public ResponseEntity<?> uploadSheetMusic(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam(value = "composer", required = false) String composer,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "difficultyLevel", required = false) String difficultyLevel
    ) throws IOException {
        
        // Upload file to cloud storage
        String fileUrl = uploadService.uploadSong(file);
        
        // Get current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userService.findUserByEmail(email);
        
        // Create sheet music
        SheetMusic sheetMusic = SheetMusic.builder()
                .title(title)
                .composer(composer)
                .description(description)
                .difficultyLevel(difficultyLevel)
                .fileUrl(fileUrl)
                .uploadedBy(user)
                .isPublic(true)
                .build();
        
        SheetMusic saved = sheetMusicRepository.save(sheetMusic);
        return ResponseEntity.ok(saved);
    }
}
