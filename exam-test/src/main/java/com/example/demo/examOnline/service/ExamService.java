package com.example.demo.examOnline.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import com.example.demo.examOnline.dto.request.CreateRandomExamRequest;
import com.example.demo.examOnline.dto.request.ExamFilterRequest;
import com.example.demo.examOnline.dto.response.ExamResponse;
import com.example.demo.examOnline.dto.response.AnswerPaperResponse;
import com.example.demo.examOnline.dto.response.QuestionPaperResponse;
import com.example.demo.examOnline.dto.response.ExamPaperResponse;
import com.example.demo.examOnline.dto.request.SubmitExamRequest;
import com.example.demo.examOnline.dto.request.ForceSubmitExamRequest;
import com.example.demo.examOnline.dto.response.GradeExamResponse;
import com.example.demo.examOnline.dto.response.TestHistoryResponse;
import com.example.demo.examOnline.dto.response.ExamResultDetailResponse;
import com.example.demo.examOnline.dto.response.RandomExamResponse;
import com.example.demo.examOnline.dto.request.UpdateExamQuestionsRequest;
import com.example.demo.examOnline.repository.ClassRepository;
import com.example.demo.examOnline.repository.ExamQuestionRepository;
import com.example.demo.examOnline.repository.ExamRepository;
import com.example.demo.examOnline.repository.QuestionRepository;
import com.example.demo.examOnline.repository.UserRepository;
import com.example.demo.examOnline.repository.StudentExamRepository;
import com.example.demo.examOnline.repository.StudentAnswerRepository;
import com.example.demo.examOnline.domain.StudentExam;
import com.example.demo.examOnline.domain.StudentAnswer;
import com.example.demo.examOnline.domain.Answer;
import com.example.demo.examOnline.repository.AnswerRepository;
import com.example.demo.examOnline.domain.enums.DifficultyLevel;

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
        private final StudentExamRepository studentExamRepository;
        private final StudentAnswerRepository studentAnswerRepository;
        private final AnswerRepository answerRepository;

        public void createExam(CreateExamRequest request) {
                List<QuestionsBank> questions = questionRepository.findAllById(request.getQuestionId());
                createExamInternal(request, questions);
        }

        public RandomExamResponse createRandomExam(CreateRandomExamRequest request) {
                int easyCount = request.getEasyQuestionCount() != null ? request.getEasyQuestionCount() : 0;
                int mediumCount = request.getMediumQuestionCount() != null ? request.getMediumQuestionCount() : 0;
                int hardCount = request.getHardQuestionCount() != null ? request.getHardQuestionCount() : 0;

                if (easyCount < 0 || mediumCount < 0 || hardCount < 0) {
                        throw new IllegalArgumentException("Question counts cannot be negative");
                }

                int totalRequested = easyCount + mediumCount + hardCount;
                Integer totalFromRequest = request.getTotalQuestions();
                if (totalFromRequest != null && totalFromRequest.intValue() != totalRequested) {
                        throw new IllegalArgumentException("Total questions does not match the sum of difficulty counts");
                }
                if (totalRequested <= 0) {
                        throw new IllegalArgumentException("Total number of questions must be greater than zero");
                }

                List<QuestionsBank> selectedQuestions = new ArrayList<>();
                selectedQuestions.addAll(selectRandomQuestions(DifficultyLevel.EASY, easyCount));
                selectedQuestions.addAll(selectRandomQuestions(DifficultyLevel.MEDIUM, mediumCount));
                selectedQuestions.addAll(selectRandomQuestions(DifficultyLevel.HARD, hardCount));

                List<Integer> questionIds = selectedQuestions.stream()
                                .map(QuestionsBank::getQuestionId)
                                .toList();
                request.setQuestionId(questionIds);

                Exam exam = createExamInternal(request, selectedQuestions);

                return RandomExamResponse.builder()
                                .examId(exam.getExamId())
                                .examName(exam.getExamName())
                                .subjectName(exam.getSubjectName())
                                .durationMinutes(exam.getDurationMinutes())
                                .maxAttempts(exam.getMaxAttempts())
                                .className(exam.getClassEntity() != null ? exam.getClassEntity().getClassName() : null)
                                .startTime(exam.getStartTime())
                                .endTime(exam.getEndTime())
                                .examStatus(exam.getStatus())
                                .questions(selectedQuestions.stream()
                                                .map(q -> RandomExamResponse.QuestionSummary.builder()
                                                                .questionId(q.getQuestionId())
                                                                .questionText(q.getQuestionText())
                                                                .questionType(q.getQuestionType())
                                                                .difficultyLevel(q.getDifficultyLevel())
                                                                .subjectName(q.getSubjectName())
                                                                .build())
                                                .toList())
                                .build();
        }

        private Exam createExamInternal(CreateExamRequest request, List<QuestionsBank> questions) {
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
                        e.printStackTrace();
                        throw e;
                }

                List<ExamQuestion> examQuestions = questions.stream()
                                .map(q -> new ExamQuestion(exam, q))
                                .toList();

                examQuestionRepository.saveAll(examQuestions);
                exam.setExamQuestions(examQuestions);

                cacheExamSnapshot(exam, classEntity, teacher, questions);
                return exam;
        }

        public void updateExamQuestions(Integer examId, UpdateExamQuestionsRequest request) {
                if (request.getQuestionIds() == null || request.getQuestionIds().isEmpty()) {
                        throw new IllegalArgumentException("Question list cannot be empty");
                }

                Exam exam = examRepository.findById(examId)
                                .orElseThrow(() -> new RuntimeException("Exam not found: " + examId));

                List<QuestionsBank> questions = questionRepository.findAllById(request.getQuestionIds());
                if (questions.size() != request.getQuestionIds().size()) {
                        throw new RuntimeException("Some questions do not exist");
                }

                examQuestionRepository.deleteByExamExamId(examId);

                List<ExamQuestion> examQuestions = questions.stream()
                                .map(q -> new ExamQuestion(exam, q))
                                .toList();

                examQuestionRepository.saveAll(examQuestions);
                exam.setExamQuestions(examQuestions);

                examCacheService.evictExam(examId);
                cacheExamSnapshot(exam, exam.getClassEntity(), exam.getTeacher(), questions);
        }

        private List<QuestionsBank> selectRandomQuestions(DifficultyLevel level, int count) {
                if (count <= 0) {
                        return List.of();
                }

                List<QuestionsBank> questions = questionRepository.findRandomQuestionsByDifficulty(level,
                                PageRequest.of(0, count));

                if (questions.size() < count) {
                        throw new IllegalArgumentException(String.format("Not enough %s questions available", level.name()));
                }

                return questions;
        }

        private void cacheExamSnapshot(Exam exam, Classes classEntity, User teacher, List<QuestionsBank> questions) {
                ExamSnapshot snapshot = ExamSnapshot.builder()
                                .examId(exam.getExamId())
                                .examName(exam.getExamName())
                                .subjectName(exam.getSubjectName())
                                .durationMinutes(exam.getDurationMinutes())
                                .maxAttempts(exam.getMaxAttempts())
                                .className(classEntity != null ? classEntity.getClassName() : null)
                                .teacherName(teacher != null ? teacher.getFullName() : null)
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

                // Save score and submitTime to StudentExam, and save student answers
                saveExamResult(request.getExamId(), request.getStudentId(), score, total, correct, details);

        return GradeExamResponse.builder()
                .examId(s.getExamId())
                .totalQuestions(total)
                .correctAnswers(correct)
                .score(score)
                .details(details)
                .build();
        }

        private void saveExamResult(Integer examId, Integer studentId, Double score, Integer totalQuestions, Integer correctAnswers, List<GradeExamResponse.ResultDetail> details) {
                try {
                        if (examId == null || studentId == null) {
                                System.err.println("Error: examId or studentId is null - examId: " + examId + ", studentId: " + studentId);
                                return;
                        }
                        
                        StudentExam studentExam = studentExamRepository
                                        .findByStudentUserIdAndExamExamId(studentId, examId)
                                        .orElseGet(() -> {
                                                Exam exam = examRepository.findById(examId)
                                                                .orElseThrow(() -> new RuntimeException("Exam not found: " + examId));
                                                User student = userRepository.findById(studentId)
                                                                .orElseThrow(() -> new RuntimeException("Student not found: " + studentId));
                                                
                                                com.example.demo.examOnline.domain.StudentExamId id = 
                                                        com.example.demo.examOnline.domain.StudentExamId.builder()
                                                                .studentId(studentId)
                                                                .examId(examId)
                                                                .build();
                                                
                                                return StudentExam.builder()
                                                                .id(id)
                                                                .exam(exam)
                                                                .student(student)
                                                                .startTime(LocalDateTime.now())
                                                                .status(com.example.demo.examOnline.domain.enums.StudentExamStatus.IN_PROGRESS)
                                                                .build();
                                        });

                        studentExam.setScore(score);
                        studentExam.setSubmitTime(LocalDateTime.now());
                        studentExam.setStatus(com.example.demo.examOnline.domain.enums.StudentExamStatus.SUBMITTED);
                        
                        System.out.println("Saving exam result - examId: " + examId + ", studentId: " + studentId + ", score: " + score + ", submitTime: " + LocalDateTime.now());
                        
                        StudentExam saved = studentExamRepository.save(studentExam);
                        System.out.println("Successfully saved exam result - examId: " + saved.getId().getExamId() + ", studentId: " + saved.getId().getStudentId() + ", score: " + saved.getScore() + ", submitTime: " + saved.getSubmitTime());
                        
                        // Save student answers
                        if (details != null) {
                                for (GradeExamResponse.ResultDetail detail : details) {
                                        QuestionsBank question = questionRepository.findById(detail.getQuestionId())
                                                        .orElse(null);
                                        if (question == null) continue;
                                        
                                        Answer chosenAnswer = null;
                                        if (detail.getChosenAnswerId() != null) {
                                                chosenAnswer = answerRepository.findById(detail.getChosenAnswerId()).orElse(null);
                                        }
                                        
                                        StudentAnswer studentAnswer = StudentAnswer.builder()
                                                        .studentExam(saved)
                                                        .question(question)
                                                        .chosenAnswer(chosenAnswer)
                                                        .isCorrect(detail.isCorrect())
                                                        .build();
                                        
                                        studentAnswerRepository.save(studentAnswer);
                                }
                        }
                } catch (Exception e) {
                        System.err.println("Error saving exam result - examId: " + examId + ", studentId: " + studentId + ", error: " + e.getMessage());
                        e.printStackTrace();
                        // Don't throw exception to avoid breaking the grading response
                }
        }

        public GradeExamResponse forceSubmitExam(ForceSubmitExamRequest request) {
                ExamSnapshot s = getOrBuildExamSnapshot(request.getExamId());

                int total = s.getQuestions().size();
                int correct = 0; // Force 0 correct answers

                var details = new java.util.ArrayList<GradeExamResponse.ResultDetail>();

                // Create empty answers - all incorrect
                for (var q : s.getQuestions()) {
                        details.add(GradeExamResponse.ResultDetail.builder()
                                        .questionId(q.getQuestionId())
                                        .chosenAnswerId(null)
                                        .correct(false)
                                        .build());
                }

                double score = 0.0; // Force 0 points

                // Save exam result with 0 score for violation
                saveExamResult(request.getExamId(), request.getStudentId(), score, total, correct, details);

                return GradeExamResponse.builder()
                                .examId(s.getExamId())
                                .totalQuestions(total)
                                .correctAnswers(correct)
                                .score(score)
                                .details(details)
                                .build();
        }

        public void saveVideoUrl(Integer examId, Integer studentId, String videoUrl) {
                StudentExam studentExam = studentExamRepository
                                .findByStudentUserIdAndExamExamId(studentId, examId)
                                .orElseGet(() -> {
                                        Exam exam = examRepository.findById(examId)
                                                        .orElseThrow(() -> new RuntimeException("Exam not found: " + examId));
                                        User student = userRepository.findById(studentId)
                                                        .orElseThrow(() -> new RuntimeException("Student not found: " + studentId));
                                        
                                        com.example.demo.examOnline.domain.StudentExamId id = 
                                                com.example.demo.examOnline.domain.StudentExamId.builder()
                                                        .studentId(studentId)
                                                        .examId(examId)
                                                        .build();
                                        
                                        return StudentExam.builder()
                                                        .id(id)
                                                        .exam(exam)
                                                        .student(student)
                                                        .startTime(LocalDateTime.now())
                                                        .status(com.example.demo.examOnline.domain.enums.StudentExamStatus.IN_PROGRESS)
                                                        .build();
                                });

                studentExam.setVideoUrl(videoUrl);
                // Update submitTime when video is uploaded (usually happens after exam submission)
                if (studentExam.getSubmitTime() == null) {
                        studentExam.setSubmitTime(LocalDateTime.now());
                }
                studentExamRepository.save(studentExam);
        }

        public List<TestHistoryResponse> getTestHistory(Integer studentId) {
                List<com.example.demo.examOnline.domain.enums.StudentExamStatus> statuses = java.util.Arrays.asList(
                                com.example.demo.examOnline.domain.enums.StudentExamStatus.SUBMITTED,
                                com.example.demo.examOnline.domain.enums.StudentExamStatus.GRADED
                );
                
                List<StudentExam> studentExams = studentExamRepository.findTestHistoryByStudentId(studentId, statuses);
                
                return studentExams.stream().map(se -> {
                        Exam exam = se.getExam();
                        int totalQuestions = exam.getExamQuestions() != null ? exam.getExamQuestions().size() : 0;
                        int correctAnswers = 0;
                        if (se.getScore() != null && totalQuestions > 0) {
                                // Score is on 0-10 scale, so correctAnswers = score * totalQuestions / 10
                                correctAnswers = (int) Math.round(se.getScore() * totalQuestions / 10.0);
                        }
                        return TestHistoryResponse.builder()
                                        .examId(exam.getExamId())
                                        .examName(exam.getExamName())
                                        .subjectName(exam.getSubjectName())
                                        .score(se.getScore())
                                        .totalQuestions(totalQuestions)
                                        .correctAnswers(correctAnswers)
                                        .submitTime(se.getSubmitTime())
                                        .videoUrl(se.getVideoUrl())
                                        .status(se.getStatus() != null ? se.getStatus().name() : null)
                                        .build();
                }).toList();
        }

        public ExamResultDetailResponse getExamResultDetail(Integer examId, Integer studentId) {
                ExamSnapshot examSnapshot = getOrBuildExamSnapshot(examId);
                StudentExam studentExam = studentExamRepository
                                .findByStudentUserIdAndExamExamId(studentId, examId)
                                .orElseThrow(() -> new RuntimeException("Student exam not found"));
                
                List<StudentAnswer> studentAnswers = studentAnswerRepository.findByStudentIdAndExamId(studentId, examId);
                var answerMap = studentAnswers.stream()
                                .collect(java.util.stream.Collectors.toMap(
                                                sa -> sa.getQuestion().getQuestionId(),
                                                sa -> sa));
                
                List<ExamResultDetailResponse.QuestionResultDetail> questionDetails = examSnapshot.getQuestions().stream()
                                .map(q -> {
                                        StudentAnswer studentAnswer = answerMap.get(q.getQuestionId());
                                        Integer chosenAnswerId = studentAnswer != null && studentAnswer.getChosenAnswer() != null
                                                        ? studentAnswer.getChosenAnswer().getAnswerId()
                                                        : null;
                                        Integer correctAnswerId = q.getAnswers().stream()
                                                        .filter(a -> a.isCorrect())
                                                        .map(a -> a.getAnswerId())
                                                        .findFirst()
                                                        .orElse(null);
                                        Boolean isCorrect = studentAnswer != null ? studentAnswer.getIsCorrect() : false;
                                        
                                        List<ExamResultDetailResponse.AnswerDetail> answerDetails = q.getAnswers().stream()
                                                        .map(a -> ExamResultDetailResponse.AnswerDetail.builder()
                                                                        .answerId(a.getAnswerId())
                                                                        .answerText(a.getAnswerText())
                                                                        .isCorrect(a.isCorrect())
                                                                        .build())
                                                        .toList();
                                        
                                        return ExamResultDetailResponse.QuestionResultDetail.builder()
                                                        .questionId(q.getQuestionId())
                                                        .questionText(q.getQuestionText())
                                                        .answers(answerDetails)
                                                        .chosenAnswerId(chosenAnswerId)
                                                        .correctAnswerId(correctAnswerId)
                                                        .isCorrect(isCorrect)
                                                        .build();
                                })
                                .toList();
                
                int totalQuestions = examSnapshot.getQuestions().size();
                int correctAnswers = (int) questionDetails.stream().filter(q -> q.getIsCorrect()).count();
                
                return ExamResultDetailResponse.builder()
                                .examId(examId)
                                .examName(examSnapshot.getExamName())
                                .subjectName(examSnapshot.getSubjectName())
                                .score(studentExam.getScore())
                                .totalQuestions(totalQuestions)
                                .correctAnswers(correctAnswers)
                                .submitTime(studentExam.getSubmitTime())
                                .questions(questionDetails)
                                .build();
        }
}