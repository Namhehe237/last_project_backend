package com.example.demo.examOnline.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ViolationEventRequest {
    private Integer examId;
    private Integer studentId;
    private String type;
    private Long timestamp;
}


