package com.piano.learn.PianoLearn.dto.sheet;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SheetRatingDTO {
    private Integer sheetRatingId;
    private Integer userId;
    private String username;
    private String userEmail;
    private Integer sheetId;
    private String sheetTitle;
    private Double rating;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
