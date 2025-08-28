package com.example.demo.examOnline.dto.request;


import com.example.demo.examOnline.domain.enums.RoleName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {
    private String email;
    private String password;
    private String fullName;
    private String phoneNumber;
    private String avatarUrl;
    private RoleName role;
}