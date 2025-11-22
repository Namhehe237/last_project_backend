package com.example.demo.examOnline.dto.request;

import com.example.demo.examOnline.domain.enums.ViolationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogViolationRequest {
    private Integer examId;
    private Integer studentId;
    private ViolationType violationType;
    private String message;
    private String sessionId;
}

