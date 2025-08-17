package com.example.demo.examOnline.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

import com.example.demo.examOnline.domain.enums.StudentExamStatus;

@Entity
@Table(name = "STUDENT_EXAMS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentExam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer studentExamId;

    private LocalDateTime startTime;
    private LocalDateTime submitTime;
    private Double score;
    private Integer attemptNumber;

    @Enumerated(EnumType.STRING)
    private StudentExamStatus status;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

    @ManyToOne
    @JoinColumn(name = "exam_id")
    private Exam exam;

    @OneToMany(mappedBy = "studentExam", cascade = CascadeType.ALL)
    private List<StudentAnswer> studentAnswers;
}
