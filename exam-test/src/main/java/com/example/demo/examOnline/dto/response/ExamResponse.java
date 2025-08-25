package com.example.demo.examOnline.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public class ExamResponse {
    private String examName;
    private String subjectName;
    private Integer durationMinutes;
    private Integer maxAttempts;
    private String className;
    private String teacherName;
    private List<Integer> questionId;
    private LocalDateTime createdAt;
}
