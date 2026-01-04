package com.example.demo.examOnline.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.demo.examOnline.dto.request.CreateUserRequest;
import com.example.demo.examOnline.dto.request.DeleteUserRequest;
import com.example.demo.examOnline.dto.request.GetUserListRequest;
import com.example.demo.examOnline.dto.response.ClassResponseDTO;
import com.example.demo.examOnline.dto.response.UserResponseDTO;

public interface AdminService {

    public Page<UserResponseDTO> getUserList(GetUserListRequest request, Pageable pageable);

    public void deleteUser(DeleteUserRequest request);

    public Page<ClassResponseDTO> getClassList(Boolean includeArchived, Pageable pageable);

    public void addUser(CreateUserRequest request);

}
