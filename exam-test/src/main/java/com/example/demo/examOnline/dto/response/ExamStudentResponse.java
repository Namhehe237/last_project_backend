package com.example.demo.examOnline.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamStudentResponse {
    private Integer studentId;
    private String studentName;
    private String studentEmail;
    private Boolean hasViolations;
    private Long violationCount;
    private Boolean hasTakenExam;
    private String videoUrl;
}

