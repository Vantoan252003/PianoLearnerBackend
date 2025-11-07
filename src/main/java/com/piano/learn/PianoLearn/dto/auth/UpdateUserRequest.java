package com.piano.learn.PianoLearn.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserRequest {

    @Size(max = 100, message = "Full name must not exceed 100 characters")
    private String fullName;

    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password; 
}
