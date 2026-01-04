package com.example.demo.examOnline.service;

import com.example.demo.examOnline.domain.Classes;
import com.example.demo.examOnline.domain.ClassRequest;
import com.example.demo.examOnline.domain.StudentClass;
import com.example.demo.examOnline.domain.User;
import com.example.demo.examOnline.dto.request.RequestJoinClassRequest;
import com.example.demo.examOnline.dto.response.ClassResponseDTO;
import com.example.demo.examOnline.dto.response.MessageResponse;
import com.example.demo.examOnline.dto.response.AssignmentResponseDTO;
import com.example.demo.examOnline.dto.response.AnnouncementResponseDTO;
import com.example.demo.examOnline.repository.ClassRepository;
import com.example.demo.examOnline.repository.ClassRequestRepository;
import com.example.demo.examOnline.repository.StudentClassRepository;
import com.example.demo.examOnline.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public interface ClassManagementService {

    public MessageResponse requestJoinClass(RequestJoinClassRequest request);

    public List<ClassResponseDTO> getStudentClasses(Integer studentId, Boolean includeArchived);

    public MessageResponse leaveClass(Integer studentId, Integer classId);

    public ClassResponseDTO getClassDetails(Integer studentId, Integer classId);

    public List<AssignmentResponseDTO> getClassAssignments(Integer studentId, Integer classId);

    public List<AnnouncementResponseDTO> getClassAnnouncements(Integer studentId, Integer classId);
}