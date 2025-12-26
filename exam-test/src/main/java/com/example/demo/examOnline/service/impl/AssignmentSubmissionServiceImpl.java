package com.example.demo.examOnline.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.examOnline.domain.AssignmentSubmission;
import com.example.demo.examOnline.domain.Post;
import com.example.demo.examOnline.domain.StudentClass;
import com.example.demo.examOnline.domain.User;
import com.example.demo.examOnline.domain.enums.SubmissionType;
import com.example.demo.examOnline.dto.response.AssignmentSubmissionResponseDTO;
import com.example.demo.examOnline.dto.response.StudentSubmissionStatusDTO;
import com.example.demo.examOnline.repository.AssignmentSubmissionRepository;
import com.example.demo.examOnline.repository.PostRepository;
import com.example.demo.examOnline.repository.StudentClassRepository;
import com.example.demo.examOnline.repository.UserRepository;
import com.example.demo.examOnline.service.AssignmentSubmissionService;
import com.example.demo.examOnline.service.CloudinaryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AssignmentSubmissionServiceImpl implements AssignmentSubmissionService {
    private final AssignmentSubmissionRepository submissionRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final StudentClassRepository studentClassRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    @Transactional
    public AssignmentSubmissionResponseDTO submitAssignment(
            Integer assignmentId,
            Integer studentId,
            String submissionType,
            String linkUrl,
            MultipartFile file) {

        Post assignment = postRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found: " + assignmentId));

        // Validate it's an assignment
        if (assignment.getPostType() != com.example.demo.examOnline.domain.enums.PostType.ASSIGNMENT) {
            throw new RuntimeException("Post is not an assignment");
        }

        // Check if deadline has passed
        if (assignment.getDueDate() != null && LocalDateTime.now().isAfter(assignment.getDueDate())) {
            throw new RuntimeException("Assignment deadline has passed");
        }

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found: " + studentId));

        SubmissionType type = SubmissionType.valueOf(submissionType);

        // Check if student already submitted
        AssignmentSubmission existing = submissionRepository
                .findByAssignmentPostIdAndStudentUserId(assignmentId, studentId)
                .orElse(null);

        AssignmentSubmission submission;
        if (existing != null) {
            // Update existing submission
            existing.setSubmissionType(type);
            existing.setUpdatedAt(LocalDateTime.now());

            if (type == SubmissionType.LINK) {
                existing.setLinkUrl(linkUrl);
                existing.setFileUrl(null);
                existing.setFileName(null);
            } else {
                // Upload file to Cloudinary
                try {
                    String folder = String.format("assignment-submissions/assignment-%d/student-%d", assignmentId,
                            studentId);
                    String fileUrl = cloudinaryService.uploadAssignmentFile(file, folder);
                    existing.setFileUrl(fileUrl);
                    existing.setFileName(file.getOriginalFilename());
                    existing.setLinkUrl(null);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to upload file: " + e.getMessage());
                }
            }

            submission = submissionRepository.save(existing);
        } else {
            // Create new submission
            AssignmentSubmission.AssignmentSubmissionBuilder builder = AssignmentSubmission.builder()
                    .assignment(assignment)
                    .student(student)
                    .submissionType(type)
                    .submittedAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now());

            if (type == SubmissionType.LINK) {
                builder.linkUrl(linkUrl);
            } else {
                // Upload file to Cloudinary
                try {
                    String folder = String.format("assignment-submissions/assignment-%d/student-%d", assignmentId,
                            studentId);
                    String fileUrl = cloudinaryService.uploadAssignmentFile(file, folder);
                    builder.fileUrl(fileUrl);
                    builder.fileName(file.getOriginalFilename());
                } catch (Exception e) {
                    throw new RuntimeException("Failed to upload file: " + e.getMessage());
                }
            }

            submission = submissionRepository.save(builder.build());
        }

        return mapToDTO(submission);
    }

    @Override
    @Transactional
    public AssignmentSubmissionResponseDTO updateSubmission(
            Integer submissionId,
            Integer studentId,
            String submissionType,
            String linkUrl,
            MultipartFile file) {

        AssignmentSubmission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found: " + submissionId));

        // Only student can update their own submission
        if (!submission.getStudent().getUserId().equals(studentId)) {
            throw new RuntimeException("You can only update your own submission");
        }

        // Check if deadline has passed
        if (submission.getAssignment().getDueDate() != null
                && LocalDateTime.now().isAfter(submission.getAssignment().getDueDate())) {
            throw new RuntimeException("Assignment deadline has passed");
        }

        SubmissionType type = SubmissionType.valueOf(submissionType);
        submission.setSubmissionType(type);
        submission.setUpdatedAt(LocalDateTime.now());

        if (type == SubmissionType.LINK) {
            submission.setLinkUrl(linkUrl);
            submission.setFileUrl(null);
            submission.setFileName(null);
        } else {
            // Upload new file to Cloudinary
            try {
                String folder = String.format("assignment-submissions/assignment-%d/student-%d",
                        submission.getAssignment().getPostId(), studentId);
                String fileUrl = cloudinaryService.uploadAssignmentFile(file, folder);
                submission.setFileUrl(fileUrl);
                submission.setFileName(file.getOriginalFilename());
                submission.setLinkUrl(null);
            } catch (Exception e) {
                throw new RuntimeException("Failed to upload file: " + e.getMessage());
            }
        }

        AssignmentSubmission updated = submissionRepository.save(submission);
        return mapToDTO(updated);
    }

    @Override
    public AssignmentSubmissionResponseDTO getStudentSubmission(Integer assignmentId, Integer studentId) {
        AssignmentSubmission submission = submissionRepository
                .findByAssignmentPostIdAndStudentUserId(assignmentId, studentId)
                .orElse(null);

        return submission != null ? mapToDTO(submission) : null;
    }

    @Override
    public List<AssignmentSubmissionResponseDTO> getAssignmentSubmissions(Integer assignmentId) {
        List<AssignmentSubmission> submissions = submissionRepository.findByAssignmentPostId(assignmentId);

        return submissions.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentSubmissionStatusDTO> getStudentsWithSubmissionStatus(Integer assignmentId) {
        Post assignment = postRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found: " + assignmentId));

        Integer classId = assignment.getClassEntity().getClassId();

        // Get all students in the class
        List<StudentClass> studentClasses = studentClassRepository.findByClassId(classId);

        // Get all submissions for this assignment
        List<AssignmentSubmission> submissions = submissionRepository.findByAssignmentPostId(assignmentId);

        // Create a map of studentId -> submission for quick lookup
        Map<Integer, AssignmentSubmission> submissionMap = submissions.stream()
                .collect(Collectors.toMap(
                        sub -> sub.getStudent().getUserId(),
                        sub -> sub));

        // Build response with all students and their submission status
        return studentClasses.stream()
                .map(sc -> {
                    User student = sc.getStudent();
                    AssignmentSubmission submission = submissionMap.get(student.getUserId());

                    if (submission != null) {
                        // Student has submitted
                        return StudentSubmissionStatusDTO.builder()
                                .studentId(student.getUserId())
                                .studentName(student.getFullName())
                                .studentEmail(student.getEmail())
                                .hasSubmitted(true)
                                .submissionId(submission.getSubmissionId())
                                .submissionType(submission.getSubmissionType().name())
                                .linkUrl(submission.getLinkUrl())
                                .fileUrl(submission.getFileUrl())
                                .fileName(submission.getFileName())
                                .submittedAt(submission.getSubmittedAt())
                                .updatedAt(submission.getUpdatedAt())
                                .earnedPoints(submission.getEarnedPoints())
                                .build();
                    } else {
                        // Student has not submitted
                        return StudentSubmissionStatusDTO.builder()
                                .studentId(student.getUserId())
                                .studentName(student.getFullName())
                                .studentEmail(student.getEmail())
                                .hasSubmitted(false)
                                .submissionId(null)
                                .submissionType(null)
                                .linkUrl(null)
                                .fileUrl(null)
                                .fileName(null)
                                .submittedAt(null)
                                .updatedAt(null)
                                .build();
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public StudentSubmissionStatusDTO updateSubmissionGrade(Integer submissionId, Double earnedPoints) {
        AssignmentSubmission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found: " + submissionId));

        // Validate points (should be >= 0 and <= total points of assignment)
        Post assignment = submission.getAssignment();
        if (assignment.getTotalPoints() != null && earnedPoints != null) {
            if (earnedPoints < 0 || earnedPoints > assignment.getTotalPoints()) {
                throw new RuntimeException("Earned points must be between 0 and " + assignment.getTotalPoints());
            }
        }

        submission.setEarnedPoints(earnedPoints);
        submission.setUpdatedAt(LocalDateTime.now());
        AssignmentSubmission saved = submissionRepository.save(submission);

        return StudentSubmissionStatusDTO.builder()
                .studentId(saved.getStudent().getUserId())
                .studentName(saved.getStudent().getFullName())
                .studentEmail(saved.getStudent().getEmail())
                .hasSubmitted(true)
                .submissionId(saved.getSubmissionId())
                .submissionType(saved.getSubmissionType().name())
                .linkUrl(saved.getLinkUrl())
                .fileUrl(saved.getFileUrl())
                .fileName(saved.getFileName())
                .submittedAt(saved.getSubmittedAt())
                .updatedAt(saved.getUpdatedAt())
                .earnedPoints(saved.getEarnedPoints())
                .build();
    }

    private AssignmentSubmissionResponseDTO mapToDTO(AssignmentSubmission submission) {
        return AssignmentSubmissionResponseDTO.builder()
                .submissionId(submission.getSubmissionId())
                .assignmentId(submission.getAssignment().getPostId())
                .studentId(submission.getStudent().getUserId())
                .studentName(submission.getStudent().getFullName())
                .studentEmail(submission.getStudent().getEmail())
                .submissionType(submission.getSubmissionType().name())
                .linkUrl(submission.getLinkUrl())
                .fileUrl(submission.getFileUrl())
                .fileName(submission.getFileName())
                .submittedAt(submission.getSubmittedAt())
                .updatedAt(submission.getUpdatedAt())
                .build();
    }
}
