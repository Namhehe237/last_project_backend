package com.example.demo.examOnline.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.examOnline.domain.QuestionsBank;

public interface QuestionRepository extends JpaRepository<QuestionsBank, Integer> {
    boolean existsByQuestionText(String questionText);
}
