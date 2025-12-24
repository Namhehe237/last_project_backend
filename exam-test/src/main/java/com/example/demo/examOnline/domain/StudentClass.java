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
@Table(name = "STUDENT_CLASSES")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentClass {
    @EmbeddedId
    private StudentClassId id;

    @ManyToOne
    @MapsId("studentId")
    @JoinColumn(name = "student_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User student;

    @ManyToOne
    @MapsId("classId")
    @JoinColumn(name = "class_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Classes classEntity;

    private LocalDateTime joinedAt;
}
