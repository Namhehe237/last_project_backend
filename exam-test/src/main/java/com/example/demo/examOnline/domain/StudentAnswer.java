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
    @Column(name = "answer_id")
    private Integer studentAnswerId;

    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "student_id", referencedColumnName = "student_id"),
        @JoinColumn(name = "exam_id", referencedColumnName = "exam_id")
    })
    private StudentExam studentExam;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private QuestionsBank question;

    @ManyToOne
    @JoinColumn(name = "chosen_answer_id")
    private Answer chosenAnswer;

    @Column(name = "answer_text")
    private String essayAnswerText; // For essay questions, stored in answer_text column

    @Column(name = "is_correct")
    private Boolean isCorrect;

    @Column(name = "points_earned")
    private Double scoreEarned;
}
