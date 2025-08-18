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
    private String description;
    private String teacherName;
    private String teacherPhoneNumber;

    public ClassResponseDTO(Integer classId, String className, String classCode) {
        this.classId = classId;
        this.className = className;
        this.classCode = classCode;
    }

    
}
