package com.example.demo.examOnline.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "CLASSES")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Classes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer classId;

    private String className;

    @Column(unique = true)
    private String classCode;

    private String description;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private User teacher; // tham chiếu đến USERS

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "classEntity", cascade = CascadeType.ALL)
    private List<StudentClass> studentClasses;

    @OneToMany(mappedBy = "classEntity", cascade = CascadeType.ALL)
    private List<ClassRequest> classRequests;
}
