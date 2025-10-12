package com.example.demo.examOnline.service;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.demo.examOnline.dto.cache.ExamSnapshot;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExamCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String KEY_PREFIX = "exam:snapshot:";
    private static final Duration DEFAULT_TTL = Duration.ofHours(6);

    public void cacheExam(ExamSnapshot snapshot) {
        if (snapshot == null || snapshot.getExamId() == null) return;
        String key = KEY_PREFIX + snapshot.getExamId();
        redisTemplate.opsForValue().set(key, snapshot, DEFAULT_TTL);
    }

    public ExamSnapshot getExam(Integer examId) {
        Object value = redisTemplate.opsForValue().get(KEY_PREFIX + examId);
        return (value instanceof ExamSnapshot) ? (ExamSnapshot) value : null;
    }

    public void evictExam(Integer examId) {
        redisTemplate.delete(KEY_PREFIX + examId);
    }
}


