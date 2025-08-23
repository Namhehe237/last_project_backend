package com.example.demo.examOnline.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.examOnline.dto.request.AddQuestionRequest;
import com.example.demo.examOnline.service.QuestionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/question")
@RequiredArgsConstructor
public class QuestionController {
    
    private final QuestionService questionService;

    @PostMapping("/add-question")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<String> addQuestion(@RequestBody AddQuestionRequest request){

        questionService.addQuestion(request);

        return ResponseEntity.ok("Thêm câu hỏi thành công");
    } 

}
