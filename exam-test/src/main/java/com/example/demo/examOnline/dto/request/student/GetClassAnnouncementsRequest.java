package com.example.demo.examOnline.dto.request.student;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetClassAnnouncementsRequest {
    @NotNull(message = "Student ID không được để trống")
    private Integer studentId;
    
    @NotNull(message = "Class ID không được để trống")
    private Integer classId;
}