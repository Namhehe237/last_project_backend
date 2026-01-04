package com.example.demo.examOnline.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.*;
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

  @Query("SELECT NEW com.example.demo.examOnline.dto.response.ExamResponse( " +
      " e.examId, e.examName, e.subjectName, e.durationMinutes, " +
      " e.classEntity.className, e.teacher.fullName) FROM Exam e ")
  Page<ExamResponse> getListExam(Pageable pageable);

  @Query("""
      SELECT DISTINCT NEW com.example.demo.examOnline.dto.response.ExamResponse(
        e.examId, e.examName, e.subjectName, e.durationMinutes,
        e.classEntity.className, e.teacher.fullName, e.startTime
      )
      FROM Exam e
      WHERE (:classId IS NULL OR e.classEntity.classId = :classId)
        AND (:studentId IS NULL OR e.classEntity.classId IN (
          SELECT sc.classEntity.classId
          FROM StudentClass sc
          WHERE sc.student.userId = :studentId
        ))
        AND (:studentId IS NULL OR NOT EXISTS (
          SELECT 1 FROM StudentExam se
          WHERE se.exam.examId = e.examId
            AND se.student.userId = :studentId
            AND se.status IN (com.example.demo.examOnline.domain.enums.StudentExamStatus.SUBMITTED,
                              com.example.demo.examOnline.domain.enums.StudentExamStatus.GRADED)
        ))
        AND e.startTime < :checkTime 
        AND (e.endTime IS NULL OR e.endTime > :checkTime)
      """)
  Page<ExamResponse> getListExams(
      @Param("classId") Integer classId,
      @Param("studentId") Integer studentId,
      @Param("checkTime") LocalDateTime checkTime,
      Pageable pageable);

  @Query("SELECT e FROM Exam e WHERE e.teacher.userId = :teacherId")
  List<Exam> findByTeacherUserId(@Param("teacherId") Integer teacherId);

}