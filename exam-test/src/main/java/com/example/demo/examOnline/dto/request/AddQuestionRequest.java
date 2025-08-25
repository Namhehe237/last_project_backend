package com.example.demo.examOnline.dto.request;

import java.util.List;

import com.example.demo.examOnline.domain.enums.DifficultyLevel;
import com.example.demo.examOnline.domain.enums.QuestionType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddQuestionRequest {
    private String questionText;
    private QuestionType questionType;
    private DifficultyLevel difficultyLevel;
    private String subjectName;
    private Integer teacherId;
    private List<String> answer;
}
