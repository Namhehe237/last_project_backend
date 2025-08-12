package com.example.demo.authentication.dto.request;

import java.time.LocalDateTime;

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
    private String userCode;
    private String role;
}