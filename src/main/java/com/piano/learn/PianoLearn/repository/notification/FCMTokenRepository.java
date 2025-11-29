package com.piano.learn.PianoLearn.repository.notification;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.piano.learn.PianoLearn.entity.notification.FCMToken;

@Repository
public interface FCMTokenRepository extends JpaRepository<FCMToken, Integer> {
    
    /**
     * Tìm token theo user ID và device ID
     */
    Optional<FCMToken> findByUser_UserIdAndDeviceId(Integer userId, String deviceId);
    
    /**
     * Tìm token theo token string
     */
    Optional<FCMToken> findByToken(String token);
    
    /**
     * Lấy tất cả token đang active của user
     */
    List<FCMToken> findByUser_UserIdAndIsActiveTrue(Integer userId);
    
    /**
     * Lấy tất cả token của user
     */
    List<FCMToken> findByUser_UserId(Integer userId);
    
    /**
     * Xóa token theo token string
     */
    void deleteByToken(String token);
    
    /**
     * Xóa tất cả token không active của user
     */
    void deleteByUser_UserIdAndIsActiveFalse(Integer userId);
}
