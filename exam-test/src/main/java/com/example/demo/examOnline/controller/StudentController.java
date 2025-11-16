package com.example.demo.examOnline.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.examOnline.dto.request.student.GetStudentClassesRequest;
import com.example.demo.examOnline.dto.request.student.GetClassDetailsRequest;
import com.example.demo.examOnline.dto.request.student.GetClassAssignmentsRequest;
import com.example.demo.examOnline.dto.request.student.GetClassAnnouncementsRequest;
import com.example.demo.examOnline.dto.request.student.LeaveClassRequest;
import com.example.demo.examOnline.dto.request.RequestJoinClassRequest;
import com.example.demo.examOnline.dto.response.MessageResponse;
import com.example.demo.examOnline.dto.response.ClassResponseDTO;
import com.example.demo.examOnline.service.StudentService;
import com.example.demo.examOnline.service.ClassManagementService;
import com.example.demo.examOnline.service.CloudinaryService;
import com.example.demo.examOnline.service.ExamService;
import com.example.demo.examOnline.service.UserService;
import com.example.demo.examOnline.repository.UserRepository;
import com.example.demo.examOnline.domain.User;
import com.example.demo.examOnline.domain.enums.RoleName;
import com.example.demo.examOnline.dto.response.AssignmentResponseDTO;
import com.example.demo.examOnline.dto.response.AnnouncementResponseDTO;
import com.example.demo.examOnline.dto.response.TestHistoryResponse;

import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class StudentController {

    private final StudentService studentService;
    private final ClassManagementService classManagementService;
    private final CloudinaryService cloudinaryService;
    private final ExamService examService;
    private final UserService userService;
    private final UserRepository userRepository;


    //  Đăng xuất
    @PostMapping("/logout")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<MessageResponse> logout() {
        try {
            MessageResponse response = studentService.logout();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    MessageResponse.builder()
                            .message("Đăng xuất thất bại: " + e.getMessage())
                            .success(false)
                            .build());
        }
    }



    // ==================== QUẢN LÝ LỚP HỌC ====================

    //  Gửi yêu cầu tham gia lớp học (cần phê duyệt) - sử dụng classId
    @PostMapping("/classes/request-join")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<MessageResponse> requestJoinClass(@Valid @RequestBody RequestJoinClassRequest request) {
        try {
            MessageResponse response = classManagementService.requestJoinClass(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    MessageResponse.builder()
                            .message("Gửi yêu cầu tham gia lớp học thất bại: " + e.getMessage())
                            .success(false)
                            .build());
        }
    }

    // 9. Xem danh sách lớp học đã tham gia
    @PostMapping("/classes")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<ClassResponseDTO>> getMyClasses(@Valid @RequestBody GetStudentClassesRequest request) {
        try {
            List<ClassResponseDTO> classes = classManagementService.getStudentClasses(request.getStudentId());
            return ResponseEntity.ok(classes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 10. Xem chi tiết lớp học
    @PostMapping("/classes/details")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ClassResponseDTO> getClassDetails(@Valid @RequestBody GetClassDetailsRequest request) {
        try {
            ClassResponseDTO classDetails = classManagementService.getClassDetails(request.getStudentId(), request.getClassId());
            return ResponseEntity.ok(classDetails);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 11. Rời khỏi lớp học
    @PostMapping("/classes/leave")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<MessageResponse> leaveClass(@Valid @RequestBody LeaveClassRequest request) {
        try {
            MessageResponse response = classManagementService.leaveClass(request.getStudentId(), request.getClassId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    MessageResponse.builder()
                            .message("Rời khỏi lớp học thất bại: " + e.getMessage())
                            .success(false)
                            .build());
        }
    }


    // 13. Xem danh sách bài tập trong lớp
    @PostMapping("/classes/assignments")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<AssignmentResponseDTO>> getClassAssignments(@Valid @RequestBody GetClassAssignmentsRequest request) {
        try {
            List<AssignmentResponseDTO> assignments = classManagementService.getClassAssignments(request.getStudentId(), request.getClassId());
            return ResponseEntity.ok(assignments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 14. Xem bảng tin lớp học
    @PostMapping("/classes/announcements")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<AnnouncementResponseDTO>> getClassAnnouncements(@Valid @RequestBody GetClassAnnouncementsRequest request) {
        try {
            List<AnnouncementResponseDTO> announcements = classManagementService.getClassAnnouncements(request.getStudentId(), request.getClassId());
            return ResponseEntity.ok(announcements);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ==================== API KHÁC ====================
    
    @GetMapping("/courses")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<String> viewCourses() {
        return ResponseEntity.ok("View Courses - All authenticated users can access");
    }

    @GetMapping("/grades")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<String> viewOwnGrades() {
        return ResponseEntity.ok("View Own Grades - Only STUDENT can access");
    }

    @PostMapping(value = "/upload-avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<String> uploadAvatar(@RequestParam("avatar") MultipartFile avatarFile) {
        try {
            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            log.info("Received avatar upload request - user: {}, fileSize: {} bytes, contentType: {}", 
                    currentUserEmail, avatarFile.getSize(), avatarFile.getContentType());
            
            if (avatarFile.isEmpty()) {
                log.error("Avatar file is empty");
                return ResponseEntity.badRequest().body("Avatar file is empty");
            }
            
            // Validate file type
            String contentType = avatarFile.getContentType();
            if (contentType == null || 
                (!contentType.equals("image/jpeg") && !contentType.equals("image/jpg") && !contentType.equals("image/png"))) {
                log.error("Invalid file type: {}", contentType);
                return ResponseEntity.badRequest().body("Only JPEG, JPG, and PNG images are allowed");
            }
            
            // Validate file size (max 2MB)
            if (avatarFile.getSize() > 2 * 1024 * 1024) {
                log.error("File size too large: {} bytes", avatarFile.getSize());
                return ResponseEntity.badRequest().body("File size must not exceed 2MB");
            }
            
            // Get user from email to get role and userId
            User user = userRepository.findByEmail(currentUserEmail)
                    .orElseThrow(() -> new RuntimeException("User not found: " + currentUserEmail));
            
            Integer userId = user.getUserId();
            RoleName roleName = user.getRoleName();
            
            // Determine folder based on role
            String folder;
            if (roleName == RoleName.TEACHER) {
                folder = String.format("teacher-avatars/teacher-%d", userId);
            } else if (roleName == RoleName.ADMIN) {
                folder = String.format("admin-avatars/admin-%d", userId);
            } else {
                folder = String.format("student-avatars/student-%d", userId);
            }
            
            log.info("Uploading to folder: {} for role: {}", folder, roleName);
            
            String avatarUrl;
            try {
                avatarUrl = cloudinaryService.uploadImage(avatarFile, folder);
            } catch (IOException e) {
                log.error("Cloudinary upload error: ", e);
                return ResponseEntity.status(500).body("Failed to upload image to Cloudinary: " + e.getMessage());
            }
            
            if (avatarUrl == null || avatarUrl.isEmpty()) {
                log.error("Received empty avatar URL from Cloudinary");
                return ResponseEntity.status(500).body("Failed to get avatar URL from Cloudinary");
            }
            
            // Update user's avatar URL in database
            try {
                userService.updateAvatarUrl(userId, avatarUrl);
            } catch (Exception e) {
                log.error("Error updating avatar URL in database: ", e);
                return ResponseEntity.status(500).body("Failed to update avatar URL in database: " + e.getMessage());
            }
            
            log.info("Avatar uploaded successfully - URL: {}", avatarUrl);
            return ResponseEntity.ok(avatarUrl);
        } catch (Exception e) {
            log.error("Error uploading avatar: ", e);
            e.printStackTrace(); // Print full stack trace for debugging
            return ResponseEntity.status(500).body("Failed to upload avatar: " + e.getMessage() + ". Check server logs for details.");
        }
    }

    @GetMapping("/test-history")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<TestHistoryResponse>> getTestHistory() {
        try {
            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            log.info("Received test history request - user: {}", currentUserEmail);
            
            Integer userId = studentService.getUserIdByEmail(currentUserEmail);
            if (userId == null) {
                log.error("User not found: {}", currentUserEmail);
                return ResponseEntity.badRequest().build();
            }
            
            List<TestHistoryResponse> testHistory = examService.getTestHistory(userId);
            log.info("Returning test history - count: {}", testHistory.size());
            return ResponseEntity.ok(testHistory);
        } catch (Exception e) {
            log.error("Error getting test history: ", e);
            return ResponseEntity.status(500).build();
        }
    }
}