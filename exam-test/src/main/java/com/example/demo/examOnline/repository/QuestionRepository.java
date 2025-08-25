package com.example.demo.examOnline.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.examOnline.domain.QuestionsBank;
import com.example.demo.examOnline.domain.enums.DifficultyLevel;

public interface QuestionRepository extends JpaRepository<QuestionsBank, Integer> {
        boolean existsByQuestionText(String questionText);

        @EntityGraph(attributePaths = { "teacher", "answers" })
        @Query("SELECT DISTINCT q FROM QuestionsBank q " +
                        "LEFT JOIN q.teacher t " +
                        "WHERE (:level IS NULL OR q.difficultyLevel = :level) " +
                        "AND (:subject IS NULL OR q.subjectName = :subject) " +
                        "AND (:teacherName IS NULL OR t.fullName = :teacherName)")
        Page<QuestionsBank> findQuestionsByFiltersWithAnswers(@Param("level") DifficultyLevel level,
                        @Param("subject") String subject,
                        @Param("teacherName") String teacherName,
                        Pageable pageable);

        @Query("SELECT CASE WHEN COUNT(q) > 0 THEN TRUE ELSE FALSE END " +
                        "FROM QuestionsBank q " +
                        "WHERE q.questionText = :questionText AND q.questionId <> :questionId")
        boolean existsByQuestionTextAndNotId(@Param("questionText") String questionText,
                        @Param("questionId") Integer questionId);

        @Query("SELECT q FROM QuestionsBank q LEFT JOIN FETCH q.answers WHERE q.questionId = :questionId")
        Optional<QuestionsBank> findByIdWithAnswers(@Param("questionId") Integer questionId);
}
