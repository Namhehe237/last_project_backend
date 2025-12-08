package com.example.demo.examOnline.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamResultDetailResponse {
    private Integer examId;
    private String examName;
    private String subjectName;
    private Double score;
    private Integer totalQuestions;
    private Integer correctAnswers;
    private LocalDateTime submitTime;
    private List<QuestionResultDetail> questions;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionResultDetail {
        private Integer questionId;
        private String questionText;
        private List<AnswerDetail> answers;
        private Integer chosenAnswerId; // null if not answered
        private Integer correctAnswerId; // The correct answer ID
        private Boolean isCorrect; // Whether the chosen answer is correct
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnswerDetail {
        private Integer answerId;
        private String answerText;
        private Boolean isCorrect;
    }
}
