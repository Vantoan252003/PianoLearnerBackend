package com.piano.learn.PianoLearn.service.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * Service gửi email
 */
@Service
@Slf4j
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    /**
     * Gửi email text đơn giản
     */
    public void sendSimpleEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            
            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to: {}", to, e);
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }
    
    /**
     * Gửi email HTML
     */
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true = HTML
            
            mailSender.send(message);
            log.info("HTML email sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send HTML email to: {}", to, e);
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }
    
    /**
     * Gửi email OTP reset password
     */
    public void sendPasswordResetOTP(String to, String otpCode, String fullName) {
        String subject = "Mã OTP Đặt Lại Mật Khẩu - Piano Learner";
        
        String htmlContent = buildPasswordResetEmailTemplate(otpCode, fullName);
        
        sendHtmlEmail(to, subject, htmlContent);
    }
    
    /**
     * Template email đẹp cho OTP
     */
    private String buildPasswordResetEmailTemplate(String otpCode, String fullName) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body {
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                        background-color: #f4f4f4;
                        margin: 0;
                        padding: 0;
                    }
                    .container {
                        max-width: 600px;
                        margin: 40px auto;
                        background: white;
                        border-radius: 10px;
                        overflow: hidden;
                        box-shadow: 0 4px 6px rgba(0,0,0,0.1);
                    }
                    .header {
                        background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                        color: white;
                        padding: 30px;
                        text-align: center;
                    }
                    .header h1 {
                        margin: 0;
                        font-size: 24px;
                    }
                    .content {
                        padding: 40px 30px;
                    }
                    .otp-box {
                        background: #f8f9ff;
                        border: 2px dashed #667eea;
                        border-radius: 8px;
                        padding: 20px;
                        text-align: center;
                        margin: 30px 0;
                    }
                    .otp-code {
                        font-size: 32px;
                        font-weight: bold;
                        color: #667eea;
                        letter-spacing: 8px;
                        margin: 10px 0;
                    }
                    .warning {
                        background: #fff3cd;
                        border-left: 4px solid #ffc107;
                        padding: 15px;
                        margin: 20px 0;
                        border-radius: 4px;
                    }
                    .footer {
                        background: #f8f9fa;
                        padding: 20px;
                        text-align: center;
                        color: #6c757d;
                        font-size: 14px;
                    }
                    .icon {
                        font-size: 48px;
                        margin-bottom: 10px;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <div class="icon">🎹</div>
                        <h1>Piano Learner</h1>
                    </div>
                    <div class="content">
                        <h2>Xin chào %s! 👋</h2>
                        <p>Bạn đã yêu cầu đặt lại mật khẩu cho tài khoản Piano Learner của mình.</p>
                        <p>Đây là mã OTP của bạn:</p>
                        
                        <div class="otp-box">
                            <p style="margin: 0; color: #6c757d;">Mã OTP của bạn</p>
                            <div class="otp-code">%s</div>
                            <p style="margin: 0; color: #6c757d; font-size: 14px;">Có hiệu lực trong 5 phút</p>
                        </div>
                        
                        <p>Vui lòng nhập mã này vào ứng dụng để tiếp tục đặt lại mật khẩu.</p>
                        
                        <div class="warning">
                            <strong>⚠️ Lưu ý:</strong> Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này và bảo mật tài khoản của bạn.
                        </div>
                        
                        <p>Nếu bạn gặp vấn đề, vui lòng liên hệ với chúng tôi qua ứng dụng.</p>
                        
                        <p style="margin-top: 30px;">
                            Trân trọng,<br>
                            <strong>Đội ngũ Piano Learner</strong> 🎼
                        </p>
                    </div>
                    <div class="footer">
                        <p>© 2025 Piano Learner. All rights reserved.</p>
                        <p>Email này được gửi tự động, vui lòng không trả lời.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(fullName != null ? fullName : "Người dùng", otpCode);
    }
}
