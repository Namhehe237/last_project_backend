package com.example.demo.examOnline.dto.response;

import com.example.demo.examOnline.domain.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDTO {
    private Integer notificationId;
    private String title;
    private String message;
    private NotificationType notificationType;
    private Boolean isRead;
    private LocalDateTime createdAt;
    private Integer userId;
    private Integer classId;
    private Integer senderId;
    private String senderName;
    private String className;
    
    // Related entity IDs for navigation
    private Integer relatedId; // examId, postId, etc. depending on type
}

