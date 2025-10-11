package com.example.demo.examOnline.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExamFilterRequest {
    private Integer studentId;
    private Integer classId;
}
