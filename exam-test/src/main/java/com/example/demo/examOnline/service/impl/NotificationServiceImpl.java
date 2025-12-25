package com.example.demo.examOnline.service.impl;

import com.example.demo.examOnline.domain.Classes;
import com.example.demo.examOnline.domain.Notification;
import com.example.demo.examOnline.domain.Post;
import com.example.demo.examOnline.domain.User;
import com.example.demo.examOnline.domain.enums.NotificationType;
import com.example.demo.examOnline.repository.NotificationRepository;
import com.example.demo.examOnline.repository.StudentClassRepository;
import com.example.demo.examOnline.repository.PostRepository;
import com.example.demo.examOnline.repository.AssignmentSubmissionRepository;
import com.example.demo.examOnline.repository.UserRepository;
import com.example.demo.examOnline.service.NotificationService;
import com.example.demo.examOnline.repository.ClassRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final StudentClassRepository studentClassRepository;
    private final PostRepository postRepository;
    private final AssignmentSubmissionRepository assignmentSubmissionRepository;
    private final UserRepository userRepository;
    private final ClassRepository classRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Notification createNotification(Integer userId, String title, String message,
            NotificationType type, Integer senderId, Integer classId) {
        log.debug("Creating notification: userId={}, title={}, type={}, senderId={}, classId={}",
                userId, title, type, senderId, classId);
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found: " + userId));
            log.debug("Found user: {}", user.getUserId());

            Notification.NotificationBuilder builder = Notification.builder()
                    .user(user)
                    .title(title)
                    .message(message)
                    .notificationType(type)
                    .isRead(false)
                    .createdAt(LocalDateTime.now());

            if (senderId != null) {
                try {
                    User sender = userRepository.findById(senderId)
                            .orElse(null);
                    if (sender != null) {
                        builder.sender(sender);
                        log.debug("Set sender: {}", sender.getUserId());
                    } else {
                        log.warn("Sender user {} not found", senderId);
                    }
                } catch (Exception e) {
                    log.warn("Could not load sender user {}: {}", senderId, e.getMessage());
                }
            }

            if (classId != null) {
                try {
                    Classes classEntity = classRepository.findById(classId)
                            .orElse(null);
                    if (classEntity != null) {
                        builder.classEntity(classEntity);
                        log.debug("Set class: {}", classEntity.getClassId());
                    } else {
                        log.warn("Class {} not found", classId);
                    }
                } catch (Exception e) {
                    log.warn("Could not load class {}: {}", classId, e.getMessage());
                }
            }

            Notification notification = builder.build();
            Notification saved = notificationRepository.save(notification);
            log.debug("Successfully saved notification with ID: {}", saved.getNotificationId());
            return saved;
        } catch (Exception e) {
            log.error("Error creating notification for user {}: {}", userId, e.getMessage(), e);
            // Don't throw - return null to indicate failure
            return null;
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void notifyStudentsInClass(Integer classId, String title, String message,
            NotificationType type, Integer senderId) {
        log.info(">>> notifyStudentsInClass called: classId={}, title={}, senderId={}", classId, title, senderId);
        try {
            // Clear entity manager to avoid conflicts with entities from parent transaction
            entityManager.clear();
            log.debug("Entity manager cleared");

            // Use a fresh query to avoid entity manager conflicts
            List<com.example.demo.examOnline.domain.StudentClass> studentClasses = studentClassRepository
                    .findByClassId(classId);

            log.info("Found {} students in class {}", studentClasses.size(), classId);

            if (studentClasses.isEmpty()) {
                log.warn("No students found in class {}. No notifications will be sent.", classId);
                return;
            }

            int successCount = 0;
            for (com.example.demo.examOnline.domain.StudentClass sc : studentClasses) {
                try {
                    Integer studentId = sc.getStudent().getUserId();
                    log.debug("Creating notification for student {} in class {}", studentId, classId);
                    Notification notification = createNotification(studentId, title, message, type, senderId, classId);
                    if (notification != null) {
                        successCount++;
                        log.debug("Successfully created notification {} for student {}",
                                notification.getNotificationId(), studentId);
                    } else {
                        log.warn("Failed to create notification for student {} (createNotification returned null)",
                                studentId);
                    }
                } catch (Exception e) {
                    // Log error for individual student but continue with others
                    log.error("Error creating notification for student {}: {}",
                            sc.getStudent() != null ? sc.getStudent().getUserId() : "unknown", e.getMessage(), e);
                }
            }
            log.info("<<< notifyStudentsInClass completed: Sent {} notifications to {} students in class {}",
                    successCount, studentClasses.size(), classId);
        } catch (Exception e) {
            log.error("<<< ERROR in notifyStudentsInClass for class {}: {}", classId, e.getMessage(), e);
            // Don't throw - let the calling code handle it gracefully
        }
    }

    @Override
    @Transactional
    public void notifyUser(Integer userId, String title, String message,
            NotificationType type, Integer senderId, Integer classId) {
        createNotification(userId, title, message, type, senderId, classId);
    }

    public Page<Notification> getUserNotifications(Integer userId, Pageable pageable) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    public Long getUnreadCount(Integer userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }

    @Override
    @Transactional
    public void markAsRead(Integer notificationId, Integer userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found: " + notificationId));

        if (!notification.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("You can only mark your own notifications as read");
        }

        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void markAllAsRead(Integer userId) {
        List<Notification> unreadNotifications = notificationRepository.findUnreadByUserId(userId);

        for (Notification notification : unreadNotifications) {
            notification.setIsRead(true);
        }

        notificationRepository.saveAll(unreadNotifications);
    }

    @Override
    public Notification getNotificationById(Integer notificationId, Integer userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found: " + notificationId));

        if (!notification.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("You can only access your own notifications");
        }

        return notification;
    }

    @Override
    @Transactional
    public void checkAndNotifyAssignmentDeadlines(Integer userId) {
        try {
            userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found: " + userId));

            // Get all classes the user is enrolled in
            List<com.example.demo.examOnline.domain.StudentClass> studentClasses = studentClassRepository
                    .findByStudentId(userId);

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime oneDayLater = now.plusDays(1);

            for (com.example.demo.examOnline.domain.StudentClass sc : studentClasses) {
                Integer classId = sc.getClassEntity().getClassId();

                // Get all assignments in this class
                List<Post> assignments = postRepository.findByClassIdOrderByCreatedAtDesc(classId);

                for (Post assignment : assignments) {
                    // Check if it's an assignment with due date
                    if (assignment.getPostType() == com.example.demo.examOnline.domain.enums.PostType.ASSIGNMENT
                            && assignment.getDueDate() != null) {

                        LocalDateTime dueDate = assignment.getDueDate();

                        // Check if due date is within 1 day
                        if (dueDate.isAfter(now) && dueDate.isBefore(oneDayLater) ||
                                dueDate.isEqual(now)
                                || (dueDate.isAfter(now) && dueDate.isBefore(oneDayLater.plusHours(1)))) {

                            // Check if student hasn't submitted
                            boolean hasSubmitted = assignmentSubmissionRepository
                                    .findByAssignmentPostIdAndStudentUserId(assignment.getPostId(), userId)
                                    .isPresent();

                            if (!hasSubmitted) {
                                // Check if notification already exists for this assignment
                                boolean notificationExists = notificationRepository
                                        .findByUserIdOrderByCreatedAtDesc(userId,
                                                org.springframework.data.domain.PageRequest.of(0, 100))
                                        .getContent()
                                        .stream()
                                        .anyMatch(n -> n.getNotificationType() == NotificationType.ASSIGNMENT_DEADLINE
                                                && n.getMessage().contains(assignment.getTitle())
                                                && n.getCreatedAt().isAfter(now.minusHours(2))); // Check within last 2
                                                                                                 // hours

                                if (!notificationExists) {
                                    String title = "Bài tập sắp hết hạn";
                                    String message = String.format(
                                            "Bài tập '%s' còn 1 ngày nữa là hết hạn. Vui lòng nộp bài sớm!",
                                            assignment.getTitle());

                                    createNotification(userId, title, message,
                                            NotificationType.ASSIGNMENT_DEADLINE,
                                            assignment.getTeacher().getUserId(),
                                            classId);

                                    log.info("Created deadline notification for user {} and assignment {}",
                                            userId, assignment.getPostId());
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error checking assignment deadlines for user {}: {}", userId, e.getMessage(), e);
        }
    }
}
