package com.piano.learn.PianoLearn.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.piano.learn.PianoLearn.entity.auth.Role;
import com.piano.learn.PianoLearn.entity.auth.User;
import com.piano.learn.PianoLearn.repository.auth.UserRepository;

@Component
public class AdminInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        // Kiểm tra xem có admin nào trong hệ thống chưa
        long adminCount = userRepository.countByRole(Role.admin);
        
        if (adminCount == 0) {
            User admin = User.builder()
                    .fullName("Administrator")
                    .email("admin@gmail.com")
                    .passwordHash(passwordEncoder.encode("Admin@123"))
                    .role(Role.admin)
                    .levelName("Expert")
                    .totalExp(0)
                    .streakDays(0)
                    .build();
            
            userRepository.save(admin);
            System.out.println("✅ Đã tạo tài khoản admin mặc định:");
            System.out.println("   Email: admin@gmail.com");
            System.out.println("   Password: Admin@123");
        } else {
            System.out.println("✅ Hệ thống đã có " + adminCount + " admin(s)");
        }
    }
}
