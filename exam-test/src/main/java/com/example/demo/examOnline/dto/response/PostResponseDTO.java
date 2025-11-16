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
public class PostResponseDTO {
    private Integer postId;
    private Integer classId;
    private Integer teacherId;
    private String teacherName;
    private String teacherEmail;
    private String teacherAvatarUrl;
    private String title;
    private String content;
    private String postType; // "ANNOUNCEMENT" or "ASSIGNMENT"
    private LocalDateTime dueDate; // For ASSIGNMENT
    private Double totalPoints; // For ASSIGNMENT
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer commentCount;
    private Boolean isSubmitted; // For ASSIGNMENT - true if current student has submitted
    private String attachmentUrl; // Optional file attachment URL
}

