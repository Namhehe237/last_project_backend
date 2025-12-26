package com.example.demo.examOnline.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.examOnline.domain.User;
import com.example.demo.examOnline.dto.request.AuthRequest;
import com.example.demo.examOnline.dto.response.AuthResponse;
import com.example.demo.examOnline.repository.UserRepository;
import com.example.demo.examOnline.service.AuthService;
import com.example.demo.examOnline.service.NotificationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final NotificationService notificationService;

    @Override
    public AuthResponse authenticate(AuthRequest request) {
        try {
            System.out.println("=== DEBUG AUTH SERVICE ===");
            System.out.println("Login attempt for email: " + request.getEmail());

            // Kiểm tra user có tồn tại không
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            System.out.println("User found: " + user.getEmail());
            System.out.println("User active: " + user.getIsActive());
            System.out.println("Password hash in DB: " + user.getPasswordHash());
            System.out.println("Raw password from request: " + request.getPassword());

            // Kiểm tra password match
            boolean passwordMatches = passwordEncoder.matches(request.getPassword(),
                    user.getPasswordHash());
            System.out.println("Password matches: " + passwordMatches);

            if (!passwordMatches) {
                throw new RuntimeException("Password does not match!");
            }

            // Thực hiện authentication
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()));
            System.out.println("Authentication successful!");

            // Generate JWT token
            String jwtToken = jwtService.generateToken(user);
            System.out.println("JWT token generated successfully");

            // Check assignment deadlines and notify if needed (async to not block login)
            try {
                notificationService.checkAndNotifyAssignmentDeadlines(user.getUserId());
            } catch (Exception e) {
                System.err.println("Error checking assignment deadlines on login: " + e.getMessage());
                // Don't fail login if deadline check fails
            }

            return AuthResponse.builder()
                    .accessToken(jwtToken)
                    .roleName(user.getRoleName())
                    .userId(user.getUserId())
                    .build();

        } catch (Exception e) {
            System.out.println("Auth error: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public AuthResponse register(AuthRequest request) {
        UUID generatedUuid = UUID.randomUUID();
        String uuidStr = generatedUuid.toString().replace("-", "").substring(0, 8);
        uuidStr = "USER" + uuidStr;

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .avatarUrl(request.getAvatarUrl())
                .userCode(uuidStr)
                .roleName(request.getRole())
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        String jwtToken = jwtService.generateToken(user);

        return AuthResponse.builder()
                .accessToken(jwtToken)
                .build();
    }
}
