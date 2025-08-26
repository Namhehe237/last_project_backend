package com.example.demo.examOnline.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.examOnline.dto.request.DeleteUserRequest;
import com.example.demo.examOnline.dto.request.GetUserListRequest;
import com.example.demo.examOnline.dto.response.ClassResponseDTO;
import com.example.demo.examOnline.dto.response.UserResponseDTO;
import com.example.demo.examOnline.repository.ClassRepository;
import com.example.demo.examOnline.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final ClassRepository classRepository;

    public Page<UserResponseDTO> getUserList(GetUserListRequest request, Pageable pageable) {
        return userRepository.findUserListWithRole(request.getRoleName(), pageable);
    }

    public void deleteUser(DeleteUserRequest request){
        userRepository.deleteUser(request.getListUserId());
    }

    public Page<ClassResponseDTO> getClassList(Pageable pageable){
        return classRepository.getListClass(pageable);
    }
}
