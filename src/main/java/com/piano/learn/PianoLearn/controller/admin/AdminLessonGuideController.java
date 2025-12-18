package com.piano.learn.PianoLearn.controller.admin;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.piano.learn.PianoLearn.dto.LessonGuideRequest;
import com.piano.learn.PianoLearn.entity.lesson.LessonGuide;
import com.piano.learn.PianoLearn.service.admin.LessonGuideService;

@Controller
public class AdminLessonGuideController {
    
    @Autowired
    private LessonGuideService lessonGuideService;
    
    // Show the page
    @GetMapping("/admin/lesson-guide")
    public String showLessonGuidePage() {
        System.out.println("Showing lesson guide page");
        return "admin/lesson_guide";
    }
    
    // Get existing guide for a lesson (API)
    @GetMapping("/api/admin/lesson-guide/{lessonId}")
    @ResponseBody
    @CrossOrigin(origins = "*")
    public ResponseEntity<?> getGuide(@PathVariable Integer lessonId) {
        System.out.println("=== GET guide for lesson: " + lessonId);
        LessonGuide guide = lessonGuideService.getGuideByLessonId(lessonId);
        
        if (guide != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("content", guide.getContent());
            response.put("updatedAt", guide.getUpdatedAt());
            return ResponseEntity.ok(response);
        }
        
        return ResponseEntity.notFound().build();
    }
    
    // Save guide and upload PDF (API)
    @PostMapping("/api/admin/lesson-guide/save")
    @ResponseBody
    @CrossOrigin(origins = "*")
    public ResponseEntity<?> saveGuide(@RequestBody LessonGuideRequest request) {
        System.out.println("=== CONTROLLER: saveGuide called");
        System.out.println("Lesson ID: " + request.getLessonId());
        System.out.println("Content length: " + (request.getContent() != null ? request.getContent().length() : 0));
        
        try {
            String pdfUrl = lessonGuideService.saveGuideAndUploadPDF(
                    request.getLessonId(), 
                    request.getContent()
            );
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Success");
            response.put("pdfUrl", pdfUrl);
            
            System.out.println("=== CONTROLLER: Success!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("=== CONTROLLER: Error occurred");
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
}
