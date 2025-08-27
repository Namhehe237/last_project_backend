package com.example.demo.examOnline.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateClassRequest {
    private String className;
    private String description;
    private String teacherName;
}
