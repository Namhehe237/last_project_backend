package com.example.demo.examOnline.dto.response;

import com.example.demo.examOnline.domain.enums.ExamStatus;
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
public class ExamResponse {
    private Integer examId;
    private String examName;
    private String subjectName;
    private Integer durationMinutes;
    private Double totalScore;
    private String startTime; // String format để tránh LocalDateTime serialization
    private String endTime; // String format để tránh LocalDateTime serialization
    private Boolean shuffleQuestions;
    private Boolean shuffleAnswers;
    private Integer maxAttempts;
    private ExamStatus status;
    private String createdAt; // String format
    private String updatedAt; // String format
    private String className;
    private String teacherName;
    private List<Integer> questionIds;
}