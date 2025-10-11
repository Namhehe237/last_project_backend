package com.example.demo.examOnline.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.examOnline.domain.Classes;
import com.example.demo.examOnline.domain.Exam;
import com.example.demo.examOnline.domain.ExamQuestion;
import com.example.demo.examOnline.domain.QuestionsBank;
import com.example.demo.examOnline.domain.User;
import com.example.demo.examOnline.dto.request.CreateExamRequest;
import com.example.demo.examOnline.dto.request.ExamFilterRequest;
import com.example.demo.examOnline.dto.response.ExamResponse;
import com.example.demo.examOnline.repository.ClassRepository;
import com.example.demo.examOnline.repository.ExamQuestionRepository;
import com.example.demo.examOnline.repository.ExamRepository;
import com.example.demo.examOnline.repository.QuestionRepository;
import com.example.demo.examOnline.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExamService {
        private final ExamRepository examRepository;
        private final ClassRepository classRepository;
        private final UserRepository userRepository;
        private final QuestionRepository questionRepository;
        private final ExamQuestionRepository examQuestionRepository;

        public void createExam(CreateExamRequest request) {
                Classes classEntity = classRepository.findByClassName(request.getClassName())
                                .orElseThrow(() -> new RuntimeException("Class not found: " + request.getClassName()));

                User teacher = userRepository.findByFullName(request.getTeacherName())
                                .orElseThrow(() -> new RuntimeException(
                                                "Teacher not found: " + request.getTeacherName()));

                Exam exam = Exam.builder()
                                .examName(request.getExamName())
                                .subjectName(request.getSubjectName())
                                .durationMinutes(request.getDurationMinutes())
                                .maxAttempts(request.getMaxAttempts())
                                .classEntity(classEntity)
                                .teacher(teacher)
                                .status(request.getExamStatus())
                                .startTime(request.getStartTime())
                                .endTime(request.getEndTime())
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();

                try {
                        examRepository.save(exam);
                } catch (DataIntegrityViolationException e) {
                        e.printStackTrace(); // log chi tiết
                        throw e;
                }

                // Thêm question vào exam
                List<QuestionsBank> questions = questionRepository.findAllById(request.getQuestionId());
                List<ExamQuestion> examQuestions = questions.stream()
                                .map(q -> new ExamQuestion(exam, q))
                                .toList();

                examQuestionRepository.saveAll(examQuestions);

                exam.setExamQuestions(examQuestions);

        }

        public void deleteExam(Integer examId) {
                Exam exam = examRepository.findById(examId)
                                .orElseThrow(() -> new RuntimeException("Không có lớp này"));

                examRepository.delete(exam);
        }

        public ExamResponse getExamDetail(Integer examId) {

                ExamResponse examResponse = examRepository.getExamDetail(examId);
                examResponse.setQuestionId(examQuestionRepository.getExamQuestion(examId));

                return examResponse;

        }

        public Page<ExamResponse> getListExam(ExamFilterRequest request, Pageable pageable) {
                return examRepository.getListExams(request.getClassId(), request.getStudentId(), pageable);
        }
}