package com.example.demo.examOnline.service;

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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public interface AssignmentSubmissionService {
   

    public AssignmentSubmissionResponseDTO submitAssignment(
            Integer assignmentId, 
            Integer studentId, 
            String submissionType,
            String linkUrl,
            MultipartFile file);

    public AssignmentSubmissionResponseDTO updateSubmission(
            Integer submissionId,
            Integer studentId,
            String submissionType,
            String linkUrl,
            MultipartFile file);

    public AssignmentSubmissionResponseDTO getStudentSubmission(Integer assignmentId, Integer studentId);

    public List<AssignmentSubmissionResponseDTO> getAssignmentSubmissions(Integer assignmentId) ;

    public List<StudentSubmissionStatusDTO> getStudentsWithSubmissionStatus(Integer assignmentId) ;

    @Transactional
    public StudentSubmissionStatusDTO updateSubmissionGrade(Integer submissionId, Double earnedPoints);

}

