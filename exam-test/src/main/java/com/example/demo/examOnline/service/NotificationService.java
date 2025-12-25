package com.example.demo.examOnline.service;

import com.example.demo.examOnline.domain.Notification;
import com.example.demo.examOnline.domain.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {

    public Notification createNotification(Integer userId, String title, String message,
            NotificationType type, Integer senderId, Integer classId);

    public void notifyStudentsInClass(Integer classId, String title, String message,
            NotificationType type, Integer senderId);

    public void notifyUser(Integer userId, String title, String message,
            NotificationType type, Integer senderId, Integer classId);

    public Page<Notification> getUserNotifications(Integer userId, Pageable pageable);

    public void markAsRead(Integer notificationId, Integer userId);

    public void markAllAsRead(Integer userId);

    public Notification getNotificationById(Integer notificationId, Integer userId);

    public void checkAndNotifyAssignmentDeadlines(Integer userId);

    public Long getUnreadCount(Integer userId);
}
