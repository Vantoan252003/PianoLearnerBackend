package com.piano.learn.PianoLearn.dto.sheet;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO để nhận request tạo/update rating cho sheet music
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSheetRatingRequest {
    
    @NotNull(message = "Rating không được để trống")
    @DecimalMin(value = "0.0", message = "Rating phải từ 0.0")
    @DecimalMax(value = "5.0", message = "Rating tối đa 5.0")
    private Double rating;
    
    @Size(max = 1000, message = "Comment tối đa 1000 ký tự")
    private String comment;
}