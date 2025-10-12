package com.example.demo.examOnline.dto.cache;

import java.io.Serializable;

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
public class AnswerSnapshot implements Serializable {
    private Integer answerId;
    private String answerText;
    private boolean correct;
}


