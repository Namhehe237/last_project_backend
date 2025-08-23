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
public class ClassResponseDTO {
    private Integer classId;
    private String className;
    private String classCode;
    private String description;
    private String teacherName;
    private String teacherEmail;
    private LocalDateTime createdAt;
    private LocalDateTime joinedAt; // Thời gian học sinh tham gia lớp
    private Integer studentCount; // Số lượng học sinh trong lớp
}
