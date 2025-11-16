package com.example.demo.examOnline.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmitAssignmentRequest {
    private Integer assignmentId;
    private Integer studentId;
    private String submissionType; // "LINK" or "FILE"
    private String linkUrl; // For LINK type
    // File will be uploaded via MultipartFile in controller
}

