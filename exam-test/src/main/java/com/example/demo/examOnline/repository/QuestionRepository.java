package com.example.demo.examOnline.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

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

        @Modifying
        @Transactional
        @Query("DELETE FROM QuestionsBank q WHERE q.questionId IN :ids")
        void deleteByIds(@Param("ids") List<Integer> ids);

        @Query("""
                        SELECT q FROM QuestionsBank q
                        WHERE q.difficultyLevel = :difficultyLevel
                        ORDER BY function('RAND')
                        """)
        List<QuestionsBank> findRandomQuestionsByDifficulty(
                        @Param("difficultyLevel") DifficultyLevel difficultyLevel,
                        Pageable pageable);
}
