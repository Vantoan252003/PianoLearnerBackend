package com.piano.learn.PianoLearn.controller.auth;

import java.io.IOException;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.piano.learn.PianoLearn.dto.auth.ChangePasswordRequest;
import com.piano.learn.PianoLearn.dto.auth.LoginRequest;
import com.piano.learn.PianoLearn.dto.auth.LoginResponse;
import com.piano.learn.PianoLearn.dto.auth.RegisterRequest;
import com.piano.learn.PianoLearn.dto.auth.UserDetailResponse;
import com.piano.learn.PianoLearn.dto.auth.UserInfo;
import com.piano.learn.PianoLearn.dto.auth.UserRankingInfo;
import com.piano.learn.PianoLearn.entity.auth.User;
import com.piano.learn.PianoLearn.security.JwtUtil;
import com.piano.learn.PianoLearn.service.UploadService;
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

    @Autowired
    private UploadService uploadService; 

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

    @GetMapping("/ranking")
    public ResponseEntity<?> getRanking() {
        try {
            java.util.List<UserRankingInfo> rankingList = userService.getAllUsersRanking();
            return ResponseEntity.ok(rankingList);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    @PutMapping("/user-info")
    public ResponseEntity<?> updateUserInfo(
            @RequestParam("fullName") String fullName,
            @RequestParam("email") String email,
            @RequestPart(value = "avatar", required = false) MultipartFile avatar) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "User not authenticated");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.findUserByEmail(userDetails.getUsername());

            // Basic validation
            if (fullName != null && fullName.length() > 100) {
                throw new RuntimeException("Full name must not exceed 100 characters");
            }
            if (email != null && (email.length() > 100 || !email.contains("@"))) {
                throw new RuntimeException("Invalid email format");
            }

            String avatarUrl = null;
            if (avatar != null && !avatar.isEmpty()) {
                avatarUrl = uploadService.uploadAvatar(avatar);
            }

            // Update user information
            User updatedUser = userService.updateUser(
                user.getUserId(),
                fullName,
                email,
                null,
                avatarUrl,
                passwordEncoder
            );

            UserDetailResponse userDetailResponse = userService.getUserDetailInfo(updatedUser.getUserId());

            return ResponseEntity.ok(userDetailResponse);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(fe -> errors.put(fe.getField(), fe.getDefaultMessage()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "User not authenticated");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.findUserByEmail(userDetails.getUsername());
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Mật khẩu hiện tại không đúng");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            if (request.getCurrentPassword().equals(request.getNewPassword())) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Mật khẩu mới không được trùng mật khẩu hiện tại");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Mật khẩu mới và xác nhận mật khẩu không khớp");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            userService.updateUser(user.getUserId(), null, null, request.getNewPassword(), null, passwordEncoder);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Đổi mật khẩu thành công");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/upload-avatar")
    public ResponseEntity<?> uploadAvatar(@RequestParam("avatar") MultipartFile avatar) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "User not authenticated");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            if (avatar == null || avatar.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Avatar file is required");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            // Validate file type
            String contentType = avatar.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Only image files are allowed");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            // Validate file size (max 5MB)
            if (avatar.getSize() > 5 * 1024 * 1024) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Dung lượng ảnh quá lớn!");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            String avatarUrl = uploadService.uploadAvatar(avatar);

            Map<String, String> response = new HashMap<>();
            response.put("avatarUrl", avatarUrl);
            response.put("message", "Cập nhật avatar thành công!");
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (IOException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to upload avatar");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/update-user-info")
    public ResponseEntity<?> updateUserInfo(@RequestParam("fullName") String fullName, @RequestParam("email") String email) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "User not authenticated");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.findUserByEmail(userDetails.getUsername());

            // Basic validation
            if (fullName != null && fullName.trim().isEmpty()) {
                throw new RuntimeException("Full name cannot be empty");
            }
            if (fullName != null && fullName.length() > 100) {
                throw new RuntimeException("Full name must not exceed 100 characters");
            }
            if (email != null && (email.trim().isEmpty() || !email.contains("@"))) {
                throw new RuntimeException("Invalid email format");
            }
            if (email != null && email.length() > 100) {
                throw new RuntimeException("Email must not exceed 100 characters");
            }

            // Check if email is already taken by another user
            if (email != null && !email.equals(user.getEmail()) && userService.checkEmailExists(email)) {
                throw new RuntimeException("Email is already taken by another user");
            }

            // Update user information (only email and fullname, no avatar)
            User updatedUser = userService.updateUser(
                user.getUserId(),
                fullName,
                email,
                null, // no password change
                null, // no avatar change
                passwordEncoder
            );

            UserDetailResponse userDetailResponse = userService.getUserDetailInfo(updatedUser.getUserId());

            return ResponseEntity.ok(userDetailResponse);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
