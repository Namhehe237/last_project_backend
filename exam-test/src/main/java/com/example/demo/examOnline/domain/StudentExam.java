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
    @EmbeddedId
    private StudentExamId id;

    @Column(name = "start_time")
    private LocalDateTime startTime;
    
    @Column(name = "end_time")
    private LocalDateTime submitTime;
    
    private Double score;
    private Integer attemptNumber;
    
    @Column(name = "video_url")
    private String videoUrl; // URL of recorded video on Cloudinary

    @Enumerated(EnumType.STRING)
    private StudentExamStatus status;

    @ManyToOne
    @MapsId("studentId")
    @JoinColumn(name = "student_id")
    private User student;

    @ManyToOne
    @MapsId("examId")
    @JoinColumn(name = "exam_id")
    private Exam exam;

    @OneToMany(mappedBy = "studentExam", cascade = CascadeType.ALL)
    private List<StudentAnswer> studentAnswers;
}
