package com.example.demo.examOnline.controller;

import com.example.demo.examOnline.dto.request.LogViolationRequest;
import com.example.demo.examOnline.dto.response.ExamStudentResponse;
import com.example.demo.examOnline.dto.response.TeacherExamResponse;
import com.example.demo.examOnline.dto.response.ViolationLogResponse;
import com.example.demo.examOnline.service.ViolationLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class ViolationLogController {
    private final ViolationLogService violationLogService;

    @PostMapping("/violation/log")
    public ResponseEntity<String> logViolation(@RequestBody LogViolationRequest request) {
        try {
            log.info("=== Received violation log request ===");
            log.info("Request body: {}", request);
            log.info("examId={}, studentId={}, violationType={}, message={}, sessionId={}", 
                    request.getExamId(), request.getStudentId(), request.getViolationType(), 
                    request.getMessage(), request.getSessionId());
            
            if (request.getExamId() == null || request.getStudentId() == null || request.getViolationType() == null) {
                log.error("Missing required fields: examId={}, studentId={}, violationType={}", 
                        request.getExamId(), request.getStudentId(), request.getViolationType());
                return ResponseEntity.badRequest().body("Missing required fields");
            }
            
            violationLogService.logViolation(request);
            log.info("=== Violation logged successfully ===");
            return ResponseEntity.ok("Violation logged successfully");
        } catch (Exception e) {
            log.error("Error logging violation: ", e);
            log.error("Exception details: {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to log violation: " + e.getMessage());
        }
    }

    @GetMapping("/exam/{examId}/students")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<List<ExamStudentResponse>> getExamStudents(@PathVariable Integer examId) {
        try {
            log.info("Getting students for examId={}", examId);
            List<ExamStudentResponse> students = violationLogService.getStudentsByExam(examId);
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            log.error("Error getting exam students: ", e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/exam/{examId}/student/{studentId}/violations")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<List<ViolationLogResponse>> getStudentViolations(
            @PathVariable Integer examId,
            @PathVariable Integer studentId) {
        try {
            log.info("Getting violations for examId={}, studentId={}", examId, studentId);
            List<ViolationLogResponse> violations = violationLogService.getViolationsByExamAndStudent(examId, studentId);
            return ResponseEntity.ok(violations);
        } catch (Exception e) {
            log.error("Error getting student violations: ", e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/teacher/{teacherId}/exams")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<List<TeacherExamResponse>> getTeacherExams(@PathVariable Integer teacherId) {
        try {
            log.info("Getting exams for teacherId={}", teacherId);
            List<TeacherExamResponse> exams = violationLogService.getExamsByTeacher(teacherId);
            return ResponseEntity.ok(exams);
        } catch (Exception e) {
            log.error("Error getting teacher exams: ", e);
            return ResponseEntity.status(500).build();
        }
    }
}

