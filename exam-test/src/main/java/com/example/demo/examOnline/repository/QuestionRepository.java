package com.example.demo.examOnline.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.examOnline.domain.QuestionsBank;
import com.example.demo.examOnline.domain.enums.DifficultyLevel;

public interface QuestionRepository extends JpaRepository<QuestionsBank, Integer> {
    boolean existsByQuestionText(String questionText);

    @Query(value = "SELECT q.questionId, q.questionText, q.questionType, q.difficultyLevel, " +
            "q.subjectName, t.fullName, a.answerText, a.isCorrect " +
            "FROM QuestionsBank q " +
            "LEFT JOIN q.teacher t " +
            "LEFT JOIN q.answers a " +
            "WHERE (:level IS NULL OR q.difficultyLevel = :level) " +
            "AND (:subject IS NULL OR q.subjectName = :subject) " +
            "AND (:teacherName IS NULL OR t.fullName = :teacherName)", countQuery = "SELECT COUNT(q) FROM QuestionsBank q "
                    +
                    "LEFT JOIN q.teacher t " +
                    "WHERE (:level IS NULL OR q.difficultyLevel = :level) " +
                    "AND (:subject IS NULL OR q.subjectName = :subject) " +
                    "AND (:teacherName IS NULL OR t.fullName = :teacherName)")
    Page<Object[]> findQuestionsByFilters(@Param("level") DifficultyLevel level,
            @Param("subject") String subject,
            @Param("teacherName") String teacherName,
            Pageable pageable);

}
