package com.example.demo.examOnline.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.examOnline.domain.Answer;
import com.example.demo.examOnline.domain.QuestionsBank;
import com.example.demo.examOnline.domain.User;
import com.example.demo.examOnline.domain.enums.DifficultyLevel;
import com.example.demo.examOnline.domain.enums.QuestionType;
import com.example.demo.examOnline.dto.request.AddQuestionRequest;
import com.example.demo.examOnline.dto.response.AnswerResponse;
import com.example.demo.examOnline.dto.response.QuestionResponse;
import com.example.demo.examOnline.repository.AnswerRepository;
import com.example.demo.examOnline.repository.QuestionRepository;
import com.example.demo.examOnline.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final AnswerRepository answerRepository;

    public void addQuestion(AddQuestionRequest request) {

        Boolean checkQuestion = questionRepository.existsByQuestionText(request.getQuestionText());

        if (checkQuestion) {
            throw new IllegalArgumentException("Câu hỏi đã tồn tại trong ngân hàng câu hỏi!");
        }

        User teacher = userRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new RuntimeException("Không có giáo viên này "));

        QuestionsBank questionsBank = QuestionsBank.builder()
                .questionText(request.getQuestionText())
                .questionType(request.getQuestionType())
                .difficultyLevel(request.getDifficultyLevel())
                .subjectName(request.getSubjectName())
                .teacher(teacher)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        List<Answer> answers = new ArrayList<>();

        for (int i = 0; i < request.getAnswer().size(); i++) {
            boolean isCorrect = (i == 0);
            Answer answer = Answer.builder()
                    .question(questionsBank)
                    .answerText(request.getAnswer().get(i))
                    .isCorrect(isCorrect)
                    .build();
            answers.add(answer);
        }

        questionsBank.setAnswers(answers);

        questionRepository.save(questionsBank);

    }

    public Page<QuestionResponse> getQuestionsByFilters(DifficultyLevel level,
            String subject,
            String teacherName,
            Pageable pageable) {
        Page<QuestionsBank> questionsPage = questionRepository.findQuestionsByFiltersWithAnswers(
                level, subject, teacherName, pageable);

        List<QuestionResponse> responses = questionsPage.getContent().stream()
                .map(question -> {
                    List<AnswerResponse> answers = question.getAnswers().stream()
                            .map(answer -> new AnswerResponse(answer.getAnswerText(), answer.getIsCorrect()))
                            .collect(Collectors.toList());

                    return new QuestionResponse(
                            question.getQuestionId(),
                            question.getQuestionText(),
                            question.getQuestionType(),
                            question.getDifficultyLevel(),
                            question.getSubjectName(),
                            question.getTeacher() != null ? question.getTeacher().getFullName() : null,
                            answers);
                })
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, questionsPage.getTotalElements());
    }

    @Transactional
    public void updateQuestionWithFetch(Integer questionId, AddQuestionRequest request) {
        // First get question with answers loaded
        QuestionsBank questionsBank = questionRepository.findByIdWithAnswers(questionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy câu hỏi"));

        Boolean checkQuestion = questionRepository.existsByQuestionTextAndNotId(request.getQuestionText(), questionId);

        if (checkQuestion) {
            throw new IllegalArgumentException("Câu hỏi đã tồn tại trong ngân hàng câu hỏi!");
        }

        // Update basic fields
        if (request.getQuestionText() != null) {
            questionsBank.setQuestionText(request.getQuestionText());
        }
        if (request.getQuestionType() != null) {
            questionsBank.setQuestionType(request.getQuestionType());
        }
        if (request.getDifficultyLevel() != null) {
            questionsBank.setDifficultyLevel(request.getDifficultyLevel());
        }
        if (request.getSubjectName() != null) {
            questionsBank.setSubjectName(request.getSubjectName());
        }
        if (request.getTeacherId() != null) {
            User teacher = userRepository.findById(request.getTeacherId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy giáo viên"));
            questionsBank.setTeacher(teacher);
        }

        // Handle answers update
        if (request.getAnswer() != null && !request.getAnswer().isEmpty()) {
            // Clear existing answers (orphanRemoval will delete from DB)
            questionsBank.getAnswers().clear();

            // Create and add new answers
            for (int i = 0; i < request.getAnswer().size(); i++) {
                boolean isCorrect = (i == 0);
                Answer answer = Answer.builder()
                        .question(questionsBank)
                        .answerText(request.getAnswer().get(i))
                        .isCorrect(isCorrect)
                        .build();

                questionsBank.getAnswers().add(answer);
            }
        }

        questionRepository.save(questionsBank);
    }

    @Transactional
    public void deleteQuestions(List<Integer> questionIds) {
        questionRepository.deleteByIds(questionIds);
    }

}