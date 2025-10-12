package com.example.demo.examOnline.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradeExamResponse {
    private Integer examId;
    private Integer totalQuestions;
    private Integer correctAnswers;
    private Double score; // 0-10
    private List<ResultDetail> details;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ResultDetail {
        private Integer questionId;
        private Integer chosenAnswerId;
        private boolean correct;
    }
}


