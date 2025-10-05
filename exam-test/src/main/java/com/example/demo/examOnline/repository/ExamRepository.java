package com.example.demo.examOnline.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.examOnline.domain.Exam;

public interface ExamRepository extends JpaRepository<Exam,Integer> {

    @Query("SELECT e FROM Exam e WHERE e.classEntity.classId = :classId")
    List<Exam> findByClassEntityClassId(@Param("classId") Integer classId);
    
}