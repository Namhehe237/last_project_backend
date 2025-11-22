package com.example.demo.examOnline.service;

import java.time.Duration;

import org.springframework.cache.*;
import org.springframework.cache.annotation.Cacheable;
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

    @Cacheable(value = "exam", key = "'exam_detail_' + #examId")
    public ExamSnapshot cacheExam(Integer examId, ExamSnapshot snapshot) {
        return snapshot;
    }

    private final CacheManager cacheManager;

    public ExamSnapshot getExamFromCache(Integer examId) {
        Cache cache = cacheManager.getCache("exam"); 
        if (cache != null) {
            return cache.get("exam_detail_" + examId, ExamSnapshot.class);
        }
        return null;
    }

    public ExamSnapshot getExam(Integer examId) {
        Object value = redisTemplate.opsForValue().get(KEY_PREFIX + examId);
        return (value instanceof ExamSnapshot) ? (ExamSnapshot) value : null;
    }

    public void evictExam(Integer examId) {
        redisTemplate.delete(KEY_PREFIX + examId);
    }
}
