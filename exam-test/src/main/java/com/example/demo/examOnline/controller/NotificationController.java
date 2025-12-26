package com.example.demo.examOnline.controller;

import com.example.demo.examOnline.domain.Notification;
import com.example.demo.examOnline.dto.response.NotificationResponseDTO;
import com.example.demo.examOnline.service.NotificationService;
import com.example.demo.examOnline.service.impl.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class NotificationController {
    private final NotificationService notificationService;
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<Page<NotificationResponseDTO>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            Integer userId = userService.getUserIdByEmail(currentUserEmail);
            if (userId == null) {
                log.error("User not found: {}", currentUserEmail);
                return ResponseEntity.status(401).build();
            }

            log.info("Received get notifications request - userId: {}, page: {}, size: {}", userId, page, size);

            Pageable pageable = PageRequest.of(page, size);
            Page<Notification> notifications = notificationService.getUserNotifications(userId, pageable);
            
            Page<NotificationResponseDTO> response = notifications.map(this::mapToDTO);
            
            log.info("Returning notifications - count: {}", response.getContent().size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting notifications: ", e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/unread-count")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<Map<String, Long>> getUnreadCount() {
        try {
            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            Integer userId = userService.getUserIdByEmail(currentUserEmail);
            if (userId == null) {
                log.error("User not found: {}", currentUserEmail);
                return ResponseEntity.status(401).build();
            }

            log.info("Received get unread count request - userId: {}", userId);

            Long unreadCount = notificationService.getUnreadCount(userId);
            
            Map<String, Long> response = new HashMap<>();
            response.put("unreadCount", unreadCount);
            
            log.info("Returning unread count: {}", unreadCount);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting unread count: ", e);
            return ResponseEntity.status(500).build();
        }
    }

    @PutMapping("/{notificationId}/read")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<Void> markAsRead(@PathVariable Integer notificationId) {
        try {
            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            Integer userId = userService.getUserIdByEmail(currentUserEmail);
            if (userId == null) {
                log.error("User not found: {}", currentUserEmail);
                return ResponseEntity.status(401).build();
            }

            log.info("Received mark as read request - notificationId: {}, userId: {}", notificationId, userId);

            notificationService.markAsRead(notificationId, userId);
            log.info("Notification marked as read - notificationId: {}", notificationId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error marking notification as read: ", e);
            return ResponseEntity.status(500).build();
        }
    }

    @PutMapping("/read-all")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<Void> markAllAsRead() {
        try {
            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            Integer userId = userService.getUserIdByEmail(currentUserEmail);
            if (userId == null) {
                log.error("User not found: {}", currentUserEmail);
                return ResponseEntity.status(401).build();
            }

            log.info("Received mark all as read request - userId: {}", userId);

            notificationService.markAllAsRead(userId);
            log.info("All notifications marked as read - userId: {}", userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error marking all notifications as read: ", e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/{notificationId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<NotificationResponseDTO> getNotification(@PathVariable Integer notificationId) {
        try {
            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            Integer userId = userService.getUserIdByEmail(currentUserEmail);
            if (userId == null) {
                log.error("User not found: {}", currentUserEmail);
                return ResponseEntity.status(401).build();
            }

            log.info("Received get notification request - notificationId: {}, userId: {}", notificationId, userId);

            Notification notification = notificationService.getNotificationById(notificationId, userId);
            NotificationResponseDTO response = mapToDTO(notification);
            
            log.info("Returning notification - notificationId: {}", notificationId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting notification: ", e);
            return ResponseEntity.status(500).build();
        }
    }

    private NotificationResponseDTO mapToDTO(Notification notification) {
        NotificationResponseDTO.NotificationResponseDTOBuilder builder = NotificationResponseDTO.builder()
                .notificationId(notification.getNotificationId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .notificationType(notification.getNotificationType())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .userId(notification.getUser() != null ? notification.getUser().getUserId() : null)
                .classId(notification.getClassEntity() != null ? notification.getClassEntity().getClassId() : null)
                .className(notification.getClassEntity() != null ? notification.getClassEntity().getClassName() : null)
                .senderId(notification.getSender() != null ? notification.getSender().getUserId() : null)
                .senderName(notification.getSender() != null ? notification.getSender().getFullName() : null);

        // Set relatedId based on notification type
        // This would need to be enhanced based on how we store related entity IDs
        // For now, we can use classId or senderId as relatedId
        if (notification.getClassEntity() != null) {
            builder.relatedId(notification.getClassEntity().getClassId());
        }

        return builder.build();
    }
}

