package com.example.demo.examOnline.service.impl;

import com.example.demo.examOnline.domain.Classes;
import com.example.demo.examOnline.domain.Post;
import com.example.demo.examOnline.domain.User;
import com.example.demo.examOnline.domain.enums.PostType;
import com.example.demo.examOnline.dto.request.CreatePostRequest;
import com.example.demo.examOnline.dto.response.PostResponseDTO;
import com.example.demo.examOnline.repository.ClassRepository;
import com.example.demo.examOnline.repository.PostRepository;
import com.example.demo.examOnline.repository.UserRepository;
import com.example.demo.examOnline.service.CloudinaryService;
import com.example.demo.examOnline.service.NotificationService;
import com.example.demo.examOnline.service.PostService;
import com.example.demo.examOnline.repository.AssignmentSubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final ClassRepository classRepository;
    private final UserRepository userRepository;
    private final AssignmentSubmissionRepository assignmentSubmissionRepository;
    private final CloudinaryService cloudinaryService;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public PostResponseDTO createPost(CreatePostRequest request, MultipartFile attachmentFile) {
        Classes classEntity = classRepository.findById(request.getClassId())
                .orElseThrow(() -> new RuntimeException("Class not found: " + request.getClassId()));

        User teacher = userRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new RuntimeException("Teacher not found: " + request.getTeacherId()));

        PostType postType = PostType.valueOf(request.getPostType());

        // Validate assignment fields
        if (postType == PostType.ASSIGNMENT && request.getDueDate() == null) {
            throw new RuntimeException("Due date is required for assignment");
        }

        // Upload attachment file if provided
        String attachmentUrl = null;
        if (attachmentFile != null && !attachmentFile.isEmpty()) {
            try {
                log.info("Uploading attachment file - fileName: {}, size: {}, contentType: {}",
                        attachmentFile.getOriginalFilename(),
                        attachmentFile.getSize(),
                        attachmentFile.getContentType());
                String folder = String.format("post-attachments/post-%d", request.getClassId());
                attachmentUrl = cloudinaryService.uploadAssignmentFile(attachmentFile, folder);
                log.info("Attachment file uploaded successfully - URL: {}", attachmentUrl);
            } catch (IOException e) {
                log.error("Failed to upload attachment file: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to upload attachment file: " + e.getMessage(), e);
            } catch (Exception e) {
                log.error("Unexpected error uploading attachment file: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to upload attachment file: " + e.getMessage(), e);
            }
        }

        Post post = Post.builder()
                .classEntity(classEntity)
                .teacher(teacher)
                .title(request.getTitle())
                .content(request.getContent())
                .postType(postType)
                .dueDate(request.getDueDate())
                .totalPoints(request.getTotalPoints())
                .attachmentUrl(attachmentUrl)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Post saved = postRepository.save(post);

        // Notify all students in the class about the new post
        try {
            if (postType == PostType.ASSIGNMENT) {
                String title = "Bài tập mới";
                String message = String.format("Giáo viên %s đã giao bài tập '%s' cho lớp %s. Hạn nộp: %s",
                        teacher.getFullName(),
                        saved.getTitle(),
                        classEntity.getClassName(),
                        saved.getDueDate() != null ? saved.getDueDate().toString() : "Chưa xác định");
                notificationService.notifyStudentsInClass(
                        classEntity.getClassId(),
                        title,
                        message,
                        com.example.demo.examOnline.domain.enums.NotificationType.ASSIGNMENT,
                        teacher.getUserId());
            } else if (postType == PostType.ANNOUNCEMENT) {
                String title = "Thông báo mới";
                String message = String.format("Giáo viên %s đã đăng thông báo '%s' trong lớp %s",
                        teacher.getFullName(),
                        saved.getTitle(),
                        classEntity.getClassName());
                notificationService.notifyStudentsInClass(
                        classEntity.getClassId(),
                        title,
                        message,
                        com.example.demo.examOnline.domain.enums.NotificationType.POST,
                        teacher.getUserId());
            }
        } catch (Exception e) {
            // Log error but don't fail post creation
            log.error("Error sending post notifications: {}", e.getMessage(), e);
        }

        return mapToDTO(saved, null);
    }

    @Override
    public List<PostResponseDTO> getClassFeed(Integer classId, Integer currentUserId) {
        List<Post> posts = postRepository.findByClassIdOrderByCreatedAtDesc(classId);

        return posts.stream().map(post -> {
            // Check if current user has submitted this assignment
            Boolean isSubmitted = null;
            if (post.getPostType() == PostType.ASSIGNMENT && currentUserId != null) {
                isSubmitted = assignmentSubmissionRepository
                        .findByAssignmentPostIdAndStudentUserId(post.getPostId(), currentUserId)
                        .isPresent();
            }
            return mapToDTO(post, isSubmitted);
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PostResponseDTO updatePost(Integer postId, Integer userId, CreatePostRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found: " + postId));

        // Only author can update
        if (!post.getTeacher().getUserId().equals(userId)) {
            throw new RuntimeException("Only the author can update this post");
        }

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());

        if (post.getPostType() == PostType.ASSIGNMENT) {
            post.setDueDate(request.getDueDate());
            post.setTotalPoints(request.getTotalPoints());
        }

        post.setUpdatedAt(LocalDateTime.now());

        Post updated = postRepository.save(post);
        return mapToDTO(updated, null);
    }

    @Override
    @Transactional
    public void deletePost(Integer postId, Integer userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found: " + postId));

        // Only author can delete
        if (!post.getTeacher().getUserId().equals(userId)) {
            throw new RuntimeException("Only the author can delete this post");
        }

        postRepository.delete(post);
    }

    private PostResponseDTO mapToDTO(Post post, Boolean isSubmitted) {
        int commentCount = post.getComments() != null ? post.getComments().size() : 0;

        return PostResponseDTO.builder()
                .postId(post.getPostId())
                .classId(post.getClassEntity().getClassId())
                .teacherId(post.getTeacher().getUserId())
                .teacherName(post.getTeacher().getFullName())
                .teacherEmail(post.getTeacher().getEmail())
                .teacherAvatarUrl(post.getTeacher().getAvatarUrl())
                .title(post.getTitle())
                .content(post.getContent())
                .postType(post.getPostType().name())
                .dueDate(post.getDueDate())
                .totalPoints(post.getTotalPoints())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .commentCount(commentCount)
                .isSubmitted(isSubmitted)
                .attachmentUrl(post.getAttachmentUrl())
                .build();
    }
}
