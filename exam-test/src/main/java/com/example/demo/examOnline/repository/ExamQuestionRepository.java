package com.example.demo.examOnline.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.examOnline.domain.ExamQuestion;
import com.example.demo.examOnline.domain.ExamQuestionId;

public interface ExamQuestionRepository extends JpaRepository<ExamQuestion, ExamQuestionId> {
    
}
