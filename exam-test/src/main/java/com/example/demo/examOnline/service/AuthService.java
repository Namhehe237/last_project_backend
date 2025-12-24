package com.example.demo.examOnline.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.examOnline.domain.User;
import com.example.demo.examOnline.dto.request.AuthRequest;
import com.example.demo.examOnline.dto.response.AuthResponse;

import com.example.demo.examOnline.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public interface AuthService {

        public AuthResponse authenticate(AuthRequest request);

        public AuthResponse register(AuthRequest request);
}