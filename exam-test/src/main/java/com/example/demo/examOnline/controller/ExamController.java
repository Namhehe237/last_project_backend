package com.example.demo.examOnline.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import lombok.extern.slf4j.Slf4j;

import com.example.demo.examOnline.dto.request.CreateExamRequest;
import com.example.demo.examOnline.dto.request.CreateRandomExamRequest;
import com.example.demo.examOnline.dto.request.ExamFilterRequest;
import com.example.demo.examOnline.dto.response.ExamResponse;
import com.example.demo.examOnline.dto.cache.ExamSnapshot;
import com.example.demo.examOnline.dto.response.AnswerSnapshotResponse;
import com.example.demo.examOnline.dto.response.QuestionSnapshotResponse;
import com.example.demo.examOnline.dto.response.ExamSnapshotResponse;
import com.example.demo.examOnline.dto.response.ExamPaperResponse;
import com.example.demo.examOnline.dto.request.SubmitExamRequest;
import com.example.demo.examOnline.dto.request.ForceSubmitExamRequest;
import com.example.demo.examOnline.dto.response.GradeExamResponse;
import com.example.demo.examOnline.dto.response.ExamResultDetailResponse;
import com.example.demo.examOnline.dto.response.RandomExamResponse;
import com.example.demo.examOnline.dto.request.UpdateExamQuestionsRequest;
import com.example.demo.examOnline.service.ExamService;
import com.example.demo.examOnline.service.impl.UserService;
import com.example.demo.examOnline.service.CloudinaryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/exam")
@RequiredArgsConstructor
@Slf4j
public class ExamController {
    private final ExamService examService;
    private final CloudinaryService cloudinaryService;
    private final UserService userService;

    @PostMapping("/create-exam")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<String> createExam(@RequestBody CreateExamRequest request) {
        log.info("=== CONTROLLER: Received create-exam request ===");
        log.info("Request: examName={}, className={}, teacherId={}", 
                request.getExamName(), request.getClassName(), request.getTeacherId());
        System.out.println("=== SYSTEM.OUT: create-exam endpoint called ===");
        System.out.println("Exam Name: " + request.getExamName());
        System.out.println("Class Name: " + request.getClassName());
        
        examService.createExam(request);
        
        log.info("=== CONTROLLER: Exam created successfully ===");
        System.out.println("=== SYSTEM.OUT: Exam created successfully ===");
        return ResponseEntity.ok("Thêm bài kiểm tra thành công");
    }

    @PostMapping("/create-random")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<RandomExamResponse> createRandomExam(@RequestBody CreateRandomExamRequest request) {
        return ResponseEntity.ok(examService.createRandomExam(request));
    }

    @PutMapping("/questions/{examId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<String> updateExamQuestions(@PathVariable Integer examId,
            @RequestBody UpdateExamQuestionsRequest request) {
        examService.updateExamQuestions(examId, request);
        return ResponseEntity.ok("Cập nhật câu hỏi bài thi thành công");
    }

    @DeleteMapping("/delete-exam/{examId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<String> deleteExam(@PathVariable Integer examId) {
        examService.deleteExam(examId);
        return ResponseEntity.ok("Huỷ bài kiểm tra thành công");
    }

    @PostMapping("/get-exam-details/{examId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<ExamResponse> getExamDetail(@PathVariable Integer examId) {
        ExamResponse examResponse = examService.getExamDetail(examId);
        return ResponseEntity.ok(examResponse);
    }

    @PostMapping("/get-all-exam")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<Page<ExamResponse>> getAllExamTest(@RequestBody ExamFilterRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(examService.getListExam(request, pageable));
    }

    @GetMapping("/snapshot/{examId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<ExamSnapshotResponse> getExamSnapshot(@PathVariable Integer examId) {
        ExamSnapshot s = examService.getOrBuildExamSnapshot(examId);
        ExamSnapshotResponse resp = ExamSnapshotResponse.builder()
                .examId(s.getExamId())
                .examName(s.getExamName())
                .subjectName(s.getSubjectName())
                .durationMinutes(s.getDurationMinutes())
                .maxAttempts(s.getMaxAttempts())
                .className(s.getClassName())
                .teacherName(s.getTeacherName())
                .startTime(s.getStartTime())
                .endTime(s.getEndTime())
                .questions(s.getQuestions().stream().map(q -> QuestionSnapshotResponse.builder()
                        .questionId(q.getQuestionId())
                        .questionText(q.getQuestionText())
                        .questionType(q.getQuestionType())
                        .difficultyLevel(q.getDifficultyLevel())
                        .subjectName(q.getSubjectName())
                        .teacherName(q.getTeacherName())
                        .answers(q.getAnswers().stream().map(a -> AnswerSnapshotResponse.builder()
                                .answerId(a.getAnswerId())
                                .answerText(a.getAnswerText())
                                .correct(a.isCorrect())
                                .build()).toList())
                        .build()).toList())
                .build();
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/paper/{examId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<ExamPaperResponse> getExamPaper(
            @PathVariable Integer examId,
            @RequestParam(required = false) Integer studentId) {
        return ResponseEntity.ok(examService.getExamPaper(examId, studentId));
    }

    @PostMapping("/grade")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<GradeExamResponse> gradeExam(@RequestBody SubmitExamRequest request) {
        return ResponseEntity.ok(examService.gradeExam(request));
    }

    @PostMapping("/force-submit")
    public ResponseEntity<GradeExamResponse> forceSubmitExam(@RequestBody ForceSubmitExamRequest request) {
        // Force submit exam with 0 points due to cheating violation
        return ResponseEntity.ok(examService.forceSubmitExam(request));
    }

    @PostMapping(value = "/upload-video", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<String> uploadVideo(
            @RequestParam("video") MultipartFile videoFile,
            @RequestParam("examId") Integer examId,
            @RequestParam("studentId") Integer studentId) {
        try {
            log.info("Received video upload request - examId: {}, studentId: {}, fileSize: {} bytes, contentType: {}", 
                    examId, studentId, videoFile.getSize(), videoFile.getContentType());
            
            if (videoFile.isEmpty()) {
                log.error("Video file is empty");
                return ResponseEntity.badRequest().body("Video file is empty");
            }
            
            if (examId == null || studentId == null) {
                log.error("Missing examId or studentId - examId: {}, studentId: {}", examId, studentId);
                return ResponseEntity.badRequest().body("Missing examId or studentId");
            }
            
            String folder = String.format("exam-recordings/exam-%d/student-%d", examId, studentId);
            String videoUrl = cloudinaryService.uploadVideo(videoFile, folder);

            examService.saveVideoUrl(examId, studentId, videoUrl);
            log.info("Video uploaded successfully - URL: {}", videoUrl);
            return ResponseEntity.ok(videoUrl);
        } catch (Exception e) {
            log.error("Error uploading video: ", e);
            return ResponseEntity.status(500).body("Failed to upload video: " + e.getMessage());
        }
    }

    @GetMapping("/result-detail/{examId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ExamResultDetailResponse> getExamResultDetail(@PathVariable Integer examId) {
        try {
            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            Integer studentId = userService.getUserIdByEmail(currentUserEmail);
            if (studentId == null) {
                log.error("User not found: {}", currentUserEmail);
                return ResponseEntity.status(401).build();
            }

            log.info("Received get exam result detail request - examId: {}, studentId: {}", examId, studentId);
            ExamResultDetailResponse result = examService.getExamResultDetail(examId, studentId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error getting exam result detail: ", e);
            return ResponseEntity.status(500).build();
        }
    }
}