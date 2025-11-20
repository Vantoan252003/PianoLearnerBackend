package com.piano.learn.PianoLearn.controller.admin;

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
@RequestMapping("/api/admin/sheet-music")
public class AdminSheetMusicController {

    @Autowired
    private SheetMusicRepository sheetMusicRepository;

    @Autowired
    private UploadService uploadService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<SheetMusic>> listAll() {
        return ResponseEntity.ok(sheetMusicRepository.findAll());
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
            @RequestParam(value = "difficultyLevel", required = false) String difficultyLevel
    ) throws IOException {

        // upload file to cloud/storage
        String fileUrl = uploadService.uploadSong(file);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userService.findUserByEmail(email);

        SheetMusic s = SheetMusic.builder()
                .title(title)
                .composer(composer)
                .description(description)
                .difficultyLevel(difficultyLevel)
                .fileUrl(fileUrl)
                .uploadedBy(user)
                .isPublic(true)
                .build();

        SheetMusic saved = sheetMusicRepository.save(s);
        return ResponseEntity.ok(saved);
    }
}
