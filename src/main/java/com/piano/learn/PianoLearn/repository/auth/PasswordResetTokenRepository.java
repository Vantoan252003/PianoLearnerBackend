package com.piano.learn.PianoLearn.repository.auth;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.piano.learn.PianoLearn.entity.auth.PasswordResetToken;
import com.piano.learn.PianoLearn.entity.auth.User;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {
    
    /**
     * Tìm token hợp lệ (chưa dùng, chưa hết hạn) của user
     */
    Optional<PasswordResetToken> findByUserAndOtpCodeAndIsUsedFalseAndExpiresAtAfter(
        User user, 
        String otpCode, 
        LocalDateTime currentTime
    );
    
    /**
     * Tìm tất cả token của user
     */
    List<PasswordResetToken> findByUser(User user);
    
    /**
     * Tìm token chưa dùng của user
     */
    List<PasswordResetToken> findByUserAndIsUsedFalse(User user);
    
    /**
     * Xóa token đã hết hạn
     */
    void deleteByExpiresAtBefore(LocalDateTime dateTime);
    
    /**
     * Đếm số token chưa dùng của user trong khoảng thời gian
     */
    long countByUserAndIsUsedFalseAndCreatedAtAfter(User user, LocalDateTime since);
}
