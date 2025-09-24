package com.example.demo.examOnline.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestJoinClassRequest {
    
    @NotBlank(message = "Mã lớp không được để trống")
    private String classCode;
}