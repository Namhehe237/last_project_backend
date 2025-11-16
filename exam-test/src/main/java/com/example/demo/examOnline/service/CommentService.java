package com.example.demo.examOnline.service;

import com.example.demo.examOnline.domain.Comment;
import com.example.demo.examOnline.domain.Post;
import com.example.demo.examOnline.domain.User;
import com.example.demo.examOnline.dto.request.CreateCommentRequest;
import com.example.demo.examOnline.dto.response.CommentResponseDTO;
import com.example.demo.examOnline.repository.CommentRepository;
import com.example.demo.examOnline.repository.PostRepository;
import com.example.demo.examOnline.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentResponseDTO createComment(CreateCommentRequest request) {
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found: " + request.getPostId()));
        
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found: " + request.getUserId()));

        Comment parentComment = null;
        if (request.getParentCommentId() != null) {
            parentComment = commentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new RuntimeException("Parent comment not found: " + request.getParentCommentId()));
        }

        Comment comment = Comment.builder()
                .post(post)
                .user(user)
                .parentComment(parentComment)
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Comment saved = commentRepository.save(comment);
        return mapToDTO(saved);
    }

    public List<CommentResponseDTO> getPostComments(Integer postId) {
        // Get all top-level comments (no parent)
        List<Comment> topLevelComments = commentRepository.findTopLevelCommentsByPostId(postId);
        
        // Build comment tree
        return topLevelComments.stream()
                .map(this::mapToDTOWithReplies)
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentResponseDTO updateComment(Integer commentId, Integer userId, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found: " + commentId));

        // Only author can update
        if (!comment.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("Only the author can update this comment");
        }

        comment.setContent(content);
        comment.setUpdatedAt(LocalDateTime.now());
        
        Comment updated = commentRepository.save(comment);
        return mapToDTOWithReplies(updated);
    }

    @Transactional
    public void deleteComment(Integer commentId, Integer userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found: " + commentId));

        // Only author can delete
        if (!comment.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("Only the author can delete this comment");
        }

        commentRepository.delete(comment);
    }

    private CommentResponseDTO mapToDTOWithReplies(Comment comment) {
        CommentResponseDTO dto = mapToDTO(comment);
        
        // Get replies for this comment
        List<Comment> replies = comment.getReplies() != null 
                ? comment.getReplies() 
                : commentRepository.findByParentCommentCommentId(comment.getCommentId());
        
        if (replies != null && !replies.isEmpty()) {
            List<CommentResponseDTO> replyDTOs = replies.stream()
                    .map(this::mapToDTOWithReplies) // Recursive for nested replies
                    .collect(Collectors.toList());
            dto.setReplies(replyDTOs);
        } else {
            dto.setReplies(new ArrayList<>());
        }
        
        return dto;
    }

    private CommentResponseDTO mapToDTO(Comment comment) {
        return CommentResponseDTO.builder()
                .commentId(comment.getCommentId())
                .postId(comment.getPost().getPostId())
                .userId(comment.getUser().getUserId())
                .userName(comment.getUser().getFullName())
                .userEmail(comment.getUser().getEmail())
                .userAvatarUrl(comment.getUser().getAvatarUrl())
                .parentCommentId(comment.getParentComment() != null ? comment.getParentComment().getCommentId() : null)
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}

