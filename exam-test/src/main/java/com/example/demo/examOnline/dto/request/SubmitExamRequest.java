package com.example.demo.examOnline.dto.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitExamRequest {
    private Integer examId;
    private Integer studentId;
    // pairs of questionId -> chosenAnswerId
    private List<SubmitItem> answers;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SubmitItem {
        private Integer questionId;
        private Integer answerId;
    }
}


