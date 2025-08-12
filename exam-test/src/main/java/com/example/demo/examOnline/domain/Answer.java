package com.example.demo.examOnline.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ANSWERS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    private Integer answerId;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private QuestionBank question;

    @Column(name = "answer_text", nullable = false, columnDefinition = "TEXT")
    private String answerText;

    @Column(name = "is_correct")
    private Boolean isCorrect = false;
}
