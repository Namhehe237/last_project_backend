package com.example.demo.examOnline.dto.request;

import java.util.List;

public class CreateExamRequest {
    private String examName;
    private String subjectName;
    private Integer durationMinutes;
    private Integer maxAttempts;
    private String className;
    private String teacherName;
    private List<Integer> questionId;
}
