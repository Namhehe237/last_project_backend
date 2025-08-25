package com.example.demo.examOnline.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.examOnline.dto.request.CreateExamRequest;
import com.example.demo.examOnline.service.ExamService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/exam")
@RequiredArgsConstructor
public class ExamController {
    private final ExamService examService;

    public void createExam(CreateExamRequest request){
        
    }
}
