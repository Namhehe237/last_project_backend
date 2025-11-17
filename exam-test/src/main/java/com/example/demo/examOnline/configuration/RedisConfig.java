package com.example.demo.examOnline.configuration;

import java.time.Duration;

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

        @Value("${spring.data.redis.password}")
        private String password;

        @Value("${spring.data.redis.username}")
        private String username;

        @Bean
        public LettuceConnectionFactory redisConnectionFactory() {
                System.out.println("=== Redis Configuration ===");
                System.out.println("Host: " + host);
                System.out.println("Port: " + port);

                // Tạo RedisURI thủ công
                RedisURI redisUri = RedisURI.builder()
                                .withHost(host)
                                .withPort(port)
                                .withAuthentication(username, password.toCharArray())
                                .withSsl(true)
                                .withVerifyPeer(false) // Quan trọng!
                                .withStartTls(false) // Thử cả true và false
                                .withTimeout(Duration.ofSeconds(30))
                                .build();

                System.out.println("RedisURI: " + redisUri.toString());

                // Tạo Redis Client với URI
                ClientOptions clientOptions = ClientOptions.builder()
                                .autoReconnect(true)
                                .disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS)
                                .build();

                LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                                .clientOptions(clientOptions)
                                .commandTimeout(Duration.ofSeconds(30))
                                .build();

                RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
                config.setHostName(host);
                config.setPort(port);
                config.setUsername(username);
                config.setPassword(password);

                LettuceConnectionFactory factory = new LettuceConnectionFactory(config, clientConfig);
                factory.afterPropertiesSet();

                return factory;
        }

        @Bean
        public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory connectionFactory) {
                RedisTemplate<String, Object> template = new RedisTemplate<>();
                template.setConnectionFactory(connectionFactory);

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
                objectMapper.activateDefaultTyping(
                                LaissezFaireSubTypeValidator.instance,
                                ObjectMapper.DefaultTyping.NON_FINAL,
                                JsonTypeInfo.As.PROPERTY);

                GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer(
                                objectMapper);

                StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

                template.setKeySerializer(stringRedisSerializer);
                template.setValueSerializer(jackson2JsonRedisSerializer);

                template.setHashKeySerializer(stringRedisSerializer);
                template.setHashValueSerializer(jackson2JsonRedisSerializer);

                template.afterPropertiesSet();
                return template;
        }
}