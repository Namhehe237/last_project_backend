package com.example.demo.examOnline.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.demo.examOnline.domain.Classes;
import com.example.demo.examOnline.domain.Exam;
import com.example.demo.examOnline.domain.ExamQuestion;
import com.example.demo.examOnline.domain.QuestionsBank;
import com.example.demo.examOnline.domain.User;
import com.example.demo.examOnline.dto.request.CreateExamRequest;
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
        private final RedisTemplate<String, Object> redisTemplate;

        @CachePut(value = "exams", key = "#result.examId", condition = "#result != null")
        public ExamResponse createExam(CreateExamRequest request) {
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
                                .startTime(request.getStartTime()) // Có thể null
                                .endTime(request.getEndTime()) // Có thể null
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();

                try {
                        exam = examRepository.save(exam);
                        
                        // Cache exam data vào Redis
                        String examKey = "exam:" + exam.getExamId();
                        redisTemplate.opsForValue().set(examKey, exam);
                        
                        // Cache exam list theo class
                        String classExamsKey = "class:" + classEntity.getClassId() + ":exams";
                        redisTemplate.delete(classExamsKey); // Xóa cache cũ để refresh
                        
                } catch (DataIntegrityViolationException e) {
                        e.printStackTrace(); // log chi tiết
                        throw e;
                }

                // Thêm question vào exam
                List<QuestionsBank> questions = questionRepository.findAllById(request.getQuestionId());
                final Exam finalExam = exam; // Tạo final reference
                List<ExamQuestion> examQuestions = questions.stream()
                                .map(q -> new ExamQuestion(finalExam, q))
                                .toList();

                examQuestionRepository.saveAll(examQuestions);
                exam.setExamQuestions(examQuestions);
                
                // Cache exam với questions
                String examWithQuestionsKey = "exam:" + exam.getExamId() + ":questions";
                redisTemplate.opsForValue().set(examWithQuestionsKey, exam);
                
                // Convert Exam entity to ExamResponse
                return ExamResponse.builder()
                        .examId(exam.getExamId())
                        .examName(exam.getExamName())
                        .subjectName(exam.getSubjectName())
                        .durationMinutes(exam.getDurationMinutes())
                        .totalScore(exam.getTotalScore())
                        .startTime(exam.getStartTime() != null ? exam.getStartTime().toString() : null)
                        .endTime(exam.getEndTime() != null ? exam.getEndTime().toString() : null)
                        .shuffleQuestions(exam.getShuffleQuestions())
                        .shuffleAnswers(exam.getShuffleAnswers())
                        .maxAttempts(exam.getMaxAttempts())
                        .status(exam.getStatus())
                        .createdAt(exam.getCreatedAt() != null ? exam.getCreatedAt().toString() : null)
                        .updatedAt(exam.getUpdatedAt() != null ? exam.getUpdatedAt().toString() : null)
                        .className(exam.getClassEntity() != null ? exam.getClassEntity().getClassName() : null)
                        .teacherName(exam.getTeacher() != null ? exam.getTeacher().getFullName() : null)
                        .questionIds(request.getQuestionId())
                        .build();
        }

        @CacheEvict(value = "exams", key = "#examId")
        public void deleteExam(Integer examId){
                Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Không có lớp này"));

                // Xóa cache từ Redis
                String examKey = "exam:" + examId;
                String examWithQuestionsKey = "exam:" + examId + ":questions";
                redisTemplate.delete(examKey);
                redisTemplate.delete(examWithQuestionsKey);
                
                // Xóa cache exam list theo class
                String classExamsKey = "class:" + exam.getClassEntity().getClassId() + ":exams";
                redisTemplate.delete(classExamsKey);

                examRepository.delete(exam);
        }
        
        @Cacheable(value = "exams", key = "#examId")
        public Optional<Exam> getExamById(Integer examId) {
                // Kiểm tra cache trước
                String examKey = "exam:" + examId;
                Exam cachedExam = (Exam) redisTemplate.opsForValue().get(examKey);
                if (cachedExam != null) {
                        return Optional.of(cachedExam);
                }
                
                // Nếu không có trong cache, lấy từ database
                Optional<Exam> exam = examRepository.findById(examId);
                if (exam.isPresent()) {
                        // Cache lại
                        redisTemplate.opsForValue().set(examKey, exam.get());
                }
                return exam;
        }
        
        @Cacheable(value = "exams", key = "'class:' + #classId")
        public List<Exam> getExamsByClassId(Integer classId) {
                String classExamsKey = "class:" + classId + ":exams";
                
                // Kiểm tra cache trước
                @SuppressWarnings("unchecked")
                List<Exam> cachedExams = (List<Exam>) redisTemplate.opsForValue().get(classExamsKey);
                if (cachedExams != null) {
                        return cachedExams;
                }
                
                // Nếu không có trong cache, lấy từ database
                List<Exam> exams = examRepository.findByClassEntityClassId(classId);
                
                // Cache lại
                redisTemplate.opsForValue().set(classExamsKey, exams);
                
                return exams;
        }
}
