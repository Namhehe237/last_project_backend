package com.example.demo.examOnline.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
        Page<Object[]> rows = questionRepository.findQuestionsByFilters(level, subject, teacherName, pageable);

        Map<Integer, QuestionResponse> map = new LinkedHashMap<>();

        for (Object[] row : rows) {
            Integer qId = (Integer) row[0];
            map.putIfAbsent(qId, new QuestionResponse(
                    (String) row[1], // questionText
                    (QuestionType) row[2], // questionType
                    (DifficultyLevel) row[3], // difficultyLevel
                    (String) row[4], // subjectName
                    (String) row[5], // teacherName
                    new ArrayList<>() // answers
            ));

            String answerText = (String) row[6];
            Boolean isCorrect = (Boolean) row[7];
            if (answerText != null) {
                map.get(qId).getAnswers().add(new AnswerResponse(answerText, isCorrect));
            }
        }

        return new PageImpl<>(new ArrayList<>(map.values()), pageable, rows.getTotalElements());
    }

}