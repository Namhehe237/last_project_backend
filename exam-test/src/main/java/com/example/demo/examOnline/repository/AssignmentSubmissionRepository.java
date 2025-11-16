package com.example.demo.examOnline.repository;

import com.example.demo.examOnline.domain.AssignmentSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentSubmissionRepository extends JpaRepository<AssignmentSubmission, Integer> {
    Optional<AssignmentSubmission> findByAssignmentPostIdAndStudentUserId(Integer assignmentId, Integer studentId);

    List<AssignmentSubmission> findByAssignmentPostId(Integer assignmentId);

    List<AssignmentSubmission> findByStudentUserId(Integer studentId);
}

