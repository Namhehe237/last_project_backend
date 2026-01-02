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
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User student;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "class_id")
    private Classes classEntity;

    @Column(name = "requested_at")
    private LocalDateTime requestedAt;
}
