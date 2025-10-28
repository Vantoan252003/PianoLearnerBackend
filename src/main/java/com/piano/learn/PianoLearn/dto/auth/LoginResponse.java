package com.piano.learn.PianoLearn.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse {
    private String token;
    private String tokenType;
    private Long expiresAt;
    private String role;  // Add role field
    private UserInfo user;
}

