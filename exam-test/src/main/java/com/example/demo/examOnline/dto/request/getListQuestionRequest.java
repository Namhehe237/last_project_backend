package com.example.demo.examOnline.dto.request;

import com.example.demo.examOnline.domain.enums.DifficultyLevel;
import com.example.demo.examOnline.domain.enums.QuestionType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class getListQuestionRequest {
    private DifficultyLevel difficultyLevel;
    private String subjectName;
    private String teacherName;
}
