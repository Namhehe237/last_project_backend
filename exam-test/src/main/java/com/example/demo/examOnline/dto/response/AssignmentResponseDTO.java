package com.example.demo.examOnline.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentResponseDTO {
    private Integer assignmentId;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private LocalDateTime createdAt;
    private String status; // PENDING, SUBMITTED, GRADED
    private Integer totalPoints;
    private Integer earnedPoints;
    private String teacherName;
} 