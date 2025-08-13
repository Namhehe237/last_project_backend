package com.example.demo.examOnline.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClassResponseDTO {
    private Integer classId;
    private String className;
    private String classCode;
}
