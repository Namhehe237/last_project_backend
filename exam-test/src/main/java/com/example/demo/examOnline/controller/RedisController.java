package com.example.demo.examOnline.controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Duration;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.examOnline.service.ClassService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/redis")
@RequiredArgsConstructor
public class RedisController {
    private final ClassService classService;

    @PostMapping("add")
    public ResponseEntity<String> add() {
        classService.getClassInformationDetail(1);
        return ResponseEntity.ok("ok");
    }

    @Autowired
    private StringRedisTemplate redisTemplate;

    @GetMapping("/redis/test")
    public ResponseEntity<?> testRedis() {
        try {

            // Test connection
            RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
            String pong = connection.ping();
            System.out.println("PING response: {}" + pong);
            connection.close();

            // Test set/get
            redisTemplate.opsForValue().set("test_key", "hello", Duration.ofSeconds(5));
            String value = redisTemplate.opsForValue().get("test_key");

            return ResponseEntity.ok(Map.of(
                    "connected", true,
                    "ping", pong,
                    "value", value));
        } catch (Exception e) {
            System.out.println("Redis connection failed" + e);

            // In ra full stack trace
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);

            return ResponseEntity.status(500).body(Map.of(
                    "connected", false,
                    "error", e.getMessage(),
                    "errorType", e.getClass().getName(),
                    "cause", e.getCause() != null ? e.getCause().getMessage() : "null",
                    "stackTrace", sw.toString()));
        }
    }

}
