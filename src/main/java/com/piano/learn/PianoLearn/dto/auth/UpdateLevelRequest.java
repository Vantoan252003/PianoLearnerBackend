package com.piano.learn.PianoLearn.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateLevelRequest {

    @NotBlank(message = "Level name is required")
    @Pattern(regexp = "Beginner|Intermediate|Advanced", message = "Level name must be Beginner, Intermediate, or Advanced")
    private String levelName;
}