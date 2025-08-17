package com.example.demo.examOnline.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

import com.example.demo.examOnline.domain.enums.ExamStatus;

@Entity
@Table(name = "EXAMS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer examId;

    private String examName;
    private String subjectName;
    private Integer durationMinutes;
    private Double totalScore;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean shuffleQuestions;
    private Boolean shuffleAnswers;
    private Integer maxAttempts;

    @Enumerated(EnumType.STRING)
    private ExamStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private Classes classEntity;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private User teacher;

    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL)
    private List<ExamQuestion> examQuestions;
}
