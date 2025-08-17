package com.example.demo.examOnline.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "CLASS_REQUESTS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer requestId;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private Classes classEntity;

    private LocalDateTime requestedAt;
}
