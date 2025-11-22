package com.example.demo.examOnline.service;

import com.example.demo.examOnline.domain.Exam;
import com.example.demo.examOnline.domain.User;
import com.example.demo.examOnline.domain.ViolationLog;
import com.example.demo.examOnline.dto.request.LogViolationRequest;
import com.example.demo.examOnline.dto.response.ExamStudentResponse;
import com.example.demo.examOnline.dto.response.TeacherExamResponse;
import com.example.demo.examOnline.dto.response.ViolationLogResponse;
import com.example.demo.examOnline.repository.ExamRepository;
import com.example.demo.examOnline.repository.StudentExamRepository;
import com.example.demo.examOnline.repository.StudentClassRepository;
import com.example.demo.examOnline.repository.UserRepository;
import com.example.demo.examOnline.repository.ViolationLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ViolationLogService {
    private final ViolationLogRepository violationLogRepository;
    private final ExamRepository examRepository;
    private final UserRepository userRepository;
    private final StudentExamRepository studentExamRepository;
    private final StudentClassRepository studentClassRepository;

    @Transactional
    public void logViolation(LogViolationRequest request) {
        log.info("=== ViolationLogService.logViolation called ===");
        log.info("Request: examId={}, studentId={}, type={}, message={}, sessionId={}", 
                request.getExamId(), request.getStudentId(), request.getViolationType(),
                request.getMessage(), request.getSessionId());
        
        Exam exam = examRepository.findById(request.getExamId())
                .orElseThrow(() -> {
                    log.error("Exam not found: {}", request.getExamId());
                    return new RuntimeException("Exam not found: " + request.getExamId());
                });
        log.info("Exam found: examId={}, examName={}", exam.getExamId(), exam.getExamName());
        
        User student = userRepository.findById(request.getStudentId())
                .orElseThrow(() -> {
                    log.error("Student not found: {}", request.getStudentId());
                    return new RuntimeException("Student not found: " + request.getStudentId());
                });
        log.info("Student found: studentId={}, studentName={}", student.getUserId(), student.getFullName());
        
        ViolationLog violationLog = ViolationLog.builder()
                .exam(exam)
                .student(student)
                .violationType(request.getViolationType())
                .message(request.getMessage())
                .sessionId(request.getSessionId())
                .build();
        
        log.info("Saving violation log: type={}", violationLog.getViolationType());
        ViolationLog saved = violationLogRepository.save(violationLog);
        log.info("=== Violation logged successfully: violationId={} ===", saved.getViolationId());
    }

    public List<ViolationLogResponse> getViolationsByExamAndStudent(Integer examId, Integer studentId) {
        log.info("Getting violations for examId={}, studentId={}", examId, studentId);
        
        List<ViolationLog> violations = violationLogRepository.findByExamExamIdAndStudentUserId(examId, studentId);
        
        return violations.stream()
                .map(v -> ViolationLogResponse.builder()
                        .violationId(v.getViolationId())
                        .violationType(v.getViolationType())
                        .message(v.getMessage())
                        .timestamp(v.getTimestamp())
                        .sessionId(v.getSessionId())
                        .build())
                .collect(Collectors.toList());
    }

    public List<ExamStudentResponse> getStudentsByExam(Integer examId) {
        log.info("Getting students for examId={}", examId);
        
        // Get exam to find classId
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found: " + examId));
        
        Integer classId = exam.getClassEntity().getClassId();
        
        // Get ALL students in the class (not just those who took the exam)
        List<com.example.demo.examOnline.domain.StudentClass> studentClasses = 
                studentClassRepository.findByClassId(classId);
        
        // Get all students who took the exam with video URLs
        List<com.example.demo.examOnline.domain.StudentExam> studentExams = 
                studentExamRepository.findByExamExamId(examId);
        Set<Integer> studentsWhoTookExam = studentExams.stream()
                .map(se -> se.getStudent().getUserId())
                .collect(Collectors.toSet());
        
        // Create a map of studentId -> videoUrl for quick lookup
        Map<Integer, String> studentVideoUrlMap = studentExams.stream()
                .filter(se -> se.getVideoUrl() != null && !se.getVideoUrl().isEmpty())
                .collect(Collectors.toMap(
                        se -> se.getStudent().getUserId(),
                        com.example.demo.examOnline.domain.StudentExam::getVideoUrl,
                        (existing, replacement) -> existing // Keep first if duplicate
                ));
        
        return studentClasses.stream()
                .map(sc -> {
                    Integer studentId = sc.getStudent().getUserId();
                    Long violationCount = violationLogRepository.countByExamIdAndStudentId(examId, studentId);
                    Boolean hasTakenExam = studentsWhoTookExam.contains(studentId);
                    String videoUrl = studentVideoUrlMap.getOrDefault(studentId, null);
                    
                    return ExamStudentResponse.builder()
                            .studentId(studentId)
                            .studentName(sc.getStudent().getFullName())
                            .studentEmail(sc.getStudent().getEmail())
                            .hasViolations(violationCount > 0)
                            .violationCount(violationCount)
                            .hasTakenExam(hasTakenExam)
                            .videoUrl(videoUrl)
                            .build();
                })
                .collect(Collectors.toList());
    }

    public List<TeacherExamResponse> getExamsByTeacher(Integer teacherId) {
        log.info("Getting exams for teacherId={}", teacherId);
        
        List<Exam> exams = examRepository.findByTeacherUserId(teacherId);
        
        return exams.stream()
                .map(exam -> {
                    List<com.example.demo.examOnline.domain.StudentExam> studentExams = 
                            studentExamRepository.findByExamExamId(exam.getExamId());
                    Long studentCount = (long) studentExams.stream()
                            .map(se -> se.getStudent().getUserId())
                            .distinct()
                            .count();
                    
                    Long violationCount = violationLogRepository.countByExamId(exam.getExamId());
                    
                    return TeacherExamResponse.builder()
                            .examId(exam.getExamId())
                            .examName(exam.getExamName())
                            .subjectName(exam.getSubjectName())
                            .className(exam.getClassEntity().getClassName())
                            .createdAt(exam.getCreatedAt())
                            .studentCount(studentCount)
                            .violationCount(violationCount)
                            .build();
                })
                .collect(Collectors.toList());
    }
}

