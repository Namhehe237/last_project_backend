package com.example.demo.examOnline.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.demo.examOnline.dto.request.student.ChangePasswordRequest;
import com.example.demo.examOnline.dto.request.student.ForgotPasswordRequest;
import com.example.demo.examOnline.dto.request.student.StudentRegisterRequest;
import com.example.demo.examOnline.dto.request.student.UpdateStudentProfileRequest;
import com.example.demo.examOnline.dto.request.RequestJoinClassRequest;
import com.example.demo.examOnline.dto.response.AuthResponse;
import com.example.demo.examOnline.dto.response.MessageResponse;
import com.example.demo.examOnline.dto.response.ClassResponseDTO;
import com.example.demo.examOnline.dto.response.student.StudentProfileResponse;
import com.example.demo.examOnline.service.StudentService;
import com.example.demo.examOnline.service.ClassManagementService;
import com.example.demo.examOnline.dto.response.UserResponseDTO;
import com.example.demo.examOnline.dto.response.AssignmentResponseDTO;
import com.example.demo.examOnline.dto.response.AnnouncementResponseDTO;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StudentController {

    private final StudentService studentService;
    private final ClassManagementService classManagementService;

    // 1. Đăng ký tài khoản mới
    @PostMapping("/register")
    public ResponseEntity<?> registerStudent(@Valid @RequestBody StudentRegisterRequest request) {
        try {
            AuthResponse response = studentService.registerStudent(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    MessageResponse.builder()
                            .message("Đăng ký thất bại: " + e.getMessage())
                            .success(false)
                            .build());
        }
    }

    // 2. Đăng nhập (sử dụng AuthController hiện có)
    // 3. Đăng xuất
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

    // 4. Quên mật khẩu
    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        try {
            MessageResponse response = studentService.forgotPassword(request.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    MessageResponse.builder()
                            .message("Yêu cầu quên mật khẩu thất bại: " + e.getMessage())
                            .success(false)
                            .build());
        }
    }

    // 5. Đổi mật khẩu
    @PostMapping("/change-password")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<MessageResponse> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        try {
            MessageResponse response = studentService.changePassword(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    MessageResponse.builder()
                            .message("Đổi mật khẩu thất bại: " + e.getMessage())
                            .success(false)
                            .build());
        }
    }

    // 6. Xem thông tin cá nhân
    @GetMapping("/profile")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> getProfile() {
        try {
            StudentProfileResponse response = studentService.getCurrentStudentProfile();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    MessageResponse.builder()
                            .message("Lấy thông tin thất bại: " + e.getMessage())
                            .success(false)
                            .build());
        }
    }

    // 7. Cập nhật thông tin cá nhân
    @PutMapping("/profile")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody UpdateStudentProfileRequest request) {
        try {
            StudentProfileResponse response = studentService.updateStudentProfile(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    MessageResponse.builder()
                            .message("Cập nhật thông tin thất bại: " + e.getMessage())
                            .success(false)
                            .build());
        }
    }

    // ==================== QUẢN LÝ LỚP HỌC ====================

    // 8. Gửi yêu cầu tham gia lớp học (cần phê duyệt)
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
    @GetMapping("/classes")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> getMyClasses() {
        try {
            List<ClassResponseDTO> classes = classManagementService.getStudentClasses();
            return ResponseEntity.ok(classes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    MessageResponse.builder()
                            .message("Lấy danh sách lớp học thất bại: " + e.getMessage())
                            .success(false)
                            .build());
        }
    }

    // 10. Xem chi tiết lớp học
    @GetMapping("/classes/{classId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> getClassDetails(@PathVariable Integer classId) {
        try {
            ClassResponseDTO classDetails = classManagementService.getClassDetails(classId);
            return ResponseEntity.ok(classDetails);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    MessageResponse.builder()
                            .message("Lấy thông tin lớp học thất bại: " + e.getMessage())
                            .success(false)
                            .build());
        }
    }

    // 11. Rời khỏi lớp học
    @DeleteMapping("/classes/{classId}/leave")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<MessageResponse> leaveClass(@PathVariable Integer classId) {
        try {
            MessageResponse response = classManagementService.leaveClass(classId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    MessageResponse.builder()
                            .message("Rời khỏi lớp học thất bại: " + e.getMessage())
                            .success(false)
                            .build());
        }
    }

    // 12. Xem danh sách học sinh trong lớp
    @GetMapping("/classes/{classId}/students")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> getClassStudents(@PathVariable Integer classId, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            PageRequest pageable = PageRequest.of(page, size);
            Page<UserResponseDTO> students = classManagementService.getClassStudents(classId, pageable);
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    MessageResponse.builder()
                            .message("Lấy danh sách học sinh thất bại: " + e.getMessage())
                            .success(false)
                            .build());
        }
    }

    // 13. Xem danh sách bài tập trong lớp
    @GetMapping("/classes/{classId}/assignments")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> getClassAssignments(@PathVariable Integer classId) {
        try {
            List<AssignmentResponseDTO> assignments = classManagementService.getClassAssignments(classId);
            return ResponseEntity.ok(assignments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    MessageResponse.builder()
                            .message("Lấy danh sách bài tập thất bại: " + e.getMessage())
                            .success(false)
                            .build());
        }
    }

    // 14. Xem bảng tin lớp học
    @GetMapping("/classes/{classId}/announcements")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> getClassAnnouncements(@PathVariable Integer classId) {
        try {
            List<AnnouncementResponseDTO> announcements = classManagementService.getClassAnnouncements(classId);
            return ResponseEntity.ok(announcements);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    MessageResponse.builder()
                            .message("Lấy bảng tin thất bại: " + e.getMessage())
                            .success(false)
                            .build());
        }
    }

    // Các API hiện có
    @GetMapping("/courses")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public String viewCourses() {
        return "View Courses - All authenticated users can access";
    }

    @GetMapping("/grades")
    @PreAuthorize("hasRole('STUDENT')")
    public String viewOwnGrades() {
        return "View Own Grades - Only STUDENT can access";
    }
}