package com.example.demo.examOnline.configuration;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.RedisURI;
import io.lettuce.core.SocketOptions;
import io.lettuce.core.SslOptions;

@Configuration
public class RedisConfig {

        @Value("${spring.data.redis.host}")
        private String host;

        @Value("${spring.data.redis.port}")
        private int port;

        // @Value("${spring.data.redis.password}")
        // private String password;

        // @Value("${spring.data.redis.username}")
        // private String username;

        @Bean(name = "redisForgotPassword")
        public LettuceConnectionFactory redisForgotPassword() {
                RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(host, port);
                // configuration.setDatabase(REDIS_INDEX_FORGOT_PASSWORD);
                return new LettuceConnectionFactory(configuration);
        }

        // RedisTemplate cho DB 0
        @Bean(name = "redisTemplateForgotPassword")
        @Qualifier("redisTemplateForgotPassword")
        public RedisTemplate<String, Object> redisTemplateFirst(
                        @Qualifier("redisForgotPassword") LettuceConnectionFactory lettuceConnectionFactory) {
                RedisTemplate<String, Object> template = new RedisTemplate<>();
                template.setConnectionFactory(lettuceConnectionFactory);

                // Sử dụng serializer cho Redis
                GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer(
                                redisObjectMapper());

                template.setKeySerializer(new StringRedisSerializer());
                template.setValueSerializer(genericJackson2JsonRedisSerializer);
                template.setHashKeySerializer(new StringRedisSerializer());
                template.setHashValueSerializer(genericJackson2JsonRedisSerializer);

                template.setEnableTransactionSupport(true);
                template.afterPropertiesSet();
                return template;
        }

        // ObjectMapper cho Redis serialization/deserialization
        @Bean
        public ObjectMapper redisObjectMapper() {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());
                objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                return objectMapper;
        }
}