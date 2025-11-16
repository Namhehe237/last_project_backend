package com.example.demo.examOnline.controller;

import com.example.demo.examOnline.dto.request.CreatePostRequest;
import com.example.demo.examOnline.dto.response.PostResponseDTO;
import com.example.demo.examOnline.service.PostService;
import com.example.demo.examOnline.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/class")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class PostController {
    private final PostService postService;
    private final UserService userService;

    @PostMapping(value = "/{classId}/posts", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<PostResponseDTO> createPost(
            @PathVariable Integer classId,
            @RequestPart("request") @Valid CreatePostRequest request,
            @RequestPart(value = "attachment", required = false) MultipartFile attachmentFile) {
        try {
            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            Integer teacherId = userService.getUserIdByEmail(currentUserEmail);
            if (teacherId == null) {
                log.error("User not found: {}", currentUserEmail);
                return ResponseEntity.status(401).build();
            }

            log.info("Received create post request - classId: {}, teacherId: {}, hasAttachment: {}", 
                    classId, teacherId, attachmentFile != null && !attachmentFile.isEmpty());

            request.setClassId(classId);
            request.setTeacherId(teacherId);

            PostResponseDTO post = postService.createPost(request, attachmentFile);
            log.info("Post created successfully - postId: {}", post.getPostId());
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            log.error("Error creating post: ", e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/{classId}/feed")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<List<PostResponseDTO>> getClassFeed(@PathVariable Integer classId) {
        try {
            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            Integer currentUserId = userService.getUserIdByEmail(currentUserEmail);
            
            log.info("Received get feed request - classId: {}, userId: {}", classId, currentUserId);

            List<PostResponseDTO> feed = postService.getClassFeed(classId, currentUserId);
            log.info("Returning feed - count: {}", feed.size());
            return ResponseEntity.ok(feed);
        } catch (Exception e) {
            log.error("Error getting feed: ", e);
            return ResponseEntity.status(500).build();
        }
    }

    @PutMapping("/posts/{postId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<PostResponseDTO> updatePost(
            @PathVariable Integer postId,
            @Valid @RequestBody CreatePostRequest request) {
        try {
            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            Integer userId = userService.getUserIdByEmail(currentUserEmail);
            if (userId == null) {
                log.error("User not found: {}", currentUserEmail);
                return ResponseEntity.status(401).build();
            }

            log.info("Received update post request - postId: {}, userId: {}", postId, userId);

            PostResponseDTO post = postService.updatePost(postId, userId, request);
            log.info("Post updated successfully - postId: {}", postId);
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            log.error("Error updating post: ", e);
            return ResponseEntity.status(500).build();
        }
    }

    @DeleteMapping("/posts/{postId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<Void> deletePost(@PathVariable Integer postId) {
        try {
            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            Integer userId = userService.getUserIdByEmail(currentUserEmail);
            if (userId == null) {
                log.error("User not found: {}", currentUserEmail);
                return ResponseEntity.status(401).build();
            }

            log.info("Received delete post request - postId: {}, userId: {}", postId, userId);

            postService.deletePost(postId, userId);
            log.info("Post deleted successfully - postId: {}", postId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error deleting post: ", e);
            return ResponseEntity.status(500).build();
        }
    }
}

