package com.example.demo.examOnline.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.demo.examOnline.dto.cache.ExamSnapshot;
import com.example.demo.examOnline.dto.request.CreateExamRequest;
import com.example.demo.examOnline.dto.request.CreateRandomExamRequest;
import com.example.demo.examOnline.dto.request.ExamFilterRequest;
import com.example.demo.examOnline.dto.response.ExamResponse;
import com.example.demo.examOnline.dto.response.ExamPaperResponse;
import com.example.demo.examOnline.dto.request.SubmitExamRequest;
import com.example.demo.examOnline.dto.request.ForceSubmitExamRequest;
import com.example.demo.examOnline.dto.response.GradeExamResponse;
import com.example.demo.examOnline.dto.response.TestHistoryResponse;
import com.example.demo.examOnline.dto.response.ExamResultDetailResponse;
import com.example.demo.examOnline.dto.response.RandomExamResponse;
import com.example.demo.examOnline.dto.request.UpdateExamQuestionsRequest;

public interface ExamService {

        public void createExam(CreateExamRequest request);

        public RandomExamResponse createRandomExam(CreateRandomExamRequest request);

        public void updateExamQuestions(Integer examId, UpdateExamQuestionsRequest request);

        public void deleteExam(Integer examId);

        public ExamResponse getExamDetail(Integer examId);

        public Page<ExamResponse> getListExam(ExamFilterRequest request, Pageable pageable);

        public ExamSnapshot getOrBuildExamSnapshot(Integer examId);

        public ExamPaperResponse getExamPaper(Integer examId);

        public GradeExamResponse gradeExam(SubmitExamRequest request);

        public GradeExamResponse forceSubmitExam(ForceSubmitExamRequest request);

        public void saveVideoUrl(Integer examId, Integer studentId, String videoUrl);

        public List<TestHistoryResponse> getTestHistory(Integer studentId);

        public ExamResultDetailResponse getExamResultDetail(Integer examId, Integer studentId);
}