package com.piano.learn.PianoLearn.controller.auth;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.piano.learn.PianoLearn.dto.auth.LoginRequest;
import com.piano.learn.PianoLearn.dto.auth.LoginResponse;
import com.piano.learn.PianoLearn.dto.auth.RegisterRequest;
import com.piano.learn.PianoLearn.dto.auth.UserDetailResponse;
import com.piano.learn.PianoLearn.dto.auth.UserInfo;
import com.piano.learn.PianoLearn.entity.auth.User;
import com.piano.learn.PianoLearn.security.JwtUtil;
import com.piano.learn.PianoLearn.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder; 

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(),
                loginRequest.getPassword()
            )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findUserByEmail(userDetails.getUsername());

        // Generate token with role
        String token = jwtUtil.generateToken(userDetails, user.getRole().name());
        user.setLastLogin(java.time.LocalDateTime.now());

        UserInfo userInfo = new UserInfo(
            user.getUserId(),
            user.getFullName(),
            user.getEmail(),
            user.getAvatarUrl(),
            user.getLevelName(),
            user.getTotalExp(),
            user.getStreakDays()
        );

        Long expiresAt = jwtUtil.extractExpiration(token).getTime();

        LoginResponse response = LoginResponse.builder()
            .token(token)
            .tokenType("Bearer")
            .expiresAt(expiresAt)
            .role(user.getRole().name())
            .user(userInfo)
            .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(fe -> errors.put(fe.getField(), fe.getDefaultMessage()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }

        try {
   
            User user = userService.registerUser(registerRequest, passwordEncoder);
            UserDetails userDetails = org.springframework.security.core.userdetails.User
                    .withUsername(user.getEmail())
                    .password(user.getPasswordHash())
                    .authorities(user.getRole().name())
                    .build();

            String token = jwtUtil.generateToken(userDetails, user.getRole().name());
            Long expiresAt = jwtUtil.extractExpiration(token).getTime();


            UserInfo userInfo = new UserInfo(
                user.getUserId(),
                user.getFullName(),
                user.getEmail(),
                user.getAvatarUrl(),
                user.getLevelName(),
                user.getTotalExp(),
                user.getStreakDays()
            );

            LoginResponse response = LoginResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresAt(expiresAt)
                .role(user.getRole().name())
                .user(userInfo)
                .build();

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/checkmail")
    public ResponseEntity<?> checkEmail(@RequestParam String email) {
        if (userService.checkEmailExists(email)) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Email này đã được sử dụng!");
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/user-info")
    public ResponseEntity<?> getUserInfo() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "User not authenticated");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.findUserByEmail(userDetails.getUsername());
            
            UserDetailResponse userDetailResponse = userService.getUserDetailInfo(user.getUserId());
            
            return ResponseEntity.ok(userDetailResponse);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
