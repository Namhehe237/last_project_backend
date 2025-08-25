package com.example.demo.examOnline.service;

import org.springframework.stereotype.Service;

import com.example.demo.examOnline.repository.ExamRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExamService {
    private final ExamRepository examRepository;

    // public 
}
