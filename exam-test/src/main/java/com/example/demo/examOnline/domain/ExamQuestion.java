package com.example.demo.examOnline.domain;



import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

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
    private Exam exam;

    @ManyToOne
    @MapsId("questionId")
    @JoinColumn(name = "question_id")
    private QuestionBank question;

    @Column(name = "question_order", nullable = false)
    private Integer questionOrder;

    @Column(name = "score_per_question", precision = 5, scale = 2)
    private BigDecimal scorePerQuestion = BigDecimal.valueOf(1.00);
}
