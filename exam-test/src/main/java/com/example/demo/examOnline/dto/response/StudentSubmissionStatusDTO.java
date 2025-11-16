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
public class StudentSubmissionStatusDTO {
    private Integer studentId;
    private String studentName;
    private String studentEmail;
    private Boolean hasSubmitted;
    private Integer submissionId; // null if not submitted
    private String submissionType; // "LINK" or "FILE" or null
    private String linkUrl; // For LINK type
    private String fileUrl; // For FILE type
    private String fileName; // For FILE type
    private LocalDateTime submittedAt; // null if not submitted
    private LocalDateTime updatedAt; // null if not submitted
    private Double earnedPoints; // Points earned by student (null if not graded yet)
}

