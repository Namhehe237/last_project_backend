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
import com.example.demo.examOnline.dto.cache.AnswerSnapshot;
import com.example.demo.examOnline.dto.cache.ExamSnapshot;
import com.example.demo.examOnline.dto.cache.QuestionSnapshot;
import com.example.demo.examOnline.dto.request.CreateExamRequest;
import com.example.demo.examOnline.dto.request.ExamFilterRequest;
import com.example.demo.examOnline.dto.response.ExamResponse;
import com.example.demo.examOnline.dto.response.AnswerPaperResponse;
import com.example.demo.examOnline.dto.response.QuestionPaperResponse;
import com.example.demo.examOnline.dto.response.ExamPaperResponse;
import com.example.demo.examOnline.dto.request.SubmitExamRequest;
import com.example.demo.examOnline.dto.response.GradeExamResponse;
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
        private final ExamCacheService examCacheService;

        public void createExam(CreateExamRequest request) {
                Classes classEntity = classRepository.findByClassName(request.getClassName())
                                .orElseThrow(() -> new RuntimeException("Class not found: " + request.getClassName()));

                User teacher = userRepository.findById(request.getTeacherId())
                                .orElseThrow(() -> new RuntimeException(
                                                "Teacher not found: " + request.getTeacherId()));

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

                // Build snapshot and cache in Redis for fast retrieval during exam
                ExamSnapshot snapshot = ExamSnapshot.builder()
                                .examId(exam.getExamId())
                                .examName(exam.getExamName())
                                .subjectName(exam.getSubjectName())
                                .durationMinutes(exam.getDurationMinutes())
                                .maxAttempts(exam.getMaxAttempts())
                                .className(classEntity.getClassName())
                                .teacherName(teacher.getFullName())
                                .startTime(exam.getStartTime())
                                .endTime(exam.getEndTime())
                                .questions(questions.stream().map(q -> QuestionSnapshot.builder()
                                                .questionId(q.getQuestionId())
                                                .questionText(q.getQuestionText())
                                                .questionType(q.getQuestionType())
                                                .difficultyLevel(q.getDifficultyLevel())
                                                .subjectName(q.getSubjectName())
                                                .teacherName(q.getTeacher() != null ? q.getTeacher().getFullName() : null)
                                                .answers(q.getAnswers().stream().map(a -> AnswerSnapshot.builder()
                                                                .answerId(a.getAnswerId())
                                                                .answerText(a.getAnswerText())
                                                                .correct(Boolean.TRUE.equals(a.getIsCorrect()))
                                                                .build())
                                                                .toList())
                                                .build())
                                                .toList())
                                .build();

                examCacheService.cacheExam(snapshot);

        }

        public void deleteExam(Integer examId) {
                Exam exam = examRepository.findById(examId)
                                .orElseThrow(() -> new RuntimeException("Không có lớp này"));

                examRepository.delete(exam);
                examCacheService.evictExam(examId);
        }

        public ExamResponse getExamDetail(Integer examId) {

                ExamResponse examResponse = examRepository.getExamDetail(examId);
                examResponse.setQuestionId(examQuestionRepository.getExamQuestion(examId));

                return examResponse;

        }

        public Page<ExamResponse> getListExam(ExamFilterRequest request, Pageable pageable) {
                return examRepository.getListExams(request.getClassId(), request.getStudentId(), pageable);
        }

        public ExamSnapshot getOrBuildExamSnapshot(Integer examId) {
                ExamSnapshot fromCache = examCacheService.getExam(examId);
                if (fromCache != null) {
                        return fromCache;
                }

                Exam exam = examRepository.findById(examId)
                                .orElseThrow(() -> new RuntimeException("Exam not found: " + examId));

                List<Integer> questionIds = examQuestionRepository.getExamQuestion(examId);
                List<QuestionsBank> questions = questionRepository.findAllById(questionIds);

                ExamSnapshot snapshot = ExamSnapshot.builder()
                                .examId(exam.getExamId())
                                .examName(exam.getExamName())
                                .subjectName(exam.getSubjectName())
                                .durationMinutes(exam.getDurationMinutes())
                                .maxAttempts(exam.getMaxAttempts())
                                .className(exam.getClassEntity() != null ? exam.getClassEntity().getClassName() : null)
                                .teacherName(exam.getTeacher() != null ? exam.getTeacher().getFullName() : null)
                                .startTime(exam.getStartTime())
                                .endTime(exam.getEndTime())
                                .questions(questions.stream().map(q -> QuestionSnapshot.builder()
                                                .questionId(q.getQuestionId())
                                                .questionText(q.getQuestionText())
                                                .questionType(q.getQuestionType())
                                                .difficultyLevel(q.getDifficultyLevel())
                                                .subjectName(q.getSubjectName())
                                                .teacherName(q.getTeacher() != null ? q.getTeacher().getFullName() : null)
                                                .answers(q.getAnswers().stream().map(a -> AnswerSnapshot.builder()
                                                                .answerId(a.getAnswerId())
                                                                .answerText(a.getAnswerText())
                                                                .correct(Boolean.TRUE.equals(a.getIsCorrect()))
                                                                .build()).toList())
                                                .build()).toList())
                                .build();

                examCacheService.cacheExam(snapshot);
                return snapshot;
        }

        public ExamPaperResponse getExamPaper(Integer examId) {
                ExamSnapshot s = getOrBuildExamSnapshot(examId);
                return ExamPaperResponse.builder()
                                .examId(s.getExamId())
                                .examName(s.getExamName())
                                .subjectName(s.getSubjectName())
                                .durationMinutes(s.getDurationMinutes())
                                .startTime(s.getStartTime())
                                .endTime(s.getEndTime())
                                .questions(s.getQuestions().stream().map(q -> QuestionPaperResponse.builder()
                                                .questionId(q.getQuestionId())
                                                .questionText(q.getQuestionText())
                                                .questionType(q.getQuestionType())
                                                .difficultyLevel(q.getDifficultyLevel())
                                                .answers(q.getAnswers().stream().map(a -> AnswerPaperResponse.builder()
                                                                .answerId(a.getAnswerId())
                                                                .answerText(a.getAnswerText())
                                                                .build()).toList())
                                                .build()).toList())
                                .build();
        }

        public GradeExamResponse gradeExam(SubmitExamRequest request) {
                ExamSnapshot s = getOrBuildExamSnapshot(request.getExamId());

                int total = s.getQuestions().size();
                int correct = 0;

                var answerMap = request.getAnswers().stream()
                                .collect(java.util.stream.Collectors.toMap(SubmitExamRequest.SubmitItem::getQuestionId,
                                                SubmitExamRequest.SubmitItem::getAnswerId));

                var details = new java.util.ArrayList<GradeExamResponse.ResultDetail>();

                for (var q : s.getQuestions()) {
                        Integer chosen = answerMap.get(q.getQuestionId());
                        boolean isCorrect = false;
                        if (chosen != null) {
                                isCorrect = q.getAnswers().stream()
                                                .anyMatch(a -> a.getAnswerId().equals(chosen) && a.isCorrect());
                        }
                        if (isCorrect)
                                correct++;
                        details.add(GradeExamResponse.ResultDetail.builder()
                                        .questionId(q.getQuestionId())
                                        .chosenAnswerId(chosen)
                                        .correct(isCorrect)
                                        .build());
                }

                double score = total == 0 ? 0.0 : Math.round((correct * 10.0 / total) * 100.0) / 100.0;

                return GradeExamResponse.builder()
                                .examId(s.getExamId())
                                .totalQuestions(total)
                                .correctAnswers(correct)
                                .score(score)
                                .details(details)
                                .build();
        }
}