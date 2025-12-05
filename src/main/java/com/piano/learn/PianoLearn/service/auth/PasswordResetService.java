package com.piano.learn.PianoLearn.service.auth;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.piano.learn.PianoLearn.entity.auth.PasswordResetToken;
import com.piano.learn.PianoLearn.entity.auth.User;
import com.piano.learn.PianoLearn.repository.auth.PasswordResetTokenRepository;
import com.piano.learn.PianoLearn.repository.auth.UserRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Service xử lý reset password
 */
@Service
@Slf4j
public class PasswordResetService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordResetTokenRepository tokenRepository;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    private static final int MAX_REQUESTS_PER_HOUR = 3;
    private static final SecureRandom random = new SecureRandom();
    
    /**
     * Bước 1: Gửi OTP qua email
     */
    @Transactional
    public void sendPasswordResetOTP(String email) {
        // Tìm user
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Email không tồn tại trong hệ thống"));
        
        // Kiểm tra rate limit (tối đa 3 lần/giờ)
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        long recentRequests = tokenRepository.countByUserAndIsUsedFalseAndCreatedAtAfter(user, oneHourAgo);
        
        if (recentRequests >= MAX_REQUESTS_PER_HOUR) {
            throw new RuntimeException("Bạn đã yêu cầu quá nhiều lần. Vui lòng thử lại sau 1 giờ.");
        }
        
        // Vô hiệu hóa các OTP cũ chưa dùng
        List<PasswordResetToken> oldTokens = tokenRepository.findByUserAndIsUsedFalse(user);
        for (PasswordResetToken token : oldTokens) {
            token.setIsUsed(true);
        }
        tokenRepository.saveAll(oldTokens);
        
        // Tạo OTP 6 số
        String otpCode = generateOTP();
        
        // Lưu token
        PasswordResetToken token = PasswordResetToken.builder()
            .user(user)
            .otpCode(otpCode)
            .expiresAt(LocalDateTime.now().plusMinutes(5))
            .isUsed(false)
            .build();
        
        tokenRepository.save(token);
        
        // Gửi email
        try {
            emailService.sendPasswordResetOTP(email, otpCode, user.getFullName());
            log.info("OTP sent successfully to email: {}", email);
        } catch (Exception e) {
            log.error("Failed to send OTP email to: {}", email, e);
            throw new RuntimeException("Không thể gửi email. Vui lòng thử lại sau.");
        }
    }
    
    /**
     * Bước 2: Xác thực OTP
     */
    @Transactional
    public boolean verifyOTP(String email, String otpCode) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Email không tồn tại"));
        
        PasswordResetToken token = tokenRepository
            .findByUserAndOtpCodeAndIsUsedFalseAndExpiresAtAfter(user, otpCode, LocalDateTime.now())
            .orElse(null);
        
        if (token == null) {
            return false;
        }
        
        // OTP hợp lệ nhưng chưa đánh dấu là đã dùng (cho phép đổi mật khẩu)
        return true;
    }
    
    /**
     * Bước 3: Đặt lại mật khẩu
     */
    @Transactional
    public void resetPassword(String email, String otpCode, String newPassword) {
        // Validate input
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new RuntimeException("Mật khẩu mới không được để trống");
        }
        
        if (newPassword.length() < 6) {
            throw new RuntimeException("Mật khẩu phải có ít nhất 6 ký tự");
        }
        
        // Tìm user
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Email không tồn tại"));
        
        // Tìm và verify OTP
        PasswordResetToken token = tokenRepository
            .findByUserAndOtpCodeAndIsUsedFalseAndExpiresAtAfter(user, otpCode, LocalDateTime.now())
            .orElseThrow(() -> new RuntimeException("Mã OTP không hợp lệ hoặc đã hết hạn"));
        
        // Đổi mật khẩu
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        // Đánh dấu token đã dùng
        token.setIsUsed(true);
        tokenRepository.save(token);
        
        log.info("Password reset successfully for user: {}", email);
    }
    
    /**
     * Tạo OTP 6 số ngẫu nhiên
     */
    private String generateOTP() {
        int otp = 100000 + random.nextInt(900000); // 100000-999999
        return String.valueOf(otp);
    }
    
    /**
     * Scheduled job: Xóa các token đã hết hạn (chạy mỗi ngày lúc 2h sáng)
     */
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void cleanupExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        tokenRepository.deleteByExpiresAtBefore(now);
        log.info("Cleaned up expired password reset tokens");
    }
}
