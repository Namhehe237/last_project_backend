package com.example.demo.examOnline.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.examOnline.domain.Exam;
import com.example.demo.examOnline.dto.response.ExamResponse;

public interface ExamRepository extends JpaRepository<Exam, Integer> {

    @Query("SELECT NEW com.example.demo.examOnline.dto.response.ExamResponse( " +
            " e.examId, e.examName, e.subjectName, e.durationMinutes, e.maxAttempts, " +
            " e.classEntity.className, e.teacher.fullName, e.createdAt) " +
            " FROM Exam e WHERE e.examId = :examId")
    ExamResponse getExamDetail(@Param("examId") Integer examId);

}