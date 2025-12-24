package com.example.demo.examOnline.domain;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "EXAM_QUESTIONS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamQuestion {
    @EmbeddedId
    private ExamQuestionId id;

    @ManyToOne
    @MapsId("examId")
    @JoinColumn(name = "exam_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Exam exam;

    @ManyToOne
    @MapsId("questionId")
    @JoinColumn(name = "question_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private QuestionsBank question;

    public ExamQuestion(Exam exam, QuestionsBank question) {
        this.exam = exam;
        this.question = question;
        this.id = ExamQuestionId.builder()
                .examId(exam.getExamId())
                .questionId(question.getQuestionId())
                .build();
    }
}
