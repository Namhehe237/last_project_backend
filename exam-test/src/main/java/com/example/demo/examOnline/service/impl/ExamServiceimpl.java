package com.example.demo.examOnline.service.impl;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
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
import com.example.demo.examOnline.service.ExamCacheService;
import com.example.demo.examOnline.service.ExamService;
import com.example.demo.examOnline.service.NotificationService;
import com.example.demo.examOnline.repository.StudentExamRepository;
import com.example.demo.examOnline.repository.StudentAnswerRepository;
import com.example.demo.examOnline.domain.StudentExam;
import com.example.demo.examOnline.domain.StudentAnswer;
import com.example.demo.examOnline.domain.Answer;
import com.example.demo.examOnline.repository.AnswerRepository;
import com.example.demo.examOnline.domain.enums.DifficultyLevel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExamServiceimpl implements ExamService {
        private final ExamRepository examRepository;
        private final ClassRepository classRepository;
        private final UserRepository userRepository;
        private final QuestionRepository questionRepository;
        private final ExamQuestionRepository examQuestionRepository;
        private final ExamCacheService examCacheService;
        private final StudentExamRepository studentExamRepository;
        private final StudentAnswerRepository studentAnswerRepository;
        private final AnswerRepository answerRepository;
        private final NotificationService notificationService;
        @Qualifier("redisTemplateForgotPassword")
        private final RedisTemplate<String, Object> redisTemplateForgotPassword;

        @Override
        public void createExam(CreateExamRequest request) {
                System.out.println("=== SYSTEM.OUT: ExamService.createExam called ===");
                log.info(">>> createExam called: examName={}, className={}, teacherId={}",
                                request.getExamName(), request.getClassName(), request.getTeacherId());
                List<QuestionsBank> questions = questionRepository.findAllById(request.getQuestionId());
                log.info("Found {} questions for exam", questions.size());
                System.out.println("Found " + questions.size() + " questions");
                Exam exam = createExamInternal(request, questions);
                log.info("<<< createExam completed: examId={}", exam.getExamId());
                System.out.println("=== SYSTEM.OUT: ExamService.createExam completed, examId=" + exam.getExamId()
                                + " ===");
        }

        @Override
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
                        throw new IllegalArgumentException(
                                        "Total questions does not match the sum of difficulty counts");
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
                log.info(">>> createExamInternal started");
                Classes classEntity = classRepository.findByClassName(request.getClassName())
                                .orElseThrow(() -> {
                                        log.error("Class not found: {}", request.getClassName());
                                        return new RuntimeException("Class not found: " + request.getClassName());
                                });
                log.info("Found class: classId={}, className={}", classEntity.getClassId(), classEntity.getClassName());

                User teacher = userRepository.findById(request.getTeacherId())
                                .orElseThrow(() -> {
                                        log.error("Teacher not found: {}", request.getTeacherId());
                                        return new RuntimeException("Teacher not found: " + request.getTeacherId());
                                });
                log.info("Found teacher: teacherId={}, teacherName={}", teacher.getUserId(), teacher.getFullName());

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

                log.info("Saving exam to database...");
                try {
                        examRepository.save(exam);
                        log.info("Exam saved successfully: examId={}", exam.getExamId());
                } catch (DataIntegrityViolationException e) {
                        log.error("Error saving exam: {}", e.getMessage(), e);
                        e.printStackTrace();
                        throw e;
                }

                log.info("Creating exam questions...");
                List<ExamQuestion> examQuestions = questions.stream()
                                .map(q -> new ExamQuestion(exam, q))
                                .toList();

                examQuestionRepository.saveAll(examQuestions);
                exam.setExamQuestions(examQuestions);
                log.info("Saved {} exam questions", examQuestions.size());

                log.info("Caching exam snapshot...");
                try {
                        cacheExamSnapshot(exam, classEntity, teacher, questions);
                        log.info("Exam snapshot cached");
                } catch (Exception e) {
                        log.error("Error caching exam snapshot: {}", e.getMessage(), e);
                        // Continue even if caching fails
                }

                // Notify all students in the class about the new exam
                System.out.println("=== SYSTEM.OUT: About to notify students ===");
                System.out.flush(); // Force flush to ensure output
                log.info("=== START: Notifying students about new exam ===");
                log.info("About to call notifyStudentsInClass - examId={}, classId={}", exam.getExamId(),
                                classEntity.getClassId());
                log.info("Exam ID: {}, Exam Name: {}, Class ID: {}, Class Name: {}",
                                exam.getExamId(), exam.getExamName(), classEntity.getClassId(),
                                classEntity.getClassName());
                System.out.println("Exam ID: " + exam.getExamId() + ", Class ID: " + classEntity.getClassId());
                try {
                        String title = "Bài thi mới";
                        String message = String.format(
                                        "Giáo viên %s đã tạo bài thi '%s' cho lớp %s. Thời gian làm bài: %d phút. Bắt đầu: %s, Kết thúc: %s",
                                        teacher.getFullName(),
                                        exam.getExamName(),
                                        classEntity.getClassName(),
                                        exam.getDurationMinutes(),
                                        exam.getStartTime() != null ? exam.getStartTime().toString() : "Chưa xác định",
                                        exam.getEndTime() != null ? exam.getEndTime().toString() : "Chưa xác định");
                        log.info("Notification title: {}, message: {}", title, message);
                        log.info("Calling notifyStudentsInClass with classId={}, senderId={}", classEntity.getClassId(),
                                        teacher.getUserId());
                        notificationService.notifyStudentsInClass(
                                        classEntity.getClassId(),
                                        title,
                                        message,
                                        com.example.demo.examOnline.domain.enums.NotificationType.EXAM,
                                        teacher.getUserId());
                        log.info("=== END: Successfully completed notification process for exam {} ===",
                                        exam.getExamId());
                } catch (Exception e) {
                        // Log error but don't fail exam creation
                        log.error("=== ERROR: Failed to send exam notifications for exam {} ===", exam.getExamId(), e);
                }

                return exam;
        }

        @Override
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
                        throw new IllegalArgumentException(
                                        String.format("Not enough %s questions available", level.name()));
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
                                                .teacherName(q.getTeacher() != null ? q.getTeacher().getFullName()
                                                                : null)
                                                .answers(q.getAnswers().stream().map(a -> AnswerSnapshot.builder()
                                                                .answerId(a.getAnswerId())
                                                                .answerText(a.getAnswerText())
                                                                .correct(Boolean.TRUE.equals(a.getIsCorrect()))
                                                                .build())
                                                                .toList())
                                                .build())
                                                .toList())
                                .build();

                redisTemplateForgotPassword.opsForValue().set(String.valueOf(exam.getExamId()), snapshot,
                                Duration.between(LocalDateTime.now(), exam.getEndTime()).toMinutes(),
                                java.util.concurrent.TimeUnit.MINUTES);
        }

        @Override
        public void deleteExam(Integer examId) {
                Exam exam = examRepository.findById(examId)
                                .orElseThrow(() -> new RuntimeException("Không có lớp này"));

                examRepository.delete(exam);
                examCacheService.evictExam(examId);
        }

        @Override
        public ExamResponse getExamDetail(Integer examId) {

                ExamResponse examResponse = examRepository.getExamDetail(examId);
                examResponse.setQuestionId(examQuestionRepository.getExamQuestion(examId));

                return examResponse;

        }

        @Override
        public Page<ExamResponse> getListExam(ExamFilterRequest request, Pageable pageable) {
                log.info("getListExam called with classId={}, studentId={}", request.getClassId(),
                                request.getStudentId());
                LocalDateTime now = LocalDateTime.now();
                System.out.println(now);
                Page<ExamResponse> result = examRepository.getListExams(request.getClassId(), request.getStudentId(),
                                now,
                                pageable);
                log.info("getListExam returned {} exams (page={}, size={})", result.getTotalElements(),
                                pageable.getPageNumber(), pageable.getPageSize());
                return result;
        }

        @Override
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
                                                .teacherName(q.getTeacher() != null ? q.getTeacher().getFullName()
                                                                : null)
                                                .answers(q.getAnswers().stream().map(a -> AnswerSnapshot.builder()
                                                                .answerId(a.getAnswerId())
                                                                .answerText(a.getAnswerText())
                                                                .correct(Boolean.TRUE.equals(a.getIsCorrect()))
                                                                .build()).toList())
                                                .build()).toList())
                                .build();

                examCacheService.cacheExam(exam.getExamId(), snapshot);
                return snapshot;
        }

        @Override
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

        @Override
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

        private void saveExamResult(Integer examId, Integer studentId, Double score, Integer totalQuestions,
                        Integer correctAnswers, List<GradeExamResponse.ResultDetail> details) {
                try {
                        if (examId == null || studentId == null) {
                                System.err.println("Error: examId or studentId is null - examId: " + examId
                                                + ", studentId: " + studentId);
                                return;
                        }

                        StudentExam studentExam = studentExamRepository
                                        .findByStudentUserIdAndExamExamId(studentId, examId)
                                        .orElseGet(() -> {
                                                Exam exam = examRepository.findById(examId)
                                                                .orElseThrow(() -> new RuntimeException(
                                                                                "Exam not found: " + examId));
                                                User student = userRepository.findById(studentId)
                                                                .orElseThrow(() -> new RuntimeException(
                                                                                "Student not found: " + studentId));

                                                com.example.demo.examOnline.domain.StudentExamId id = com.example.demo.examOnline.domain.StudentExamId
                                                                .builder()
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

                        System.out.println("Saving exam result - examId: " + examId + ", studentId: " + studentId
                                        + ", score: " + score + ", submitTime: " + LocalDateTime.now());

                        StudentExam saved = studentExamRepository.save(studentExam);
                        System.out.println("Successfully saved exam result - examId: " + saved.getId().getExamId()
                                        + ", studentId: " + saved.getId().getStudentId() + ", score: "
                                        + saved.getScore() + ", submitTime: " + saved.getSubmitTime());

                        // Save student answers
                        if (details != null) {
                                for (GradeExamResponse.ResultDetail detail : details) {
                                        QuestionsBank question = questionRepository.findById(detail.getQuestionId())
                                                        .orElse(null);
                                        if (question == null)
                                                continue;

                                        Answer chosenAnswer = null;
                                        if (detail.getChosenAnswerId() != null) {
                                                chosenAnswer = answerRepository.findById(detail.getChosenAnswerId())
                                                                .orElse(null);
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
                        System.err.println("Error saving exam result - examId: " + examId + ", studentId: " + studentId
                                        + ", error: " + e.getMessage());
                        e.printStackTrace();
                        // Don't throw exception to avoid breaking the grading response
                }
        }

        @Override
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

        @Override
        public void saveVideoUrl(Integer examId, Integer studentId, String videoUrl) {
                StudentExam studentExam = studentExamRepository
                                .findByStudentUserIdAndExamExamId(studentId, examId)
                                .orElseGet(() -> {
                                        Exam exam = examRepository.findById(examId)
                                                        .orElseThrow(() -> new RuntimeException(
                                                                        "Exam not found: " + examId));
                                        User student = userRepository.findById(studentId)
                                                        .orElseThrow(() -> new RuntimeException(
                                                                        "Student not found: " + studentId));

                                        com.example.demo.examOnline.domain.StudentExamId id = com.example.demo.examOnline.domain.StudentExamId
                                                        .builder()
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
                // Update submitTime when video is uploaded (usually happens after exam
                // submission)
                if (studentExam.getSubmitTime() == null) {
                        studentExam.setSubmitTime(LocalDateTime.now());
                }
                studentExamRepository.save(studentExam);
        }

        @Override
        public List<TestHistoryResponse> getTestHistory(Integer studentId) {
                List<com.example.demo.examOnline.domain.enums.StudentExamStatus> statuses = java.util.Arrays.asList(
                                com.example.demo.examOnline.domain.enums.StudentExamStatus.SUBMITTED,
                                com.example.demo.examOnline.domain.enums.StudentExamStatus.GRADED);

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

        @Override
        public ExamResultDetailResponse getExamResultDetail(Integer examId, Integer studentId) {
                ExamSnapshot examSnapshot = getOrBuildExamSnapshot(examId);
                StudentExam studentExam = studentExamRepository
                                .findByStudentUserIdAndExamExamId(studentId, examId)
                                .orElseThrow(() -> new RuntimeException("Student exam not found"));

                List<StudentAnswer> studentAnswers = studentAnswerRepository.findByStudentIdAndExamId(studentId,
                                examId);
                var answerMap = studentAnswers.stream()
                                .collect(java.util.stream.Collectors.toMap(
                                                sa -> sa.getQuestion().getQuestionId(),
                                                sa -> sa));

                List<ExamResultDetailResponse.QuestionResultDetail> questionDetails = examSnapshot.getQuestions()
                                .stream()
                                .map(q -> {
                                        StudentAnswer studentAnswer = answerMap.get(q.getQuestionId());
                                        Integer chosenAnswerId = studentAnswer != null
                                                        && studentAnswer.getChosenAnswer() != null
                                                                        ? studentAnswer.getChosenAnswer().getAnswerId()
                                                                        : null;
                                        Integer correctAnswerId = q.getAnswers().stream()
                                                        .filter(a -> a.isCorrect())
                                                        .map(a -> a.getAnswerId())
                                                        .findFirst()
                                                        .orElse(null);
                                        Boolean isCorrect = studentAnswer != null ? studentAnswer.getIsCorrect()
                                                        : false;

                                        List<ExamResultDetailResponse.AnswerDetail> answerDetails = q.getAnswers()
                                                        .stream()
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