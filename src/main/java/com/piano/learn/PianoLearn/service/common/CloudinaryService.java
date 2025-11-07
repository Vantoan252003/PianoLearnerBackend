package com.piano.learn.PianoLearn.service.common;

import java.io.IOException;
import java.util.Map;

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
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap("resource_type", "auto"));
        return (String) uploadResult.get("secure_url");
    }
}

