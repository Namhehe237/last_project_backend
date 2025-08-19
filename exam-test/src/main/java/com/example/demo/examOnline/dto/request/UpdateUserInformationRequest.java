package com.example.demo.examOnline.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserInformationRequest {
    private String email;
    private String password;
    private String fullName;
    private String phoneNumber;
    private String avatarUrl;
}
