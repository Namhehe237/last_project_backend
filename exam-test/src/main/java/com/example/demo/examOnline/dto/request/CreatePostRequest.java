package com.example.demo.examOnline.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CreatePostRequest {
    private Integer classId;
    private Integer teacherId;
    private String title;
    private String content;
    private String postType; // "ANNOUNCEMENT" or "ASSIGNMENT"
    private LocalDateTime dueDate; // Required for ASSIGNMENT
    private Double totalPoints; // Optional for ASSIGNMENT
}

