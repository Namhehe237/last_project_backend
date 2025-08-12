package com.example.demo.examOnline.domain;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "STUDENT_ANSWERS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_answer_id")
    private Integer studentAnswerId;

    @ManyToOne
    @JoinColumn(name = "student_exam_id", nullable = false)
    private StudentExam studentExam;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private QuestionBank question;

    @ManyToOne
    @JoinColumn(name = "chosen_answer_id")
    private Answer chosenAnswer;

    @Column(name = "essay_answer_text", columnDefinition = "TEXT")
    private String essayAnswerText;

    @Column(name = "is_correct")
    private Boolean isCorrect;

    @Column(name = "score_earned", precision = 5, scale = 2)
    private BigDecimal scoreEarned;
}
