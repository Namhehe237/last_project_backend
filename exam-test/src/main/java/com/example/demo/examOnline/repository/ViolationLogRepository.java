package com.example.demo.examOnline.repository;

import com.example.demo.examOnline.domain.ViolationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ViolationLogRepository extends JpaRepository<ViolationLog, Integer> {
    
    List<ViolationLog> findByExamExamIdAndStudentUserId(Integer examId, Integer studentId);
    
    List<ViolationLog> findByExamExamId(Integer examId);
    
    @Query("SELECT COUNT(v) FROM ViolationLog v WHERE v.exam.examId = :examId AND v.student.userId = :studentId")
    Long countByExamIdAndStudentId(@Param("examId") Integer examId, @Param("studentId") Integer studentId);
    
    @Query("SELECT COUNT(v) FROM ViolationLog v WHERE v.exam.examId = :examId")
    Long countByExamId(@Param("examId") Integer examId);
}

