package com.example.demo.examOnline.dto.cache;

import java.io.Serializable;
import java.util.List;

import com.example.demo.examOnline.domain.enums.DifficultyLevel;
import com.example.demo.examOnline.domain.enums.QuestionType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionSnapshot implements Serializable {
    private Integer questionId;
    private String questionText;
    private QuestionType questionType;
    private DifficultyLevel difficultyLevel;
    private String subjectName;
    private String teacherName;
    private List<AnswerSnapshot> answers;
}


