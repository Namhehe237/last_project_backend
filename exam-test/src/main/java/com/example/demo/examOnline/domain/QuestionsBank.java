package com.example.demo.examOnline.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.example.demo.examOnline.domain.enums.DifficultyLevel;
import com.example.demo.examOnline.domain.enums.QuestionType;

@Entity
@Table(name = "QUESTIONS_BANK")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionsBank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer questionId;

    private String questionText;

    @Enumerated(EnumType.STRING)
    private QuestionType questionType;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private DifficultyLevel difficultyLevel = DifficultyLevel.MEDIUM;

    private String imageUrl;
    private String audioUrl;
    private String videoUrl;
    private String subjectName;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User teacher;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Answer> answers = new ArrayList<>();
}
