package com.example.demo.examOnline.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.examOnline.dto.request.CreateExamRequest;
import com.example.demo.examOnline.dto.response.ExamResponse;
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
}
