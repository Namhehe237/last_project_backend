package com.example.demo.examOnline.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestJoinClassRequest {

    @NotNull(message = "Student ID không được để trống")
    private Integer studentId;

    @NotBlank(message = "Class code không được để trống")
    private String classCode;
}