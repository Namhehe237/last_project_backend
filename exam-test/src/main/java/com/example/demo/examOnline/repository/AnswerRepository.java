package com.example.demo.examOnline.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.examOnline.domain.Answer;

public interface AnswerRepository extends JpaRepository<Answer, Integer> {
    
}
