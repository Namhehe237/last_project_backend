package com.example.demo.examOnline.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private Integer studentAnswerId;

    @ManyToOne
    @JoinColumn(name = "student_exam_id")
    private StudentExam studentExam;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private QuestionsBank question;

    @ManyToOne
    @JoinColumn(name = "chosen_answer_id")
    private Answer chosenAnswer;

    private String essayAnswerText;
    private Boolean isCorrect;
    private Double scoreEarned;
}
