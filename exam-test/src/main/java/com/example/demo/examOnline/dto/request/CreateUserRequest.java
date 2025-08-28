package com.example.demo.examOnline.dto.request;

import com.example.demo.examOnline.domain.enums.RoleName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequest {
    private String email;
    private String password;
    private String fullName;
    private String phoneNumber;
    private RoleName roleName;
}
