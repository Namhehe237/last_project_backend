package com.example.demo.examOnline.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswerResponse {
    private String answerText;
    private boolean isCorrect;
}