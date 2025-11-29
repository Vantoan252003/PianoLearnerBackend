package com.piano.learn.PianoLearn.dto.notification;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO để nhận FCM token từ Flutter app
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FCMTokenRequest {
    
    @NotBlank(message = "Token không được để trống")
    @Size(max = 500, message = "Token quá dài")
    private String token;
    
    @Size(max = 255, message = "Device ID quá dài")
    private String deviceId;
    
    @Size(max = 50, message = "Device type quá dài")
    private String deviceType; // android, ios, web
    
    @Size(max = 255, message = "Device name quá dài")
    private String deviceName;
}
