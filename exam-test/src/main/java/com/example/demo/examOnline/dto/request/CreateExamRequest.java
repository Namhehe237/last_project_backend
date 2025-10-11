package com.example.demo.examOnline.dto.request;

import java.time.LocalDateTime;
import java.util.List;

import com.example.demo.examOnline.domain.enums.ExamStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateExamRequest {
    private String examName;
    private String subjectName;
    private Integer durationMinutes;
    private Integer maxAttempts;
    private String className;
    
    private LocalDateTime startTime; // Optional - có thể null
    private LocalDateTime endTime; // Optional - có thể null
    
    private ExamStatus examStatus;
    private Integer teacherId;
    private List<Integer> questionId;
}
