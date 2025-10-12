package com.example.demo.examOnline.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.examOnline.dto.request.CreateExamRequest;
import com.example.demo.examOnline.dto.request.ExamFilterRequest;
import com.example.demo.examOnline.dto.response.ExamResponse;
import com.example.demo.examOnline.dto.cache.ExamSnapshot;
import com.example.demo.examOnline.dto.response.AnswerSnapshotResponse;
import com.example.demo.examOnline.dto.response.QuestionSnapshotResponse;
import com.example.demo.examOnline.dto.response.ExamSnapshotResponse;
import com.example.demo.examOnline.dto.response.ExamPaperResponse;
import com.example.demo.examOnline.dto.request.SubmitExamRequest;
import com.example.demo.examOnline.dto.response.GradeExamResponse;
import com.example.demo.examOnline.service.ExamService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/exam")
@RequiredArgsConstructor
public class ExamController {
    private final ExamService examService;

    @PostMapping("/create-exam")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<String> createExam(@RequestBody CreateExamRequest request) {

        examService.createExam(request);

        return ResponseEntity.ok("Thêm bài kiểm tra thành công");
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
    public ResponseEntity<ExamPaperResponse> getExamPaper(@PathVariable Integer examId) {
        return ResponseEntity.ok(examService.getExamPaper(examId));
    }

    @PostMapping("/grade")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<GradeExamResponse> gradeExam(@RequestBody SubmitExamRequest request) {
        return ResponseEntity.ok(examService.gradeExam(request));
    }
}