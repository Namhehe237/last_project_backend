package com.example.demo.examOnline.service.impl;

import com.example.demo.examOnline.domain.Comment;
import com.example.demo.examOnline.domain.Post;
import com.example.demo.examOnline.domain.User;
import com.example.demo.examOnline.dto.request.CreateCommentRequest;
import com.example.demo.examOnline.dto.response.CommentResponseDTO;
import com.example.demo.examOnline.repository.CommentRepository;
import com.example.demo.examOnline.repository.PostRepository;
import com.example.demo.examOnline.repository.UserRepository;
import com.example.demo.examOnline.service.CommentService;
import com.example.demo.examOnline.service.NotificationService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public CommentResponseDTO createComment(CreateCommentRequest request) {
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found: " + request.getPostId()));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found: " + request.getUserId()));

        Comment parentComment = null;
        if (request.getParentCommentId() != null) {
            parentComment = commentRepository.findById(request.getParentCommentId())
                    .orElseThrow(
                            () -> new RuntimeException("Parent comment not found: " + request.getParentCommentId()));
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

        // If this is a reply, notify the parent comment author
        if (parentComment != null) {
            try {
                Integer parentAuthorId = parentComment.getUser().getUserId();
                // Don't notify if replying to own comment
                if (!parentAuthorId.equals(user.getUserId())) {
                    String title = "Có người trả lời bình luận của bạn";
                    String message = String.format("%s đã trả lời bình luận của bạn: %s",
                            user.getFullName(),
                            saved.getContent().length() > 50 ? saved.getContent().substring(0, 50) + "..."
                                    : saved.getContent());
                    Integer classId = post.getClassEntity() != null ? post.getClassEntity().getClassId() : null;
                    notificationService.notifyUser(
                            parentAuthorId,
                            title,
                            message,
                            com.example.demo.examOnline.domain.enums.NotificationType.COMMENT_REPLY,
                            user.getUserId(),
                            classId);
                }
            } catch (Exception e) {
                // Log error but don't fail comment creation
                System.err.println("Error sending comment reply notification: " + e.getMessage());
            }
        }

        return mapToDTO(saved);
    }

    @Override
    public List<CommentResponseDTO> getPostComments(Integer postId) {
        // Get all top-level comments (no parent)
        List<Comment> topLevelComments = commentRepository.findTopLevelCommentsByPostId(postId);

        // Build comment tree
        return topLevelComments.stream()
                .map(this::mapToDTOWithReplies)
                .collect(Collectors.toList());
    }

    @Override
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

    @Override
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
