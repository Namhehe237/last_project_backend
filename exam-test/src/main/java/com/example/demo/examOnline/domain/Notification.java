package com.example.demo.examOnline.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import com.example.demo.examOnline.domain.enums.NotificationType;

@Entity
@Table(name = "NOTIFICATIONS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer notificationId;

    private String title;
    private String message;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    private Boolean isRead;
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // người nhận

    @ManyToOne
    @JoinColumn(name = "class_id")
    private Classes classEntity;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender; // người gửi
}
