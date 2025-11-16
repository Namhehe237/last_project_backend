package com.example.demo.examOnline.repository;

import com.example.demo.examOnline.domain.StudentAnswer;
import com.example.demo.examOnline.domain.StudentExam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentAnswerRepository extends JpaRepository<StudentAnswer, Integer> {
    List<StudentAnswer> findByStudentExam(StudentExam studentExam);
    
    @Query("SELECT sa FROM StudentAnswer sa WHERE sa.studentExam.id.studentId = :studentId AND sa.studentExam.id.examId = :examId")
    List<StudentAnswer> findByStudentIdAndExamId(@Param("studentId") Integer studentId, @Param("examId") Integer examId);
}

