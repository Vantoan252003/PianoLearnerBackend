package com.piano.learn.PianoLearn.service.common;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Service
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public CloudinaryService() {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dofpypbi9",
                "api_key", "431576587161232",
                "api_secret", "IBD02yVTZSEtVl_MsrNFG_xLYkk"
        ));
    }

    public String uploadFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String publicId = UUID.randomUUID().toString() + extension;
        
        @SuppressWarnings("unchecked")
        Map<String, Object> uploadResult = (Map<String, Object>) cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap("resource_type", "auto", "public_id", publicId));
        return (String) uploadResult.get("secure_url");
    }

    public String uploadImage(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String publicId = UUID.randomUUID().toString() + extension;
        
        @SuppressWarnings("unchecked")
        Map<String, Object> uploadResult = (Map<String, Object>) cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap("resource_type", "image", "public_id", publicId));
        return (String) uploadResult.get("secure_url");
    }

    public String uploadRawFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String publicId = UUID.randomUUID().toString() + extension;
        
        @SuppressWarnings("unchecked")
        Map<String, Object> uploadResult = (Map<String, Object>) cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap("resource_type", "raw", "public_id", publicId));
        return (String) uploadResult.get("secure_url");
    }

    public String generateSignedUrl(String publicId, String resourceType) {
        return cloudinary.url().resourceType(resourceType).type("upload").signed(true).generate(publicId);
    }
    
    public void updateAccessMode(String publicId, String resourceType, String accessMode) throws Exception {
        cloudinary.api().update(publicId, ObjectUtils.asMap("access_mode", accessMode, "resource_type", resourceType));
    }
}
