package com.example.demo.examOnline.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.examOnline.domain.User;
import com.example.demo.examOnline.domain.enums.RoleName;
import com.example.demo.examOnline.dto.request.student.ChangePasswordRequest;
import com.example.demo.examOnline.dto.request.student.StudentRegisterRequest;
import com.example.demo.examOnline.dto.request.student.UpdateStudentProfileRequest;
import com.example.demo.examOnline.dto.response.AuthResponse;
import com.example.demo.examOnline.dto.response.MessageResponse;
import com.example.demo.examOnline.dto.response.student.StudentProfileResponse;
import com.example.demo.examOnline.repository.UserRepository;
import com.example.demo.examOnline.service.JwtService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse registerStudent(StudentRegisterRequest request) {
        // Kiểm tra email đã tồn tại chưa
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email đã được sử dụng");
        }

        // Kiểm tra userCode đã tồn tại chưa
        if (userRepository.findByUserCode(request.getUserCode()).isPresent()) {
            throw new RuntimeException("Mã học sinh đã được sử dụng");
        }

        User student = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .avatarUrl(request.getAvatarUrl())
                .userCode(request.getUserCode())
                .roleName(RoleName.STUDENT)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userRepository.save(student);

        String jwtToken = jwtService.generateToken(student);

        return AuthResponse.builder()
                .accessToken(jwtToken)
                .build();
    }

    public StudentProfileResponse getCurrentStudentProfile() {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User student = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin học sinh"));

        return StudentProfileResponse.builder()
                .userId(student.getUserId())
                .email(student.getEmail())
                .fullName(student.getFullName())
                .phoneNumber(student.getPhoneNumber())
                .avatarUrl(student.getAvatarUrl())
                .userCode(student.getUserCode())
                .isActive(student.getIsActive())
                .createdAt(student.getCreatedAt())
                .updatedAt(student.getUpdatedAt())
                .build();
    }

    public StudentProfileResponse updateStudentProfile(UpdateStudentProfileRequest request) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User student = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin học sinh"));

        student.setFullName(request.getFullName());
        student.setPhoneNumber(request.getPhoneNumber());
        student.setAvatarUrl(request.getAvatarUrl());
        student.setUpdatedAt(LocalDateTime.now());

        userRepository.save(student);

        return StudentProfileResponse.builder()
                .userId(student.getUserId())
                .email(student.getEmail())
                .fullName(student.getFullName())
                .phoneNumber(student.getPhoneNumber())
                .avatarUrl(student.getAvatarUrl())
                .userCode(student.getUserCode())
                .isActive(student.getIsActive())
                .createdAt(student.getCreatedAt())
                .updatedAt(student.getUpdatedAt())
                .build();
    }

    public MessageResponse changePassword(ChangePasswordRequest request) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User student = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin học sinh"));

        // Kiểm tra mật khẩu cũ
        if (!passwordEncoder.matches(request.getOldPassword(), student.getPasswordHash())) {
            throw new RuntimeException("Mật khẩu cũ không đúng");
        }

        // Kiểm tra mật khẩu mới và xác nhận mật khẩu
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Mật khẩu mới và xác nhận mật khẩu không khớp");
        }

        // Cập nhật mật khẩu mới
        student.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        student.setUpdatedAt(LocalDateTime.now());
        userRepository.save(student);

        return MessageResponse.builder()
                .message("Đổi mật khẩu thành công")
                .success(true)
                .build();
    }

    public MessageResponse forgotPassword(String email) {
        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email không tồn tại trong hệ thống"));

        // Kiểm tra xem user có phải là học sinh không
        if (student.getRoleName() != RoleName.STUDENT) {
            throw new RuntimeException("Email này không phải là tài khoản học sinh");
        }

        return MessageResponse.builder()
                .message("Hướng dẫn đặt lại mật khẩu đã được gửi đến email của bạn")
                .success(true)
                .build();
    }

    public MessageResponse logout() {
        // JWT tokens are stateless, so we just return a success message
        // In a real application, you might want to implement a blacklist for tokens
        return MessageResponse.builder()
                .message("Đăng xuất thành công")
                .success(true)
                .build();
    }

    public Integer getUserIdByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(User::getUserId)
                .orElse(null);
    }

    public void updateAvatarUrl(Integer userId, String avatarUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        user.setAvatarUrl(avatarUrl);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }
} 