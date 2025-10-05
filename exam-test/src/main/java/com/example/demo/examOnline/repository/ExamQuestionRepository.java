package com.example.demo.examOnline.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.examOnline.domain.ExamQuestion;
import com.example.demo.examOnline.domain.ExamQuestionId;

public interface ExamQuestionRepository extends JpaRepository<ExamQuestion, ExamQuestionId> {

    @Query("SELECT q.id.questionId FROM ExamQuestion q WHERE q.exam.examId = :examId")
    List<Integer> getExamQuestion(@Param("examId") Integer examId);

}
