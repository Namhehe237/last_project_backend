package com.example.demo.examOnline.dto.request;

import com.example.demo.examOnline.domain.enums.RoleName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetUserListRequest {
    private RoleName roleName;
}
