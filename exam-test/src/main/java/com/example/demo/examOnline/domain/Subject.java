package com.example.demo.examOnline.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "SUBJECTS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subject_id")
    private Integer subjectId;

    @Column(name = "subject_name", nullable = false, unique = true, length = 255)
    private String subjectName;
}
