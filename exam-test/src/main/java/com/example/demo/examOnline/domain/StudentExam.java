package com.example.demo.examOnline.domain;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.demo.examOnline.domain.enums.StudentExamStatus;

@Entity
@Table(name = "STUDENT_EXAMS",
       uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "exam_id", "attempt_number"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentExam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_exam_id")
    private Integer studentExamId;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "submit_time")
    private LocalDateTime submitTime;

    @Column(name = "score", precision = 5, scale = 2)
    private BigDecimal score;

    @Column(name = "attempt_number", nullable = false)
    private Integer attemptNumber = 1;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StudentExamStatus status = StudentExamStatus.IN_PROGRESS;
}

