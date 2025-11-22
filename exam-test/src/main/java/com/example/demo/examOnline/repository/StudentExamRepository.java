package com.example.demo.examOnline.repository;

import com.example.demo.examOnline.domain.StudentExam;
import com.example.demo.examOnline.domain.StudentExamId;
import com.example.demo.examOnline.domain.enums.StudentExamStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentExamRepository extends JpaRepository<StudentExam, StudentExamId> {
    Optional<StudentExam> findByStudentUserIdAndExamExamId(Integer studentId, Integer examId);
    
    List<StudentExam> findByStudentUserIdAndStatusIn(Integer studentId, List<StudentExamStatus> statuses);
    
    @Query("SELECT se FROM StudentExam se WHERE se.student.userId = :studentId AND se.status IN :statuses ORDER BY se.submitTime DESC")
    List<StudentExam> findTestHistoryByStudentId(@Param("studentId") Integer studentId, @Param("statuses") List<StudentExamStatus> statuses);
    
    @Query("SELECT se FROM StudentExam se WHERE se.exam.examId = :examId")
    List<StudentExam> findByExamExamId(@Param("examId") Integer examId);
}

