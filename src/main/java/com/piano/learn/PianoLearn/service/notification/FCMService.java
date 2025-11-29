package com.piano.learn.PianoLearn.service.notification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.piano.learn.PianoLearn.entity.auth.User;
import com.piano.learn.PianoLearn.entity.notification.FCMToken;
import com.piano.learn.PianoLearn.repository.notification.FCMTokenRepository;

/**
 * Service để gửi thông báo qua Firebase Cloud Messaging
 */
@Service
public class FCMService {
    
    @Autowired
    private FCMTokenRepository fcmTokenRepository;
    
    /**
     * Lưu hoặc cập nhật FCM token cho user
     */
    @Transactional
    public FCMToken saveOrUpdateToken(Integer userId, String token, String deviceId, String deviceType, String deviceName) {
        // Tìm token cũ nếu có
        FCMToken fcmToken = fcmTokenRepository.findByUser_UserIdAndDeviceId(userId, deviceId)
                .orElse(null);
        
        if (fcmToken != null) {
            // Update token cũ
            fcmToken.setToken(token);
            fcmToken.setDeviceType(deviceType);
            fcmToken.setDeviceName(deviceName);
            fcmToken.setIsActive(true);
            fcmToken.setUpdatedAt(LocalDateTime.now());
            fcmToken.setLastUsedAt(LocalDateTime.now());
        } else {
            // Tạo token mới
            User user = User.builder().userId(userId).build();
            fcmToken = FCMToken.builder()
                    .user(user)
                    .token(token)
                    .deviceId(deviceId)
                    .deviceType(deviceType)
                    .deviceName(deviceName)
                    .isActive(true)
                    .build();
        }
        
        return fcmTokenRepository.save(fcmToken);
    }
    
    /**
     * Xóa token
     */
    @Transactional
    public boolean deleteToken(String token) {
        try {
            fcmTokenRepository.deleteByToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Gửi thông báo đến một user cụ thể
     */
    @Async
    public CompletableFuture<Void> sendNotificationToUser(Integer userId, String title, String body, String imageUrl) {
        List<FCMToken> tokens = fcmTokenRepository.findByUser_UserIdAndIsActiveTrue(userId);
        
        for (FCMToken fcmToken : tokens) {
            try {
                sendNotification(fcmToken.getToken(), title, body, imageUrl);
            } catch (Exception e) {
                System.err.println("Error sending notification to token " + fcmToken.getToken() + ": " + e.getMessage());
                // Đánh dấu token không còn active nếu gặp lỗi
                fcmToken.setIsActive(false);
                fcmTokenRepository.save(fcmToken);
            }
        }
        
        return CompletableFuture.completedFuture(null);
    }
    
    /**
     * Gửi thông báo đến một token cụ thể
     */
    public void sendNotification(String token, String title, String body, String imageUrl) throws Exception {
        Notification.Builder notificationBuilder = Notification.builder()
                .setTitle(title)
                .setBody(body);
        
        // Thêm image nếu có
        if (imageUrl != null && !imageUrl.isEmpty()) {
            notificationBuilder.setImage(imageUrl);
        }
        
        Message message = Message.builder()
                .setToken(token)
                .setNotification(notificationBuilder.build())
                .build();
        
        String response = FirebaseMessaging.getInstance().send(message);
        System.out.println("Successfully sent message: " + response);
    }
    
    /**
     * Gửi thông báo với data payload
     */
    @Async
    public CompletableFuture<Void> sendNotificationWithData(Integer userId, String title, String body, 
                                                             String imageUrl, java.util.Map<String, String> data) {
        List<FCMToken> tokens = fcmTokenRepository.findByUser_UserIdAndIsActiveTrue(userId);
        
        for (FCMToken fcmToken : tokens) {
            try {
                Notification.Builder notificationBuilder = Notification.builder()
                        .setTitle(title)
                        .setBody(body);
                
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    notificationBuilder.setImage(imageUrl);
                }
                
                Message.Builder messageBuilder = Message.builder()
                        .setToken(fcmToken.getToken())
                        .setNotification(notificationBuilder.build());
                
                // Thêm data nếu có
                if (data != null && !data.isEmpty()) {
                    messageBuilder.putAllData(data);
                }
                
                String response = FirebaseMessaging.getInstance().send(messageBuilder.build());
                System.out.println("Successfully sent message: " + response);
                
            } catch (Exception e) {
                System.err.println("Error sending notification: " + e.getMessage());
                fcmToken.setIsActive(false);
                fcmTokenRepository.save(fcmToken);
            }
        }
        
        return CompletableFuture.completedFuture(null);
    }
    
    /**
     * Lấy tất cả token của user
     */
    public List<FCMToken> getUserTokens(Integer userId) {
        return fcmTokenRepository.findByUser_UserId(userId);
    }
    
    /**
     * Lấy tất cả token active của user
     */
    public List<FCMToken> getUserActiveTokens(Integer userId) {
        return fcmTokenRepository.findByUser_UserIdAndIsActiveTrue(userId);
    }
}
