package com.example.demo.examOnline.dto.response.student;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentProfileResponse {
    private Integer userId;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String avatarUrl;
    private String userCode;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 