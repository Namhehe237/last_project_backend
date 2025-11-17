package com.example.demo.examOnline.dto.request;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateExamQuestionsRequest {
    private List<Integer> questionIds;
}

