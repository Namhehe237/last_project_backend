package com.example.demo.examOnline.dto.request;

import java.time.LocalDateTime;
import java.util.List;

import com.example.demo.examOnline.domain.enums.ExamStatus;

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
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private ExamStatus examStatus;
    private String teacherName;
    private List<Integer> questionId;
}
