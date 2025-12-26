package com.example.demo.examOnline.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.examOnline.domain.Answer;
import com.example.demo.examOnline.domain.QuestionsBank;
import com.example.demo.examOnline.domain.User;
import com.example.demo.examOnline.domain.enums.DifficultyLevel;
import com.example.demo.examOnline.domain.enums.QuestionType;
import com.example.demo.examOnline.dto.request.AddQuestionRequest;
import com.example.demo.examOnline.dto.response.AnswerResponse;
import com.example.demo.examOnline.dto.response.QuestionResponse;
import com.example.demo.examOnline.repository.QuestionRepository;
import com.example.demo.examOnline.repository.UserRepository;
import com.example.demo.examOnline.service.QuestionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    @Override
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

    @Override
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

    @Override
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

    @Override
    @Transactional
    public void deleteQuestions(List<Integer> questionIds) {
        questionRepository.deleteAllById(questionIds);
    }

    @Override
    public void parseExcelFile(MultipartFile file) throws IOException {
        List<QuestionsBank> questions = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null)
                    continue;

                QuestionsBank question = QuestionsBank.builder()
                        .questionText(getCellValue(row.getCell(0)))
                        .difficultyLevel(parseDifficultyLevel(getCellValue(row.getCell(1))))
                        .subjectName(getCellValue(row.getCell(2)))
                        .questionType(QuestionType.MULTIPLE_CHOICE)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

                List<Answer> answers = new ArrayList<>();
                for (int j = 3; j <= 6; j++) {
                    String answerText = getCellValue(row.getCell(j));
                    if (answerText == null || answerText.isEmpty())
                        continue;

                    boolean isCorrect = (j == 3);
                    Answer answer = Answer.builder()
                            .answerText(answerText)
                            .isCorrect(isCorrect)
                            .question(question)
                            .build();

                    answers.add(answer);
                }

                question.setAnswers(answers);
                questions.add(question);
            }
        }
        questionRepository.saveAll(questions);
    }

    @Override
    public byte[] generateQuestionTemplate() throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Questions");
            String[] headers = {
                    "Question Text",
                    "Difficulty (EASY/MEDIUM/HARD)",
                    "Subject Name",
                    "Answer 1 (Correct)",
                    "Answer 2",
                    "Answer 3",
                    "Answer 4"
            };
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            Row exampleRow = sheet.createRow(1);
            exampleRow.createCell(0).setCellValue("Ví dụ: Java là gì?");
            exampleRow.createCell(1).setCellValue("EASY");
            exampleRow.createCell(2).setCellValue("Java Programming");
            exampleRow.createCell(3).setCellValue("Ngôn ngữ lập trình hướng đối tượng");
            exampleRow.createCell(4).setCellValue("Một framework");
            exampleRow.createCell(5).setCellValue("Một CSDL");
            exampleRow.createCell(6).setCellValue("Một hệ điều hành");

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    private String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    private DifficultyLevel parseDifficultyLevel(String value) {
        if (value == null || value.isBlank()) {
            return DifficultyLevel.MEDIUM; // default
        }

        try {
            return DifficultyLevel.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            // Nếu Excel có giá trị sai như "easyyy" -> fallback
            return DifficultyLevel.MEDIUM;
        }
    }

}