package com.example.demo.examOnline.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateClassRequest {
    private Integer teacherId;
    private String className;
    private String description;
}
