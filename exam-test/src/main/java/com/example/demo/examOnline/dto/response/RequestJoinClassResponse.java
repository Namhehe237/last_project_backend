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
public class RequestJoinClassResponse {
    private Integer requestId;
    private String studentName;
    private String studentEmail;
    private String studentCode;
    private String className;
    private String classCode;
    private LocalDateTime requestedAt;
    
    // Constructor for query result mapping
    public RequestJoinClassResponse(Integer requestId, String studentName) {
        this.requestId = requestId;
        this.studentName = studentName;
    }
}