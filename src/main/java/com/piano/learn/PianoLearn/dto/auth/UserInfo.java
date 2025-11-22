package com.piano.learn.PianoLearn.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserInfo {
    private Integer userId;
    private String fullName;
    private String email;
    private String avatarUrl;
    private String levelName;
    private Integer totalExp;
    private Integer streakDays;
}
