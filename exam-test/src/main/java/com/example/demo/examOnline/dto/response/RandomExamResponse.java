package com.example.demo.examOnline.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.example.demo.examOnline.domain.enums.DifficultyLevel;
import com.example.demo.examOnline.domain.enums.ExamStatus;
import com.example.demo.examOnline.domain.enums.QuestionType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RandomExamResponse {
    private Integer examId;
    private String examName;
    private String subjectName;
    private Integer durationMinutes;
    private Integer maxAttempts;
    private String className;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private ExamStatus examStatus;
    private List<QuestionSummary> questions;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionSummary {
        private Integer questionId;
        private String questionText;
        private QuestionType questionType;
        private DifficultyLevel difficultyLevel;
        private String subjectName;
    }
}

