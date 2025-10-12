package com.example.demo.examOnline.dto.response;

import java.time.LocalDateTime;
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
public class ExamSnapshotResponse {
    private Integer examId;
    private String examName;
    private String subjectName;
    private Integer durationMinutes;
    private Integer maxAttempts;
    private String className;
    private String teacherName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<QuestionSnapshotResponse> questions;
}


