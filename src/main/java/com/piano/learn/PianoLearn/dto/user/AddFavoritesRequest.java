package com.piano.learn.PianoLearn.dto.user;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddFavoritesRequest {
    
    @NotEmpty(message = "Song IDs cannot be empty")
    private List<Integer> songIds;
}