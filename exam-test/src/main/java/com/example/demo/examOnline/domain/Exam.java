package com.example.demo.examOnline.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

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
    @Column(name = "exam_id")
    private Integer examId;

    @Column(name = "exam_name", nullable = false, length = 255)
    private String examName;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private Class class_id;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private User teacher;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(name = "total_score", precision = 5, scale = 2)
    private BigDecimal totalScore = BigDecimal.valueOf(100.00);

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "shuffle_questions")
    private Boolean shuffleQuestions = true;

    @Column(name = "shuffle_answers")
    private Boolean shuffleAnswers = true;

    @Column(name = "max_attempts")
    private Integer maxAttempts = 1;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ExamStatus status = ExamStatus.DRAFT;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // relation to ExamQuestion
    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("questionOrder ASC")
    private Set<ExamQuestion> examQuestions = new LinkedHashSet<>();
}
