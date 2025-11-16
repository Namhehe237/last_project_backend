package com.example.demo.examOnline.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCommentRequest {
    private Integer postId;
    private Integer userId;
    private Integer parentCommentId; // Null for top-level comment, set for reply
    private String content;
}

