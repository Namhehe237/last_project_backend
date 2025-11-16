package com.example.demo.examOnline.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestHistoryResponse {
    private Integer examId;
    private String examName;
    private String subjectName;
    private Double score;
    private Integer totalQuestions;
    private Integer correctAnswers;
    private LocalDateTime submitTime;
    private String videoUrl;
    private String status; // SUBMITTED, GRADED, etc.
}

