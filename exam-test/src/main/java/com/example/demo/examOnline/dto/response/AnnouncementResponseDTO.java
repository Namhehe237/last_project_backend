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
public class AnnouncementResponseDTO {
    private Integer announcementId;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private String teacherName;
    private String teacherEmail;
    private Boolean isImportant;
} 