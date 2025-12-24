package com.example.demo.examOnline.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.example.demo.examOnline.domain.enums.ViolationType;
import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "violation_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ViolationLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "violation_id")
    private Integer violationId;

    @ManyToOne
    @JoinColumn(name = "exam_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Exam exam;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User student;

    @Enumerated(EnumType.STRING)
    @Column(name = "violation_type", nullable = false)
    private ViolationType violationType;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "session_id")
    private String sessionId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
}
