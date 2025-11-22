package com.example.demo.examOnline.service;

import com.example.demo.examOnline.domain.Post;
import com.example.demo.examOnline.domain.enums.PostType;
import com.example.demo.examOnline.repository.PostRepository;
import com.example.demo.examOnline.repository.StudentClassRepository;
import com.example.demo.examOnline.repository.AssignmentSubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssignmentDeadlineReminderService {
    private final PostRepository postRepository;
    private final StudentClassRepository studentClassRepository;
    private final AssignmentSubmissionRepository assignmentSubmissionRepository;
    private final NotificationService notificationService;

    /**
     * Check assignment deadlines daily at 8 AM and notify students who haven't submitted
     * Runs every day at 8:00 AM
     */
    @Scheduled(cron = "0 0 8 * * ?")
    @Transactional
    public void checkAssignmentDeadlines() {
        log.info("Starting scheduled check for assignment deadlines");
        
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime oneDayLater = now.plusDays(1);
            
            // Get all posts (assignments) from database
            List<Post> allPosts = postRepository.findAll();
            
            int notificationCount = 0;
            
            for (Post post : allPosts) {
                // Check if it's an assignment with due date
                if (post.getPostType() == PostType.ASSIGNMENT && post.getDueDate() != null) {
                    LocalDateTime dueDate = post.getDueDate();
                    
                    // Check if due date is within 1 day (between now and 1 day later)
                    if (dueDate.isAfter(now) && dueDate.isBefore(oneDayLater) || 
                        (dueDate.isAfter(now) && dueDate.isBefore(oneDayLater.plusHours(1)))) {
                        
                        Integer classId = post.getClassEntity().getClassId();
                        
                        // Get all students in the class
                        List<com.example.demo.examOnline.domain.StudentClass> studentClasses = 
                                studentClassRepository.findByClassId(classId);
                        
                        for (com.example.demo.examOnline.domain.StudentClass sc : studentClasses) {
                            Integer studentId = sc.getStudent().getUserId();
                            
                            // Check if student hasn't submitted
                            boolean hasSubmitted = assignmentSubmissionRepository
                                    .findByAssignmentPostIdAndStudentUserId(post.getPostId(), studentId)
                                    .isPresent();
                            
                            if (!hasSubmitted) {
                                // Check if notification already exists (to avoid duplicates)
                                // We'll check by trying to create and catch duplicates, or check recent notifications
                                try {
                                    String title = "Bài tập sắp hết hạn";
                                    String message = String.format("Bài tập '%s' còn 1 ngày nữa là hết hạn. Vui lòng nộp bài sớm!", 
                                            post.getTitle());
                                    
                                    notificationService.notifyUser(
                                            studentId, 
                                            title, 
                                            message, 
                                            com.example.demo.examOnline.domain.enums.NotificationType.ASSIGNMENT_DEADLINE,
                                            post.getTeacher().getUserId(), 
                                            classId);
                                    
                                    notificationCount++;
                                    log.debug("Created deadline notification for student {} and assignment {}", 
                                            studentId, post.getPostId());
                                } catch (Exception e) {
                                    log.warn("Error creating deadline notification for student {} and assignment {}: {}", 
                                            studentId, post.getPostId(), e.getMessage());
                                }
                            }
                        }
                    }
                }
            }
            
            log.info("Completed scheduled check for assignment deadlines. Created {} notifications", notificationCount);
        } catch (Exception e) {
            log.error("Error in scheduled assignment deadline check: {}", e.getMessage(), e);
        }
    }
}

