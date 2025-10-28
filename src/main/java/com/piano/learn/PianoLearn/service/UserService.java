package com.piano.learn.PianoLearn.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.piano.learn.PianoLearn.dto.auth.RegisterRequest;
import com.piano.learn.PianoLearn.entity.auth.User;
import com.piano.learn.PianoLearn.repository.auth.UserRepository;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    public User registerUser (RegisterRequest registerRequest, PasswordEncoder encoder ){
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()){
            throw  new RuntimeException("Username has existed");
        }
        User user = new User();
        user.setFullName(registerRequest.getFullName());
        user.setEmail(registerRequest.getEmail());
        user.setPasswordHash(encoder.encode(registerRequest.getPassword()));
        user.setLevelName(registerRequest.getLevelName());
        user.setAvatarUrl(null);         
        user.setTotalExp(0);
        user.setStreakDays(0);
        user.setCreatedAt(java.time.LocalDateTime.now());
        user.setLastLogin(null);
        return userRepository.save(user);
    }
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User u = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email not found"));

        return org.springframework.security.core.userdetails.User
                .withUsername(u.getEmail())
                .password(u.getPasswordHash())
                .authorities(u.getRole().name())  // Use actual role from database
                .build();
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email not found"));
    }

}
