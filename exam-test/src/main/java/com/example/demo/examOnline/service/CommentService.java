package com.example.demo.examOnline.service;

import com.example.demo.examOnline.dto.request.CreateCommentRequest;
import com.example.demo.examOnline.dto.response.CommentResponseDTO;

import java.util.List;

public interface CommentService {
    public CommentResponseDTO createComment(CreateCommentRequest request);

    public List<CommentResponseDTO> getPostComments(Integer postId);

    public CommentResponseDTO updateComment(Integer commentId, Integer userId, String content);

    public void deleteComment(Integer commentId, Integer userId);

}
