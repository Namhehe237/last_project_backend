package com.example.demo.examOnline.dto.response;

import com.example.demo.examOnline.domain.enums.RoleName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {
    private Integer userId;
    private String email;
    private String fullName;
    private String phoneNumber;
    private RoleName role;

    public UserResponseDTO(Integer userId, String email, String fullName, String phoneNumber) {
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
    }

}
