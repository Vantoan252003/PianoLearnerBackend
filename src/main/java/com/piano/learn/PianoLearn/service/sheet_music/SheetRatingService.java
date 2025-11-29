package com.piano.learn.PianoLearn.service.sheet_music;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.piano.learn.PianoLearn.dto.sheet.CreateSheetRatingRequest;
import com.piano.learn.PianoLearn.dto.sheet.SheetRatingDTO;
import com.piano.learn.PianoLearn.entity.auth.User;
import com.piano.learn.PianoLearn.entity.sheet.SheetMusic;
import com.piano.learn.PianoLearn.entity.sheet.SheetRating;
import com.piano.learn.PianoLearn.repository.auth.UserRepository;
import com.piano.learn.PianoLearn.repository.sheet.SheetMusicRepository;
import com.piano.learn.PianoLearn.repository.sheet.SheetRatingRepository;
import com.piano.learn.PianoLearn.service.notification.NotificationService;

@Service
public class SheetRatingService {
    
    @Autowired
    private SheetRatingRepository sheetRatingRepository;
    
    @Autowired
    private SheetMusicRepository sheetMusicRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Tạo hoặc cập nhật rating của user cho sheet music
     */
    @Transactional
    public SheetRatingDTO createOrUpdateRating(Integer userId, Integer sheetId, CreateSheetRatingRequest request) {
        // Kiểm tra sheet có tồn tại không
        SheetMusic sheet = sheetMusicRepository.findById(sheetId)
                .orElseThrow(() -> new RuntimeException("Sheet music không tồn tại"));
        
        // Kiểm tra user đã rating sheet này chưa
        Optional<SheetRating> existingRating = sheetRatingRepository.findByUser_UserIdAndSheetMusic_SheetId(userId, sheetId);
        
        SheetRating sheetRating;
        if (existingRating.isPresent()) {
            // Update rating cũ
            sheetRating = existingRating.get();
            sheetRating.setRating(request.getRating());
            sheetRating.setComment(request.getComment());
            sheetRating.setUpdatedAt(LocalDateTime.now());
        } else {
            // Tạo rating mới
            User user = User.builder().userId(userId).build();
            sheetRating = SheetRating.builder()
                    .user(user)
                    .sheetMusic(sheet)
                    .rating(request.getRating())
                    .comment(request.getComment())
                    .build();
        }
        
        SheetRating saved = sheetRatingRepository.save(sheetRating);
        
        // Cập nhật average rating cho sheet
        updateSheetAverageRating(sheetId);
        
        // Gửi thông báo cho owner của sheet music (nếu không phải tự rate)
        User rater = userRepository.findById(userId).orElse(null);
        if (rater != null && sheet.getUploadedBy() != null) {
            notificationService.notifyOnSheetRated(
                sheet.getUploadedBy().getUserId(),
                rater,
                sheet,
                request.getRating(),
                request.getComment()
            );
        }
        
        return convertToDTO(saved);
    }
    
    /**
     * Lấy tất cả ratings của một sheet
     */
    public List<SheetRatingDTO> getRatingsBySheetId(Integer sheetId) {
        List<SheetRating> ratings = sheetRatingRepository.findBySheetMusic_SheetIdOrderByCreatedAtDesc(sheetId);
        return ratings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy rating của user cho một sheet (nếu có)
     */
    public Optional<SheetRatingDTO> getUserRatingForSheet(Integer userId, Integer sheetId) {
        return sheetRatingRepository.findByUser_UserIdAndSheetMusic_SheetId(userId, sheetId)
                .map(this::convertToDTO);
    }
    
    /**
     * Xóa rating
     */
    @Transactional
    public boolean deleteRating(Integer userId, Integer sheetId) {
        Optional<SheetRating> rating = sheetRatingRepository.findByUser_UserIdAndSheetMusic_SheetId(userId, sheetId);
        if (rating.isPresent()) {
            sheetRatingRepository.delete(rating.get());
            updateSheetAverageRating(sheetId);
            return true;
        }
        return false;
    }
    
    /**
     * Cập nhật average rating cho sheet
     */
    private void updateSheetAverageRating(Integer sheetId) {
        Double averageRating = sheetRatingRepository.getAverageRatingBySheetId(sheetId);
        SheetMusic sheet = sheetMusicRepository.findById(sheetId).orElse(null);
        if (sheet != null) {
            sheet.setRating(averageRating != null ? averageRating : 0.0);
            sheetMusicRepository.save(sheet);
        }
    }
    
    /**
     * Convert entity sang DTO
     */
    private SheetRatingDTO convertToDTO(SheetRating rating) {
        // Load full user entity để lấy thông tin
        User user = rating.getUser();
        return SheetRatingDTO.builder()
                .sheetRatingId(rating.getSheetRatingId())
                .userId(user.getUserId())
                .username(user.getFullName())
                .userEmail(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .sheetId(rating.getSheetMusic().getSheetId())
                .sheetTitle(rating.getSheetMusic().getTitle())
                .rating(rating.getRating())
                .comment(rating.getComment())
                .createdAt(rating.getCreatedAt())
                .updatedAt(rating.getUpdatedAt())
                .build();
    }
}