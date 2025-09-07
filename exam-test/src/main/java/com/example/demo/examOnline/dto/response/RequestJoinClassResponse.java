package com.example.demo.examOnline.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RequestJoinClassResponse {
    private Integer requestId;
    private String studentName;
}
