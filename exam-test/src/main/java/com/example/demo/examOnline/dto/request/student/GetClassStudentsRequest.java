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
public class GetClassStudentsRequest {
    @NotNull(message = "Class ID không được để trống")
    private Integer classId;
    
    private Integer page = 0;
    private Integer size = 10;
}