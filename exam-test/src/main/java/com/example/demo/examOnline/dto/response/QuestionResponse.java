package com.example.demo.examOnline.dto.response;

import java.util.List;

import com.example.demo.examOnline.domain.enums.DifficultyLevel;
import com.example.demo.examOnline.domain.enums.QuestionType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class QuestionResponse {
    private String questionText;
    private QuestionType questionType;
    private DifficultyLevel difficultyLevel;
    private String subjectName;
    private String teacherName;
    private List<AnswerResponse> answers;
}
