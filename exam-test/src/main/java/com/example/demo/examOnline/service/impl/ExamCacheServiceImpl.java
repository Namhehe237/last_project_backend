package com.example.demo.examOnline.service.impl;

import java.time.Duration;

import org.springframework.cache.*;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.demo.examOnline.dto.cache.ExamSnapshot;
import com.example.demo.examOnline.service.ExamCacheService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExamCacheServiceImpl implements ExamCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String KEY_PREFIX = "exam:snapshot:";
    private static final Duration DEFAULT_TTL = Duration.ofHours(6);
    private final CacheManager cacheManager;

    @Override
    @Cacheable(value = "exam", key = "'exam_detail_' + #examId")
    public ExamSnapshot cacheExam(Integer examId, ExamSnapshot snapshot) {
        return snapshot;
    }

    @Override
    public ExamSnapshot getExamFromCache(Integer examId) {
        Cache cache = cacheManager.getCache("exam");
        if (cache != null) {
            return cache.get("exam_detail_" + examId, ExamSnapshot.class);
        }
        return null;
    }

    @Override
    public ExamSnapshot getExam(Integer examId) {
        Object value = redisTemplate.opsForValue().get(KEY_PREFIX + examId);
        return (value instanceof ExamSnapshot) ? (ExamSnapshot) value : null;
    }

    @Override
    public void evictExam(Integer examId) {
        redisTemplate.delete(KEY_PREFIX + examId);
    }
}
