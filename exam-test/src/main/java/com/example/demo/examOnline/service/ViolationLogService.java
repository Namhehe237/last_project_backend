package com.example.demo.examOnline.service;

import com.example.demo.examOnline.dto.request.LogViolationRequest;
import com.example.demo.examOnline.dto.response.ExamStudentResponse;
import com.example.demo.examOnline.dto.response.TeacherExamResponse;
import com.example.demo.examOnline.dto.response.ViolationLogResponse;
import java.util.List;

public interface ViolationLogService {

        public void logViolation(LogViolationRequest request);

        public List<ViolationLogResponse> getViolationsByExamAndStudent(Integer examId, Integer studentId);

        public List<ExamStudentResponse> getStudentsByExam(Integer examId);

        public List<TeacherExamResponse> getExamsByTeacher(Integer teacherId);
}
