package com.piano.learn.PianoLearn.controller.admin;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.piano.learn.PianoLearn.entity.auth.User;
import com.piano.learn.PianoLearn.entity.sheet.SheetMusic;
import com.piano.learn.PianoLearn.repository.auth.UserRepository;
import com.piano.learn.PianoLearn.repository.sheet.SheetMusicRepository;
import com.piano.learn.PianoLearn.service.UploadService;
import com.piano.learn.PianoLearn.service.UserService;

@RestController
@RequestMapping("/api/admin/sheet-music")
public class AdminSheetMusicController {

    @Autowired
    private SheetMusicRepository sheetMusicRepository;

    @Autowired
    private UploadService uploadService;

    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<SheetMusic>> listAll() {
        return ResponseEntity.ok(sheetMusicRepository.findAll());
    }
    
    /**
     * GET /api/admin/sheet-music/{id} - Lấy chi tiết một sheet music
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getSheetById(@PathVariable Integer id) {
        Optional<SheetMusic> sheet = sheetMusicRepository.findById(id);
        if (sheet.isPresent()) {
            return ResponseEntity.ok(sheet.get());
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * POST /api/admin/sheet-music
     * multipart form-data: file (MultipartFile), title, composer (optional), description (optional), difficultyLevel (optional)
     */
    @PostMapping
    public ResponseEntity<?> uploadSheet(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam(value = "composer", required = false) String composer,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "difficultyLevel", required = false) String difficultyLevel,
            @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail,
            @RequestParam(value = "isPublic", required = false, defaultValue = "true") Boolean isPublic,
            @RequestParam(value = "uploadedBy", required = false) Integer uploadedBy
    ) throws IOException {

        // upload file to cloud/storage
        String fileUrl = uploadService.uploadSong(file);
        
        // upload thumbnail if provided
        String thumbnailUrl = null;
        if (thumbnail != null && !thumbnail.isEmpty()) {
            thumbnailUrl = uploadService.uploadAvatar(thumbnail);
        }

        User uploader = null;
        if (uploadedBy != null) {
            uploader = userRepository.findById(uploadedBy).orElse(null);
        } else {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            uploader = userService.findUserByEmail(email);
        }

        SheetMusic s = SheetMusic.builder()
                .title(title)
                .composer(composer)
                .description(description)
                .difficultyLevel(difficultyLevel)
                .fileUrl(fileUrl)
                .thumbnailUrl(thumbnailUrl)
                .uploadedBy(uploader)
                .isPublic(isPublic)
                .build();

        SheetMusic saved = sheetMusicRepository.save(s);
        return ResponseEntity.ok(saved);
    }
    
    /**
     * PUT /api/admin/sheet-music/{id} - Cập nhật sheet music
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSheet(
            @PathVariable Integer id,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam(value = "composer", required = false) String composer,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "difficultyLevel", required = false) String difficultyLevel,
            @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail,
            @RequestParam(value = "isPublic", required = false, defaultValue = "true") Boolean isPublic,
            @RequestParam(value = "uploadedBy", required = false) Integer uploadedBy
    ) throws IOException {
        
        Optional<SheetMusic> existing = sheetMusicRepository.findById(id);
        if (!existing.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        SheetMusic sheet = existing.get();
        
        // Update file if provided
        if (file != null && !file.isEmpty()) {
            String fileUrl = uploadService.uploadSong(file);
            sheet.setFileUrl(fileUrl);
        }
        
        // Update thumbnail if provided
        if (thumbnail != null && !thumbnail.isEmpty()) {
            String thumbnailUrl = uploadService.uploadAvatar(thumbnail);
            sheet.setThumbnailUrl(thumbnailUrl);
        }
        
        // Update uploader if provided
        if (uploadedBy != null) {
            User uploader = userRepository.findById(uploadedBy).orElse(null);
            sheet.setUploadedBy(uploader);
        }
        
        // Update other fields
        sheet.setTitle(title);
        sheet.setComposer(composer);
        sheet.setDescription(description);
        sheet.setDifficultyLevel(difficultyLevel);
        sheet.setIsPublic(isPublic);
        
        SheetMusic updated = sheetMusicRepository.save(sheet);
        return ResponseEntity.ok(updated);
    }
    
    /**
     * DELETE /api/admin/sheet-music/{id} - Xóa sheet music
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSheet(@PathVariable Integer id) {
        if (sheetMusicRepository.existsById(id)) {
            sheetMusicRepository.deleteById(id);
            return ResponseEntity.ok("Sheet music deleted successfully");
        }
        return ResponseEntity.notFound().build();
    }
    
    /**
     * PUT /api/admin/sheet-music/{id}/view - Tăng view count
     */
    @PutMapping("/{id}/view")
    public ResponseEntity<?> incrementView(@PathVariable Integer id) {
        Optional<SheetMusic> sheet = sheetMusicRepository.findById(id);
        if (sheet.isPresent()) {
            SheetMusic s = sheet.get();
            s.setViewCount(s.getViewCount() + 1);
            sheetMusicRepository.save(s);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    /**
     * PUT /api/admin/sheet-music/{id}/download - Tăng download count
     */
    @PutMapping("/{id}/download")
    public ResponseEntity<?> incrementDownload(@PathVariable Integer id) {
        Optional<SheetMusic> sheet = sheetMusicRepository.findById(id);
        if (sheet.isPresent()) {
            SheetMusic s = sheet.get();
            s.setDownloadCount(s.getDownloadCount() + 1);
            sheetMusicRepository.save(s);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
