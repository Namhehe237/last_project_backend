package com.example.demo.examOnline.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.example.demo.examOnline.service.UserService;
@RestController
@RequestMapping("/api/redis")
@RequiredArgsConstructor
@Slf4j
public class RedisTestController {

    private final RedisTemplate<String, Object> redisTemplate;
    private final UserService userService;
    /**
     * Kiểm tra kết nối Redis
     * GET /api/redis/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> checkRedisHealth() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Test 1: Ping Redis
            String pingResult = redisTemplate.getConnectionFactory()
                    .getConnection()
                    .ping();
            
            response.put("status", "OK");
            response.put("ping", pingResult);
            response.put("message", "Redis đang hoạt động bình thường");
            response.put("timestamp", LocalDateTime.now());
            
            // Test 2: Set và Get value
            String testKey = "redis:test:health";
            String testValue = "Redis is working! " + System.currentTimeMillis();
            
            redisTemplate.opsForValue().set(testKey, testValue);
            Object retrievedValue = redisTemplate.opsForValue().get(testKey);
            
            response.put("test_set", testValue);
            response.put("test_get", retrievedValue);
            response.put("test_success", testValue.equals(retrievedValue));
            
            // Test 3: Get Redis info
            try {
                var info = redisTemplate.getConnectionFactory()
                        .getConnection()
                        .serverCommands()
                        .info("server");
                response.put("redis_info_available", true);
                response.put("redis_info_size", info != null ? info.size() : 0);
            } catch (Exception e) {
                response.put("redis_info_available", false);
                response.put("redis_info_error", e.getMessage());
            }
            
            log.info("Redis health check passed");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", "Redis không hoạt động: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            response.put("timestamp", LocalDateTime.now());
            
            log.error("Redis health check failed", e);
            return ResponseEntity.status(503).body(response);
        }
    }

    /**
     * Test set/get value trong Redis (không cần truyền tham số)
     * GET /api/redis/test
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testRedis() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Tự động tạo key và value
            String key = "redis:test:" + System.currentTimeMillis();
            String value = "Test value created at " + LocalDateTime.now();
            
            // Set value
            redisTemplate.opsForValue().set(key, value);
            response.put("set_key", key);
            response.put("set_value", value);
            
            // Get value
            Object retrieved = redisTemplate.opsForValue().get(key);
            response.put("get_value", retrieved);
            response.put("match", value.equals(retrieved));
            
            // Get TTL (nếu có)
            Long ttl = redisTemplate.getExpire(key);
            response.put("ttl", ttl != null ? ttl + " seconds" : "no expiration");
            
            response.put("status", "SUCCESS");
            response.put("message", "Test thành công - không cần truyền tham số");
            response.put("timestamp", LocalDateTime.now());
            
            log.info("Redis test completed successfully with key: {}", key);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            log.error("Redis test failed", e);
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Xem thông tin cấu hình Redis
     * GET /api/redis/info
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getRedisInfo() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            var connectionFactory = redisTemplate.getConnectionFactory();
            var connection = connectionFactory.getConnection();
            
            response.put("connection_factory", connectionFactory.getClass().getSimpleName());
            response.put("timestamp", LocalDateTime.now());
            
            // Test connection bằng cách ping
            try {
                String pingResult = connection.ping();
                response.put("connected", true);
                response.put("ping", pingResult);
            } catch (Exception e) {
                response.put("connected", false);
                response.put("ping_error", e.getMessage());
            }
            
            // Thử lấy một số thông tin từ Redis
            try {
                var serverInfo = connection.serverCommands().info("server");
                response.put("server_info_available", true);
                response.put("server_info_keys", serverInfo != null ? serverInfo.keySet().size() : 0);
                if (serverInfo != null && !serverInfo.isEmpty()) {
                    response.put("redis_version", serverInfo.get("redis_version"));
                }
            } catch (Exception e) {
                response.put("server_info_available", false);
                response.put("server_info_error", e.getMessage());
            }
            
            response.put("status", "OK");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Thêm dữ liệu mẫu vào Redis
     * POST /api/redis/add-sample-data
     */
    @Cacheable(value = "sampleData")      
    @PostMapping("/add-sample-data")
    public ResponseEntity<String> addSampleData() {
      userService.getUserInformationDetail(1);
      return ResponseEntity.ok("userService");
    }
    /**
     * Lấy value của một key trong Redis
     * GET /api/redis/list-keys?key=sample:string:user:1
     */
    @GetMapping("/list-keys")
    public ResponseEntity<Map<String, Object>> listKeys(
            @org.springframework.web.bind.annotation.RequestParam String key) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Thử lấy string value
            Object value = redisTemplate.opsForValue().get(key);
            
            if (value != null) {
                response.put("key", key);
                response.put("type", "string");
                response.put("value", value);
            } else {
                // Thử lấy hash
                Map<Object, Object> hashValue = redisTemplate.opsForHash().entries(key);
                if (!hashValue.isEmpty()) {
                    response.put("key", key);
                    response.put("type", "hash");
                    response.put("value", hashValue);
                } else {
                    // Thử lấy list
                    List<Object> listValue = redisTemplate.opsForList().range(key, 0, -1);
                    if (listValue != null && !listValue.isEmpty()) {
                        response.put("key", key);
                        response.put("type", "list");
                        response.put("value", listValue);
                        response.put("count", listValue.size());
                    } else {
                        // Thử lấy set
                        Set<Object> setValue = redisTemplate.opsForSet().members(key);
                        if (setValue != null && !setValue.isEmpty()) {
                            response.put("key", key);
                            response.put("type", "set");
                            response.put("value", setValue);
                            response.put("count", setValue.size());
                        } else {
                            response.put("key", key);
                            response.put("type", "unknown");
                            response.put("value", null);
                            response.put("message", "Key không tồn tại hoặc rỗng");
                        }
                    }
                }
            }
            
            // Lấy TTL
            Long ttl = redisTemplate.getExpire(key);
            response.put("ttl", ttl != null && ttl > 0 ? ttl + " seconds" : "no expiration");
            response.put("status", "SUCCESS");
            response.put("timestamp", LocalDateTime.now());
            
            log.info("Retrieved value for key: {}", key);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("key", key);
            response.put("message", e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            log.error("Failed to get value for key: {}", key, e);
            return ResponseEntity.status(500).body(response);
        }
    }
}

