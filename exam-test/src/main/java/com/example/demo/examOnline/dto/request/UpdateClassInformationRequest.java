package com.example.demo.examOnline.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateClassInformationRequest {
    private String className;
    private String classCode;
    private String description;
}
