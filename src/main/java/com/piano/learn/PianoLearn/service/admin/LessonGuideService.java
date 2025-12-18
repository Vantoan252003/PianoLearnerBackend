package com.piano.learn.PianoLearn.service.admin;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.itextpdf.html2pdf.HtmlConverter;
import com.piano.learn.PianoLearn.entity.lesson.Lesson;
import com.piano.learn.PianoLearn.entity.lesson.LessonGuide;
import com.piano.learn.PianoLearn.repository.lesson.LessonGuideRepository;
import com.piano.learn.PianoLearn.repository.lesson.LessonRepository;
import com.piano.learn.PianoLearn.service.common.CloudinaryService;

@Service
public class LessonGuideService {
    
    @Autowired
    private LessonGuideRepository lessonGuideRepository;
    
    @Autowired
    private LessonRepository lessonRepository;
    
    @Autowired
    private CloudinaryService cloudinaryService;
    
    public LessonGuide getGuideByLessonId(Integer lessonId) {
        return lessonGuideRepository.findByLesson_LessonId(lessonId).orElse(null);
    }
    
    public String saveGuideAndUploadPDF(Integer lessonId, String htmlContent) throws Exception {
        System.out.println("=== Starting saveGuideAndUploadPDF for lesson: " + lessonId);
        
        try {
            // Find or create lesson guide
            System.out.println("Step 1: Finding lesson...");
            Lesson lesson = lessonRepository.findById(lessonId)
                    .orElseThrow(() -> new RuntimeException("Lesson not found"));
            System.out.println("Found lesson: " + lesson.getLessonTitle());
            
            System.out.println("Step 2: Finding or creating guide...");
            LessonGuide guide = lessonGuideRepository.findByLesson_LessonId(lessonId)
                    .orElse(LessonGuide.builder()
                            .lesson(lesson)
                            .build());
            
            guide.setContent(htmlContent);
            lessonGuideRepository.save(guide);
            System.out.println("Guide saved successfully");
            
            // Generate PDF from HTML
            System.out.println("Step 3: Converting HTML to PDF...");
            byte[] pdfBytes = convertHtmlToPdf(htmlContent);
            System.out.println("PDF generated, size: " + pdfBytes.length + " bytes");
            
            // Create custom MultipartFile from PDF bytes
            System.out.println("Step 4: Creating MultipartFile...");
            MultipartFile pdfFile = new ByteArrayMultipartFile(
                    "file",
                    "lesson_guide_" + lessonId + ".pdf",
                    "application/pdf",
                    pdfBytes
            );
            
            System.out.println("Step 5: Uploading to Cloudinary...");
            String pdfUrl = cloudinaryService.uploadRawFile(pdfFile);
            System.out.println("Upload successful: " + pdfUrl);
            
            // Update lesson's videoUrl with PDF URL
            System.out.println("Step 6: Updating lesson videoUrl...");
            lesson.setVideoUrl(pdfUrl);
            lessonRepository.save(lesson);
            System.out.println("Lesson updated successfully");
            
            return pdfUrl;
        } catch (Exception e) {
            System.err.println("ERROR in saveGuideAndUploadPDF: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    // Custom MultipartFile implementation for byte arrays
    private static class ByteArrayMultipartFile implements MultipartFile {
        private final String name;
        private final String originalFilename;
        private final String contentType;
        private final byte[] content;
        
        public ByteArrayMultipartFile(String name, String originalFilename, String contentType, byte[] content) {
            this.name = name;
            this.originalFilename = originalFilename;
            this.contentType = contentType;
            this.content = content;
        }
        
        @Override
        public String getName() {
            return name;
        }
        
        @Override
        public String getOriginalFilename() {
            return originalFilename;
        }
        
        @Override
        public String getContentType() {
            return contentType;
        }
        
        @Override
        public boolean isEmpty() {
            return content.length == 0;
        }
        
        @Override
        public long getSize() {
            return content.length;
        }
        
        @Override
        public byte[] getBytes() throws IOException {
            return content;
        }
        
        @Override
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(content);
        }
        
        @Override
        public void transferTo(File dest) throws IOException, IllegalStateException {
            try (FileOutputStream fos = new FileOutputStream(dest)) {
                fos.write(content);
            }
        }
    }
    
    private byte[] convertHtmlToPdf(String htmlContent) throws Exception {
        // Wrap HTML content with proper structure and styling
        String fullHtml = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<style>" +
                "body { font-family: 'Times New Roman', serif; padding: 40px; line-height: 1.6; }" +
                "h1 { color: #333; font-size: 28px; margin-bottom: 20px; }" +
                "h2 { color: #555; font-size: 24px; margin-top: 30px; margin-bottom: 15px; }" +
                "h3 { color: #666; font-size: 20px; margin-top: 25px; margin-bottom: 12px; }" +
                "p { margin-bottom: 12px; text-align: justify; }" +
                "ul, ol { margin-left: 30px; margin-bottom: 15px; }" +
                "li { margin-bottom: 8px; }" +
                "img { max-width: 100%; height: auto; margin: 20px 0; }" +
                "blockquote { border-left: 4px solid #ccc; padding-left: 20px; margin: 20px 0; font-style: italic; }" +
                "code { background-color: #f4f4f4; padding: 2px 6px; border-radius: 3px; font-family: 'Courier New', monospace; }" +
                "pre { background-color: #f4f4f4; padding: 15px; border-radius: 5px; overflow-x: auto; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                htmlContent +
                "</body>" +
                "</html>";
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        HtmlConverter.convertToPdf(fullHtml, outputStream);
        
        return outputStream.toByteArray();
    }
}
