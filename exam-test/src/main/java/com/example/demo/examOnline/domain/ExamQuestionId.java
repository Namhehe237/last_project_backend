package com.example.demo.examOnline.domain;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExamQuestionId implements Serializable {
    private Integer examId;
    private Integer questionId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExamQuestionId)) return false;
        ExamQuestionId that = (ExamQuestionId) o;
        return Objects.equals(examId, that.examId) && Objects.equals(questionId, that.questionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(examId, questionId);
    }
}
