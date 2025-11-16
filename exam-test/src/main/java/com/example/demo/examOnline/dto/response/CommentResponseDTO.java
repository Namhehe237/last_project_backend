package com.example.demo.examOnline.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponseDTO {
    private Integer commentId;
    private Integer postId;
    private Integer userId;
    private String userName;
    private String userEmail;
    private String userAvatarUrl;
    private Integer parentCommentId; // Null for top-level comment
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CommentResponseDTO> replies; // Nested replies for threading
}

