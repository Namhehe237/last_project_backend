package com.example.demo.examOnline.controller;

import java.io.IOException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.examOnline.dto.request.AddQuestionRequest;
import com.example.demo.examOnline.dto.request.DeleteQuestionRequest;
import com.example.demo.examOnline.dto.request.getListQuestionRequest;
import com.example.demo.examOnline.dto.response.QuestionResponse;
import com.example.demo.examOnline.service.QuestionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/question")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping("/add-question")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<String> addQuestion(@RequestBody AddQuestionRequest request) {

        questionService.addQuestion(request);

        return ResponseEntity.ok("Thêm câu hỏi thành công");
    }

    @PostMapping("/list-question")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<Page<QuestionResponse>> listQuestion(
            @RequestBody getListQuestionRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageRequest pageable = PageRequest.of(page, size);

        Page<QuestionResponse> result = questionService.getQuestionsByFilters(
                request.getDifficultyLevel(),
                request.getSubjectName(),
                request.getTeacherName(),
                pageable);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/update/{questionId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<String> updateQuestion(@PathVariable Integer questionId,
            @RequestBody AddQuestionRequest request) {
        questionService.updateQuestionWithFetch(questionId, request);

        return ResponseEntity.ok("Update câu hỏi thành công");
    }

    @DeleteMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<String> deleteQuestions(@RequestBody DeleteQuestionRequest request) {
        questionService.deleteQuestions(request.getQuestionId());
        return ResponseEntity.ok("Deleted successfully");
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadExcel(@RequestParam("file") MultipartFile file) {
        try {
            questionService.parseExcelFile(file);
            return ResponseEntity.ok("Import questions successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/template")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<byte[]> downloadTemplate() throws IOException {
        byte[] data = questionService.generateQuestionTemplate();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=question-template.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }

}
