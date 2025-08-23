package com.example.demo.examOnline.dto.request.student;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStudentProfileRequest {
    
    @NotBlank(message = "Họ tên không được để trống")
    @Size(min = 2, max = 255, message = "Họ tên phải từ 2 đến 255 ký tự")
    private String fullName;
    
    @Size(max = 20, message = "Số điện thoại không được quá 20 ký tự")
    private String phoneNumber;
    
    private String avatarUrl;
} 