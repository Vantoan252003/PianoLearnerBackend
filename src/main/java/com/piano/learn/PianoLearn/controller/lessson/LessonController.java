package com.piano.learn.PianoLearn.controller.lessson;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.piano.learn.PianoLearn.dto.lesson.LessonWithUnlockStatusDTO;
import com.piano.learn.PianoLearn.entity.lesson.Lesson;
import com.piano.learn.PianoLearn.repository.lesson.LessonRepository;
import com.piano.learn.PianoLearn.service.UserService;
import com.piano.learn.PianoLearn.service.lessons.LessonService;

@RestController
@RequestMapping("/api/auth/course")
public class LessonController {

    private final LessonService lessonService;
    private final UserService userService;
    
    @Autowired
    private LessonRepository lessonRepository;

    public LessonController(LessonService lessonService, UserService userService) {
        this.lessonService = lessonService;
        this.userService = userService;
    }
    
    @GetMapping("/lesson/{courseId}/with-status")
    public ResponseEntity<List<LessonWithUnlockStatusDTO>> getLessonsWithUnlockStatus(@PathVariable Integer courseId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Integer userId = userService.findUserByEmail(email).getUserId();
        
        List<LessonWithUnlockStatusDTO> lessons = lessonService.getLessonsWithUnlockStatus(userId, courseId);
        return ResponseEntity.ok(lessons);
    }
    
    /**
     * GET /api/auth/course/lesson/stream/{lessonId} - Stream lesson guide PDF
     */
    @GetMapping("/lesson/stream/{lessonId}")
    public ResponseEntity<InputStreamResource> streamLessonGuide(@PathVariable Integer lessonId) {
        try {
            // Kiểm tra lesson có tồn tại không
            Optional<Lesson> lessonOpt = lessonRepository.findById(lessonId);
            if (!lessonOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            Lesson lesson = lessonOpt.get();
            
            // Kiểm tra có videoUrl (PDF guide) không
            String pdfUrl = lesson.getVideoUrl();
            if (pdfUrl == null || pdfUrl.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            
            // Stream file từ Cloudinary URL
            URL url = new URL(pdfUrl);
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
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + lesson.getLessonTitle() + "_guide.pdf\"");
            headers.setCacheControl("max-age=3600"); // Cache 1 hour
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
                    
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * GET /api/auth/course/lesson/download/{lessonId} - Download lesson guide PDF
     */
    @GetMapping("/lesson/download/{lessonId}")
    public ResponseEntity<InputStreamResource> downloadLessonGuide(@PathVariable Integer lessonId) {
        try {
            Optional<Lesson> lessonOpt = lessonRepository.findById(lessonId);
            if (!lessonOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            Lesson lesson = lessonOpt.get();
            
            String pdfUrl = lesson.getVideoUrl();
            if (pdfUrl == null || pdfUrl.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            
            // Stream file từ Cloudinary
            URL url = new URL(pdfUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            
            String contentType = connection.getContentType();
            if (contentType == null) {
                contentType = "application/pdf";
            }
            
            InputStream inputStream = connection.getInputStream();
            InputStreamResource resource = new InputStreamResource(inputStream);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + lesson.getLessonTitle() + "_guide.pdf\"");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
                    
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
