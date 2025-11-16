package com.example.demo.examOnline.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForceSubmitExamRequest {
    private Integer examId;
    private Integer studentId;
    private String violationType; // "eye_gaze", "voice", "face_presence"
}

