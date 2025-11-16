package com.example.demo.examOnline.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentSubmissionResponseDTO {
    private Integer submissionId;
    private Integer assignmentId;
    private Integer studentId;
    private String studentName;
    private String studentEmail;
    private String submissionType; // "LINK" or "FILE"
    private String linkUrl; // For LINK type
    private String fileUrl; // For FILE type - Cloudinary URL
    private String fileName; // For FILE type
    private LocalDateTime submittedAt;
    private LocalDateTime updatedAt;
}

