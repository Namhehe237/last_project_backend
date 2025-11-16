package com.example.demo.examOnline.controller;

import com.example.demo.examOnline.dto.response.AssignmentSubmissionResponseDTO;
import com.example.demo.examOnline.dto.response.StudentSubmissionStatusDTO;
import com.example.demo.examOnline.service.AssignmentSubmissionService;
import com.example.demo.examOnline.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/class")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class AssignmentSubmissionController {
    private final AssignmentSubmissionService submissionService;
    private final UserService userService;

    @PostMapping(value = "/assignments/{assignmentId}/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<AssignmentSubmissionResponseDTO> submitAssignment(
            @PathVariable Integer assignmentId,
            @RequestParam("submissionType") String submissionType,
            @RequestParam(value = "linkUrl", required = false) String linkUrl,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            Integer studentId = userService.getUserIdByEmail(currentUserEmail);
            if (studentId == null) {
                log.error("User not found: {}", currentUserEmail);
                return ResponseEntity.status(401).build();
            }

            log.info("Received submit assignment request - assignmentId: {}, studentId: {}, submissionType: {}", 
                    assignmentId, studentId, submissionType);

            // Validate request based on submission type
            if ("LINK".equals(submissionType)) {
                if (linkUrl == null || linkUrl.isEmpty()) {
                    log.error("Link URL is required for LINK submission type");
                    return ResponseEntity.badRequest().build();
                }
            } else if ("FILE".equals(submissionType)) {
                if (file == null || file.isEmpty()) {
                    log.error("File is required for FILE submission type");
                    return ResponseEntity.badRequest().build();
                }
            } else {
                log.error("Invalid submission type: {}", submissionType);
                return ResponseEntity.badRequest().build();
            }

            AssignmentSubmissionResponseDTO submission = submissionService.submitAssignment(
                    assignmentId, studentId, submissionType, linkUrl, file);
            
            log.info("Assignment submitted successfully - submissionId: {}", submission.getSubmissionId());
            return ResponseEntity.ok(submission);
        } catch (Exception e) {
            log.error("Error submitting assignment: ", e);
            return ResponseEntity.status(500).build();
        }
    }

    @PutMapping(value = "/submissions/{submissionId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<AssignmentSubmissionResponseDTO> updateSubmission(
            @PathVariable Integer submissionId,
            @RequestParam("submissionType") String submissionType,
            @RequestParam(value = "linkUrl", required = false) String linkUrl,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            Integer studentId = userService.getUserIdByEmail(currentUserEmail);
            if (studentId == null) {
                log.error("User not found: {}", currentUserEmail);
                return ResponseEntity.status(401).build();
            }

            log.info("Received update submission request - submissionId: {}, studentId: {}, submissionType: {}", 
                    submissionId, studentId, submissionType);

            // Validate request based on submission type
            if ("LINK".equals(submissionType)) {
                if (linkUrl == null || linkUrl.isEmpty()) {
                    log.error("Link URL is required for LINK submission type");
                    return ResponseEntity.badRequest().build();
                }
            } else if ("FILE".equals(submissionType)) {
                if (file == null || file.isEmpty()) {
                    log.error("File is required for FILE submission type");
                    return ResponseEntity.badRequest().build();
                }
            } else {
                log.error("Invalid submission type: {}", submissionType);
                return ResponseEntity.badRequest().build();
            }

            AssignmentSubmissionResponseDTO submission = submissionService.updateSubmission(
                    submissionId, studentId, submissionType, linkUrl, file);
            
            log.info("Submission updated successfully - submissionId: {}", submissionId);
            return ResponseEntity.ok(submission);
        } catch (Exception e) {
            log.error("Error updating submission: ", e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/assignments/{assignmentId}/submission")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<AssignmentSubmissionResponseDTO> getStudentSubmission(
            @PathVariable Integer assignmentId) {
        try {
            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            Integer studentId = userService.getUserIdByEmail(currentUserEmail);
            if (studentId == null) {
                log.error("User not found: {}", currentUserEmail);
                return ResponseEntity.status(401).build();
            }

            log.info("Received get submission request - assignmentId: {}, studentId: {}", assignmentId, studentId);

            AssignmentSubmissionResponseDTO submission = submissionService.getStudentSubmission(assignmentId, studentId);
            if (submission == null) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(submission);
        } catch (Exception e) {
            log.error("Error getting submission: ", e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/assignments/{assignmentId}/submissions")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<List<AssignmentSubmissionResponseDTO>> getAssignmentSubmissions(
            @PathVariable Integer assignmentId) {
        try {
            log.info("Received get submissions request - assignmentId: {}", assignmentId);

            List<AssignmentSubmissionResponseDTO> submissions = submissionService.getAssignmentSubmissions(assignmentId);
            log.info("Returning submissions - count: {}", submissions.size());
            return ResponseEntity.ok(submissions);
        } catch (Exception e) {
            log.error("Error getting submissions: ", e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/assignments/{assignmentId}/students-with-submissions")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<List<StudentSubmissionStatusDTO>> getStudentsWithSubmissionStatus(
            @PathVariable Integer assignmentId) {
        try {
            log.info("Received get students with submission status request - assignmentId: {}", assignmentId);

            List<StudentSubmissionStatusDTO> students = submissionService.getStudentsWithSubmissionStatus(assignmentId);
            log.info("Returning students with submission status - count: {}", students.size());
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            log.error("Error getting students with submission status: ", e);
            return ResponseEntity.status(500).build();
        }
    }

    @PutMapping("/submissions/{submissionId}/grade")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<StudentSubmissionStatusDTO> updateSubmissionGrade(
            @PathVariable Integer submissionId,
            @RequestParam(value = "earnedPoints", required = false) Double earnedPoints) {
        try {
            log.info("Received update submission grade request - submissionId: {}, earnedPoints: {}", 
                    submissionId, earnedPoints);

            StudentSubmissionStatusDTO updated = submissionService.updateSubmissionGrade(submissionId, earnedPoints);
            log.info("Submission grade updated successfully - submissionId: {}", submissionId);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            log.error("Error updating submission grade: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error updating submission grade: ", e);
            return ResponseEntity.status(500).build();
        }
    }
}

