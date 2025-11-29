package com.piano.learn.PianoLearn.controller.notification;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.piano.learn.PianoLearn.dto.notification.FCMTokenRequest;
import com.piano.learn.PianoLearn.entity.notification.FCMToken;
import com.piano.learn.PianoLearn.service.UserService;
import com.piano.learn.PianoLearn.service.notification.FCMService;

import jakarta.validation.Valid;

/**
 * Controller để quản lý FCM tokens từ Flutter app
 */
@RestController
@RequestMapping("/api/auth/fcm")
public class FCMController {
    
    @Autowired
    private FCMService fcmService;
    
    @Autowired
    private UserService userService;
    
    /**
     * POST /api/auth/fcm/token
     * Lưu hoặc cập nhật FCM token từ Flutter app
     */
    @PostMapping("/token")
    public ResponseEntity<String> saveToken(@Valid @RequestBody FCMTokenRequest request) {
        Integer userId = getCurrentUserId();
        
        fcmService.saveOrUpdateToken(
            userId, 
            request.getToken(), 
            request.getDeviceId(), 
            request.getDeviceType(), 
            request.getDeviceName()
        );
        
        return ResponseEntity.ok("FCM token saved successfully");
    }
    
    /**
     * DELETE /api/auth/fcm/token
     * Xóa FCM token (khi user logout hoặc uninstall app)
     */
    @DeleteMapping("/token")
    public ResponseEntity<String> deleteToken(@RequestParam String token) {
        boolean deleted = fcmService.deleteToken(token);
        
        if (deleted) {
            return ResponseEntity.ok("FCM token deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * GET /api/auth/fcm/tokens
     * Lấy danh sách tất cả token của user hiện tại
     */
    @GetMapping("/tokens")
    public ResponseEntity<List<FCMToken>> getMyTokens() {
        Integer userId = getCurrentUserId();
        List<FCMToken> tokens = fcmService.getUserTokens(userId);
        return ResponseEntity.ok(tokens);
    }
    
    /**
     * GET /api/auth/fcm/active-tokens
     * Lấy danh sách token active của user hiện tại
     */
    @GetMapping("/active-tokens")
    public ResponseEntity<List<FCMToken>> getMyActiveTokens() {
        Integer userId = getCurrentUserId();
        List<FCMToken> tokens = fcmService.getUserActiveTokens(userId);
        return ResponseEntity.ok(tokens);
    }
    
    private Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userService.findUserByEmail(email).getUserId();
    }
}
