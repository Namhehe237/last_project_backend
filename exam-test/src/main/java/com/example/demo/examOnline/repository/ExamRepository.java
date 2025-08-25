package com.example.demo.examOnline.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.examOnline.domain.Exam;

public interface ExamRepository extends JpaRepository<Exam,Integer> {

    
}