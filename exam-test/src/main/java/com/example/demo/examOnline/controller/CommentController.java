package com.example.demo.examOnline.controller;

import com.example.demo.examOnline.dto.request.CreateCommentRequest;
import com.example.demo.examOnline.dto.request.UpdateCommentRequest;
import com.example.demo.examOnline.dto.response.CommentResponseDTO;
import com.example.demo.examOnline.service.CommentService;
import com.example.demo.examOnline.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/class")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class CommentController {
    private final CommentService commentService;
    private final UserService userService;

    @PostMapping("/posts/{postId}/comments")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<CommentResponseDTO> createComment(
            @PathVariable Integer postId,
            @Valid @RequestBody CreateCommentRequest request) {
        try {
            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            Integer userId = userService.getUserIdByEmail(currentUserEmail);
            if (userId == null) {
                log.error("User not found: {}", currentUserEmail);
                return ResponseEntity.status(401).build();
            }

            log.info("Received create comment request - postId: {}, userId: {}", postId, userId);

            request.setPostId(postId);
            request.setUserId(userId);

            CommentResponseDTO comment = commentService.createComment(request);
            log.info("Comment created successfully - commentId: {}", comment.getCommentId());
            return ResponseEntity.ok(comment);
        } catch (Exception e) {
            log.error("Error creating comment: ", e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/posts/{postId}/comments")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<List<CommentResponseDTO>> getPostComments(@PathVariable Integer postId) {
        try {
            log.info("Received get comments request - postId: {}", postId);

            List<CommentResponseDTO> comments = commentService.getPostComments(postId);
            log.info("Returning comments - count: {}", comments.size());
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            log.error("Error getting comments: ", e);
            return ResponseEntity.status(500).build();
        }
    }

    @PutMapping("/comments/{commentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<CommentResponseDTO> updateComment(
            @PathVariable Integer commentId,
            @Valid @RequestBody UpdateCommentRequest request) {
        try {
            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            Integer userId = userService.getUserIdByEmail(currentUserEmail);
            if (userId == null) {
                log.error("User not found: {}", currentUserEmail);
                return ResponseEntity.status(401).build();
            }

            log.info("Received update comment request - commentId: {}, userId: {}", commentId, userId);

            CommentResponseDTO comment = commentService.updateComment(commentId, userId, request.getContent());
            log.info("Comment updated successfully - commentId: {}", commentId);
            return ResponseEntity.ok(comment);
        } catch (Exception e) {
            log.error("Error updating comment: ", e);
            return ResponseEntity.status(500).build();
        }
    }

    @DeleteMapping("/comments/{commentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<Void> deleteComment(@PathVariable Integer commentId) {
        try {
            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            Integer userId = userService.getUserIdByEmail(currentUserEmail);
            if (userId == null) {
                log.error("User not found: {}", currentUserEmail);
                return ResponseEntity.status(401).build();
            }

            log.info("Received delete comment request - commentId: {}, userId: {}", commentId, userId);

            commentService.deleteComment(commentId, userId);
            log.info("Comment deleted successfully - commentId: {}", commentId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error deleting comment: ", e);
            return ResponseEntity.status(500).build();
        }
    }
}

