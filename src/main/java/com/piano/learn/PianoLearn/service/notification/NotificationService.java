package com.piano.learn.PianoLearn.service.notification;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.piano.learn.PianoLearn.entity.auth.User;
import com.piano.learn.PianoLearn.entity.sheet.SheetMusic;


@Service
public class NotificationService {
    
    @Autowired
    private FCMService fcmService;
    
 
   
    public void notifyOnSheetFavorited(Integer sheetOwnerId, User favoriter, SheetMusic sheet) {
        if (sheetOwnerId == null || favoriter == null || sheet == null) {
            return;
        }
        
        // Không gửi thông báo nếu chính người dùng favorite sheet của mình
        if (sheetOwnerId.equals(favoriter.getUserId())) {
            return;
        }
        
        String title = "Sheet music của bạn được yêu thích!";
        String body = String.format("%s đã thêm sheet music \"%s\" vào danh sách yêu thích", 
                                   favoriter.getFullName(), sheet.getTitle());
        
        Map<String, String> data = new HashMap<>();
        data.put("type", "sheet_favorited");
        data.put("sheetId", sheet.getSheetId().toString());
        data.put("userId", favoriter.getUserId().toString());
        
        fcmService.sendNotificationWithData(sheetOwnerId, title, body, sheet.getThumbnailUrl(), data);
    }
    
 
    
    /**
     * Gửi thông báo khi có người đánh giá sheet music
     */
    public void notifyOnSheetRated(Integer sheetOwnerId, User rater, SheetMusic sheet, Double rating, String comment) {
        if (sheetOwnerId == null || rater == null || sheet == null) {
            return;
        }
        
        // Không gửi thông báo nếu chính người dùng rate sheet của mình
        if (sheetOwnerId.equals(rater.getUserId())) {
            return;
        }
        
        String title = "Sheet music của bạn được đánh giá!";
        String body;
        
        if (comment != null && !comment.isEmpty()) {
            body = String.format("%s đã đánh giá %.1f⭐ sheet music \"%s\": %s", 
                               rater.getFullName(), rating, sheet.getTitle(), comment);
        } else {
            body = String.format("%s đã đánh giá %.1f⭐ sheet music \"%s\"", 
                               rater.getFullName(), rating, sheet.getTitle());
        }
        
        Map<String, String> data = new HashMap<>();
        data.put("type", "sheet_rated");
        data.put("sheetId", sheet.getSheetId().toString());
        data.put("userId", rater.getUserId().toString());
        data.put("rating", rating.toString());
        
        fcmService.sendNotificationWithData(sheetOwnerId, title, body, sheet.getThumbnailUrl(), data);
    }
    
    /**
     * Gửi thông báo custom
     */
    public void sendCustomNotification(Integer userId, String title, String body, String imageUrl, Map<String, String> data) {
        fcmService.sendNotificationWithData(userId, title, body, imageUrl, data);
    }
}
