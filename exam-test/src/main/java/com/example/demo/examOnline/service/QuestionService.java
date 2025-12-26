package com.example.demo.examOnline.service;

import java.io.IOException;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.examOnline.domain.enums.DifficultyLevel;
import com.example.demo.examOnline.dto.request.AddQuestionRequest;
import com.example.demo.examOnline.dto.response.QuestionResponse;

public interface QuestionService {

    public void addQuestion(AddQuestionRequest request);

    public Page<QuestionResponse> getQuestionsByFilters(DifficultyLevel level,
            String subject,
            String teacherName,
            Pageable pageable);

    @Transactional
    public void updateQuestionWithFetch(Integer questionId, AddQuestionRequest request);

    @Transactional
    public void deleteQuestions(List<Integer> questionIds);

    public void parseExcelFile(MultipartFile file) throws IOException;

    public byte[] generateQuestionTemplate() throws IOException;

}