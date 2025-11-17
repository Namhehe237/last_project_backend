package com.example.demo.examOnline.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateRandomExamRequest extends CreateExamRequest {
    private Integer easyQuestionCount;
    private Integer mediumQuestionCount;
    private Integer hardQuestionCount;
    private Integer totalQuestions;
}

