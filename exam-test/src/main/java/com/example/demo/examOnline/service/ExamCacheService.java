package com.example.demo.examOnline.service;

import com.example.demo.examOnline.dto.cache.ExamSnapshot;

public interface ExamCacheService {

    public ExamSnapshot cacheExam(Integer examId, ExamSnapshot snapshot);

    public ExamSnapshot getExamFromCache(Integer examId);

    public ExamSnapshot getExam(Integer examId);

    public void evictExam(Integer examId);
}
