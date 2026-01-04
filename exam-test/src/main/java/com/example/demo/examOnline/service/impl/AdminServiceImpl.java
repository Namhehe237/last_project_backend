package com.example.demo.examOnline.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.examOnline.domain.User;
import com.example.demo.examOnline.dto.request.CreateUserRequest;
import com.example.demo.examOnline.dto.request.DeleteUserRequest;
import com.example.demo.examOnline.dto.request.GetUserListRequest;
import com.example.demo.examOnline.dto.response.ClassResponseDTO;
import com.example.demo.examOnline.dto.response.UserResponseDTO;
import com.example.demo.examOnline.repository.ClassRepository;
import com.example.demo.examOnline.repository.UserRepository;
import com.example.demo.examOnline.service.AdminService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    private final ClassRepository classRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Page<UserResponseDTO> getUserList(GetUserListRequest request, Pageable pageable) {
        return userRepository.findUserListWithRole(request.getRoleName(), pageable);
    }

    @Override
    public void deleteUser(DeleteUserRequest request) {
        userRepository.deleteUser(request.getListUserId());
    }

    @Override
    public Page<ClassResponseDTO> getClassList(Boolean includeArchived, Pageable pageable) {
        Boolean includeArchivedValue = (includeArchived != null) ? includeArchived : false;
        return classRepository.getListClass(includeArchivedValue, pageable);
    }

    @Override
    public void addUser(CreateUserRequest request) {
        if (userRepository.checkMail(request.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại trong hệ thống!");
        }

        UUID generatedUuid = UUID.randomUUID();
        String uuidStr = generatedUuid.toString().replace("-", "").substring(0, 8);
        uuidStr = "USER" + uuidStr;

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .userCode(uuidStr)
                .roleName(request.getRoleName())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userRepository.save(user);
    }

}
