package com.example.demo.examOnline.dto.response;

import com.example.demo.examOnline.domain.enums.RoleName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String accessToken;
    private RoleName roleName;
}
