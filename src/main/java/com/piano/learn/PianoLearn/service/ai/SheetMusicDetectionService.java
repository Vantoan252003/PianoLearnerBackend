package com.piano.learn.PianoLearn.service.ai;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SheetMusicDetectionService {

    private final PdfToImageService pdfToImageService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String BASE_URL;
    private final String huggingFaceApiToken;

    @Autowired
    public SheetMusicDetectionService(PdfToImageService pdfToImageService) {
        this.pdfToImageService = pdfToImageService;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        this.BASE_URL = System.getenv("HUGGINGFACE_API_URL") != null ? 
            System.getenv("HUGGINGFACE_API_URL") : "https://vantoan252003-music-note-detection.hf.space";
        this.huggingFaceApiToken = System.getenv("HUGGINGFACE_API_TOKEN");
    }

    public SheetValidationResult validatePdfSheetMusic(MultipartFile pdfFile) {
        try {
            List<byte[]> imageBytesList = pdfToImageService.convertFirstTwoPagesToImageBytes(pdfFile);
            if (imageBytesList.isEmpty()) {
                return new SheetValidationResult(false, 0, "Không thể đọc file PDF");
            }

            int totalMusicNotes = 0;
            boolean isSheet = false;

            for (byte[] imageBytes : imageBytesList) {
                DetectionResult result = callGradioApi(imageBytes);
                if (result != null) {
                    totalMusicNotes += result.musicNotes;
                    if (result.isSheet) isSheet = true;
                }
            }

            String message = isSheet
                ? "Sheet nhạc hợp lệ (" + totalMusicNotes + " nốt)"
                : "Không phải sheet nhạc (" + totalMusicNotes + " nốt)";
            return new SheetValidationResult(isSheet, totalMusicNotes, message);

        } catch (Exception e) {
            e.printStackTrace();
            return new SheetValidationResult(false, 0, "Lỗi: " + e.getMessage());
        }
    }

    public SheetValidationResult validateImageSheetMusic(MultipartFile imageFile) {
        try {
            DetectionResult result = callGradioApi(imageFile.getBytes());
            if (result == null) {
                return new SheetValidationResult(false, 0, "Lỗi gọi API AI");
            }

            String message = result.isSheet
                ? "Sheet nhạc hợp lệ (" + result.musicNotes + " nốt)"
                : "Không phải sheet nhạc (" + result.musicNotes + " nốt)";
            return new SheetValidationResult(result.isSheet, result.musicNotes, message);

        } catch (Exception e) {
            e.printStackTrace();
            return new SheetValidationResult(false, 0, "Lỗi: " + e.getMessage());
        }
    }

    private DetectionResult callGradioApi(byte[] imageBytes) {
        try {
            String uploadUrl = BASE_URL + "/gradio_api/upload";
            
            HttpHeaders uploadHeaders = new HttpHeaders();
            uploadHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);

            ByteArrayResource resource = new ByteArrayResource(imageBytes) {
                @Override
                public String getFilename() {
                    return "image.png";
                }
            };

            MultiValueMap<String, Object> uploadBody = new LinkedMultiValueMap<>();
            uploadBody.add("files", resource);

            HttpEntity<MultiValueMap<String, Object>> uploadRequest = new HttpEntity<>(uploadBody, uploadHeaders);
            ResponseEntity<String> uploadResponse = restTemplate.postForEntity(uploadUrl, uploadRequest, String.class);
            
            JsonNode uploadResult = objectMapper.readTree(uploadResponse.getBody());
            if (!uploadResult.isArray() || uploadResult.size() == 0) {
                return null;
            }
            String filePath = uploadResult.get(0).asText();

            String callUrl = BASE_URL + "/gradio_api/call/detect_sheet";
            
            String payload = objectMapper.writeValueAsString(new Object[]{
                new Object[]{
                    java.util.Map.of("path", filePath, "orig_name", "image.png", "size", imageBytes.length, "mime_type", "image/png"),
                    java.util.Map.of("path", filePath, "orig_name", "image.png", "size", imageBytes.length, "mime_type", "image/png")
                }
            });
            payload = "{\"data\":" + payload.substring(1, payload.length() - 1) + "}";

            HttpHeaders callHeaders = new HttpHeaders();
            callHeaders.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> callRequest = new HttpEntity<>(payload, callHeaders);
            ResponseEntity<String> callResponse = restTemplate.postForEntity(callUrl, callRequest, String.class);
            
            JsonNode callResult = objectMapper.readTree(callResponse.getBody());
            if (!callResult.has("event_id")) {
                return null;
            }
            
            String eventId = callResult.get("event_id").asText();
            String resultUrl = callUrl + "/" + eventId;

            return restTemplate.execute(resultUrl, HttpMethod.GET, req -> {
                req.getHeaders().add("Accept", "text/event-stream");
            }, resp -> {
                java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(resp.getBody(), java.nio.charset.StandardCharsets.UTF_8)
                );
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("data: [")) {
                        String jsonPart = line.substring(6).trim();
                        JsonNode arr = objectMapper.readTree(jsonPart);
                        if (arr.isArray() && arr.size() > 0) {
                            JsonNode data = arr.get(0);
                            int notes = data.has("music_notes") ? data.get("music_notes").asInt() : 0;
                            boolean isSheet = data.has("is_sheet") && data.get("is_sheet").asBoolean();
                            return new DetectionResult(notes, isSheet);
                        }
                    }
                }
                return null;
            });

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static class DetectionResult {
        int musicNotes;
        boolean isSheet;
        DetectionResult(int n, boolean s) { this.musicNotes = n; this.isSheet = s; }
    }

    public static class SheetValidationResult {
        private final boolean valid;
        private final int musicNotesCount;
        private final String message;

        public SheetValidationResult(boolean v, int n, String m) {
            this.valid = v; this.musicNotesCount = n; this.message = m;
        }
        public boolean isValid() { return valid; }
        public int getMusicNotesCount() { return musicNotesCount; }
        public String getMessage() { return message; }
    }
}