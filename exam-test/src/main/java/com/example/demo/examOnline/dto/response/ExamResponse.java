package com.example.demo.examOnline.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class ExamResponse {
    private Integer examId;
    private String examName;
    private String subjectName;
    private Integer durationMinutes;
    private Integer maxAttempts;
    private String className;
    private String teacherName;
    private List<Integer> questionId;
    private LocalDateTime startTime;

    public ExamResponse(Integer examId, String examName, String subjectName, Integer durationMinutes,
            Integer maxAttempts, String className, String teacherName, LocalDateTime createdAt) {
        this.examId = examId;
        this.examName = examName;
        this.subjectName = subjectName;
        this.durationMinutes = durationMinutes;
        this.maxAttempts = maxAttempts;
        this.className = className;
        this.teacherName = teacherName;
        this.startTime = createdAt;
        this.questionId = null;
    }

    public ExamResponse(Integer examId, String examName, String subjectName, Integer durationMinutes, String className,
            String teacherName) {
        this.examId = examId;
        this.examName = examName;
        this.subjectName = subjectName;
        this.durationMinutes = durationMinutes;
        this.className = className;
        this.teacherName = teacherName;
    }

    public ExamResponse(Integer examId, String examName, String subjectName, Integer durationMinutes, String className,
            String teacherName, LocalDateTime startTime) {
        this.examId = examId;
        this.examName = examName;
        this.subjectName = subjectName;
        this.durationMinutes = durationMinutes;
        this.className = className;
        this.teacherName = teacherName;
        this.startTime = startTime;
    }

}