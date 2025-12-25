package com.example.demo.examOnline.service;

import com.example.demo.examOnline.dto.request.student.ChangePasswordRequest;
import com.example.demo.examOnline.dto.request.student.StudentRegisterRequest;
import com.example.demo.examOnline.dto.request.student.UpdateStudentProfileRequest;
import com.example.demo.examOnline.dto.response.AuthResponse;
import com.example.demo.examOnline.dto.response.MessageResponse;
import com.example.demo.examOnline.dto.response.student.StudentProfileResponse;

public interface StudentService {

    public AuthResponse registerStudent(StudentRegisterRequest request);

    public StudentProfileResponse getCurrentStudentProfile();

    public StudentProfileResponse updateStudentProfile(UpdateStudentProfileRequest request);

    public MessageResponse changePassword(ChangePasswordRequest request);

    public MessageResponse forgotPassword(String email);

    public MessageResponse logout();;

    public void updateAvatarUrl(Integer userId, String avatarUrl);

    public Integer getUserIdByEmail(String email);
}