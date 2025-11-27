package com.piano.learn.PianoLearn.controller.sheet_music;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.piano.learn.PianoLearn.entity.auth.User;
import com.piano.learn.PianoLearn.entity.sheet.SheetMusic;
import com.piano.learn.PianoLearn.repository.sheet.SheetMusicRepository;
import com.piano.learn.PianoLearn.service.UploadService;
import com.piano.learn.PianoLearn.service.UserService;
import com.piano.learn.PianoLearn.service.ai.SheetMusicDetectionService;
import com.piano.learn.PianoLearn.service.ai.SheetMusicDetectionService.SheetValidationResult;

@RestController
@RequestMapping("/api/auth/sheet-music")
public class SheetMusicController {
    
    @Autowired
    private UploadService uploadService;
    
    @Autowired
    private SheetMusicRepository sheetMusicRepository;

    @Autowired
    private UserService userService;
    
    @Autowired
    private SheetMusicDetectionService sheetMusicDetectionService;
    
    // Các extension được chấp nhận
    private static final String[] ALLOWED_EXTENSIONS = {
        ".pdf", ".jpg", ".jpeg", ".png", ".mxl", ".musicxml", ".xml"
    };

    
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
     * Hỗ trợ: PDF, ảnh (JPG/PNG), MXL/MusicXML
     * - PDF: Convert sang ảnh → gọi Hugging Face API để check
     * - Ảnh: Gọi Hugging Face API để check trực tiếp
     * - MXL/MusicXML: Cho qua luôn (định dạng nhạc chuyên dụng)
     */
    @PostMapping
    public ResponseEntity<?> uploadSheetMusic(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam(value = "composer", required = false) String composer,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "difficultyLevel", required = false) String difficultyLevel
    ) throws IOException {
        
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename != null ? 
            originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase() : "";
        
        boolean isAllowedExtension = false;
        for (String ext : ALLOWED_EXTENSIONS) {
            if (fileExtension.equals(ext)) {
                isAllowedExtension = true;
                break;
            }
        }
        
        if (!isAllowedExtension) {
            return ResponseEntity.badRequest().body(
                java.util.Map.of(
                    "error", "INVALID_FILE_TYPE",
                    "message", "Chỉ chấp nhận file PDF, ảnh (JPG/PNG), hoặc MusicXML (.mxl)"
                )
            );
        }
        
        boolean isMusicXmlFile = fileExtension.equals(".mxl") || 
                                  fileExtension.equals(".musicxml") || 
                                  fileExtension.equals(".xml");
        
        String fileUrl;
        
        if (isMusicXmlFile) {
            fileUrl = uploadService.uploadSong(file);
        } else {
            String contentType = file.getContentType();
            SheetValidationResult validationResult;
            
            if (contentType != null && contentType.equals("application/pdf")) {
                validationResult = sheetMusicDetectionService.validatePdfSheetMusic(file);
            } else {
                validationResult = sheetMusicDetectionService.validateImageSheetMusic(file);
            }
            
            if (!validationResult.isValid()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    java.util.Map.of(
                        "error", "INVALID_SHEET_MUSIC",
                        "message", validationResult.getMessage(),
                        "musicNotesDetected", validationResult.getMusicNotesCount()
                    )
                );
            }
            
            fileUrl = uploadService.uploadSong(file);
        }
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userService.findUserByEmail(email);
        
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
    
    /**
     * GET /api/auth/sheet-music/my-sheets - Lấy tất cả sheet music của user hiện tại
     */
    @GetMapping("/my-sheets")
    public ResponseEntity<List<SheetMusic>> getMySheets() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userService.findUserByEmail(email);
        
        List<SheetMusic> mySheets = sheetMusicRepository.findByUploadedBy_UserId(user.getUserId());
        return ResponseEntity.ok(mySheets);
    }
    
    /**
     * DELETE /api/auth/sheet-music/{id} - Xóa sheet music của chính user (chỉ được xóa sheet do mình upload)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMySheet(@PathVariable Integer id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userService.findUserByEmail(email);
        
        Optional<SheetMusic> sheetOpt = sheetMusicRepository.findById(id);
        if (!sheetOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sheet music not found");
        }
        
        SheetMusic sheet = sheetOpt.get();
        
        // Kiểm tra xem user có phải là người upload sheet này không
        if (sheet.getUploadedBy() == null || !sheet.getUploadedBy().getUserId().equals(user.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can only delete your own sheet music");
        }
        
        sheetMusicRepository.deleteById(id);
        return ResponseEntity.ok("Sheet music deleted successfully");
    }
    
    @GetMapping("/stream/{id}")
    public ResponseEntity<InputStreamResource> streamSheetMusic(@PathVariable Integer id) {
        try {
            // Kiểm tra sheet music có tồn tại không
            Optional<SheetMusic> sheetOpt = sheetMusicRepository.findById(id);
            if (!sheetOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            SheetMusic sheet = sheetOpt.get();
            
            // Kiểm tra quyền truy cập (chỉ sheet public hoặc của chính user)
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            User user = userService.findUserByEmail(email);
            
            boolean hasAccess = sheet.getIsPublic() || 
                              (sheet.getUploadedBy() != null && sheet.getUploadedBy().getUserId().equals(user.getUserId()));
            
            if (!hasAccess) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            // Tăng view count
            sheet.setViewCount((sheet.getViewCount() != null ? sheet.getViewCount() : 0) + 1);
            sheetMusicRepository.save(sheet);
            
            // Lấy file từ Cloudinary (secure_url đã được lưu khi upload)
            String fileUrl = sheet.getFileUrl();
            if (fileUrl == null || fileUrl.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            URL url = new URL(fileUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            
            // Lấy content type
            String contentType = connection.getContentType();
            if (contentType == null) {
                contentType = "application/pdf"; // default
            }
            
            // Stream file về client
            InputStream inputStream = connection.getInputStream();
            InputStreamResource resource = new InputStreamResource(inputStream);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + sheet.getTitle() + ".pdf\"");
            headers.setCacheControl("max-age=3600"); // Cache 1 hour
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
                    
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * GET /api/auth/sheet-music/download/{id} - Download sheet music (tăng download count)
     */
    @GetMapping("/download/{id}")
    public ResponseEntity<InputStreamResource> downloadSheetMusic(@PathVariable Integer id) {
        try {
            Optional<SheetMusic> sheetOpt = sheetMusicRepository.findById(id);
            if (!sheetOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            SheetMusic sheet = sheetOpt.get();
            
            // Kiểm tra quyền truy cập
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            User user = userService.findUserByEmail(email);
            
            boolean hasAccess = sheet.getIsPublic() || 
                              (sheet.getUploadedBy() != null && sheet.getUploadedBy().getUserId().equals(user.getUserId()));
            
            if (!hasAccess) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            // Tăng download count
            sheet.setDownloadCount((sheet.getDownloadCount() != null ? sheet.getDownloadCount() : 0) + 1);
            sheetMusicRepository.save(sheet);
            
            // Stream file (secure_url đã được lưu khi upload)
            String fileUrl = sheet.getFileUrl();
            if (fileUrl == null || fileUrl.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            // Sử dụng trực tiếp secure_url đã lưu trong DB
            URL url = new URL(fileUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            
            String contentType = connection.getContentType();
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            
            InputStream inputStream = connection.getInputStream();
            InputStreamResource resource = new InputStreamResource(inputStream);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + sheet.getTitle() + ".pdf\"");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
                    
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
}
