package com.example.demo.examOnline.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
}