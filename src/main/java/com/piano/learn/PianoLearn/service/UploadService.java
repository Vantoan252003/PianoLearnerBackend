package com.piano.learn.PianoLearn.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.piano.learn.PianoLearn.service.common.CloudinaryService;

@Service
public class UploadService {

    @Autowired
    private CloudinaryService cloudinaryService;

    public String uploadAvatar(MultipartFile file) throws IOException {
        return cloudinaryService.uploadFile(file);
    }
}
