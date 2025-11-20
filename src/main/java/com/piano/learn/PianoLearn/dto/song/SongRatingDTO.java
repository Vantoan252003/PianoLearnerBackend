package com.piano.learn.PianoLearn.dto.song;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO để hiển thị thông tin rating của user cho bài hát
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SongRatingDTO {
    
    private Integer songRatingId;
    private Integer userId;
    private String username;
    private String userEmail;
    private Integer songId;
    private String avatarUrl;
    private String songTitle;
    private Double rating;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
