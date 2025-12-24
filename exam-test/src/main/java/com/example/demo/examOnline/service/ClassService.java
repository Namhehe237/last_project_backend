package com.example.demo.examOnline.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.demo.examOnline.dto.request.CreateClassRequest;
import com.example.demo.examOnline.dto.request.DeleteClassRequest;
import com.example.demo.examOnline.dto.request.DeleteUserRequest;
import com.example.demo.examOnline.dto.request.HandleJoinRequestRequest;
import com.example.demo.examOnline.dto.request.UpdateClassInformationRequest;
import com.example.demo.examOnline.dto.response.ClassOptionResponse;
import com.example.demo.examOnline.dto.response.ClassResponseDTO;
import com.example.demo.examOnline.dto.response.CreateClassResponse;
import com.example.demo.examOnline.dto.response.RequestJoinClassResponse;
import com.example.demo.examOnline.dto.response.UserResponseDTO;

public interface ClassService {

    public ClassResponseDTO getClassInformationDetail(Integer classId);

    public void updateClassInfomationDetail(Integer classId, UpdateClassInformationRequest request);

    public Page<ClassResponseDTO> getClassOfTeacher(Integer teacherId, Pageable pageable);

    public List<ClassOptionResponse> getClassOptions(Integer teacherId);

    public Page<UserResponseDTO> getStudentOfClass(Integer classId, Pageable pageable);

    public void handleRequestJoinClass(HandleJoinRequestRequest request);

    public CreateClassResponse createClass(CreateClassRequest request);

    public void deleteClass(DeleteClassRequest request);

    public void deleteStudentFromClass(Integer classId, DeleteUserRequest request);

    public Page<RequestJoinClassResponse> getRequestOfClass(Integer classId, Pageable pageable);
}
