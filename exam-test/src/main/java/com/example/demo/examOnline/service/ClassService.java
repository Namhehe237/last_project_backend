package com.example.demo.examOnline.service;

import java.time.LocalDateTime;
import java.util.List;

import javax.management.RuntimeErrorException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.examOnline.domain.ClassRequest;
import com.example.demo.examOnline.domain.Classes;
import com.example.demo.examOnline.domain.StudentClass;
import com.example.demo.examOnline.domain.StudentClassId;
import com.example.demo.examOnline.domain.User;
import com.example.demo.examOnline.dto.request.DeleteUserRequest;
import com.example.demo.examOnline.dto.request.JoinClassRequest;
import com.example.demo.examOnline.dto.request.HandleJoinRequestRequest;
import com.example.demo.examOnline.dto.request.UpdateClassInformationRequest;
import com.example.demo.examOnline.dto.response.ClassResponseDTO;
import com.example.demo.examOnline.dto.response.UserResponseDTO;
import com.example.demo.examOnline.repository.ClassRepository;
import com.example.demo.examOnline.repository.ClassRequestRepository;
import com.example.demo.examOnline.repository.StudentClassRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClassService {
    private final ClassRepository classRepository;
    private final ClassRequestRepository classRequestRepository;
    private final StudentClassRepository studentClassRepository;

    public ClassResponseDTO getClassInformationDetail(Integer classId) {
        return classRepository.getClassInformationDetail(classId);
    }

    public void updateClassInfomationDetail(Integer classId, UpdateClassInformationRequest request) {

        Boolean checkExist = classRepository.checkClassCodeIsExist(request.getClassCode(), classId);

        if (checkExist) {
            throw new DataIntegrityViolationException("Class code đã có");
        }

        Classes classes = classRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy class với id : " + classId));

        if (request.getClassCode() != null)
            classes.setClassCode(request.getClassCode());
        if (request.getClassName() != null)
            classes.setClassName(request.getClassName());
        if (request.getDescription() != null)
            classes.setDescription(request.getDescription());

        classRepository.save(classes);
    }

    public Page<ClassResponseDTO> getClassOfTeacher(Integer teacherId,  Pageable pageable) {

        Page<ClassResponseDTO> listClasses = classRepository.findClassOfTeacher(teacherId,pageable);

        return listClasses;
    }

    public Page<UserResponseDTO> getStudentOfClass(Integer classId, Pageable pageable) {
        return classRepository.findStudentOfClass(classId, pageable);
    }

    public void deleteStudentFromClass(Integer classId, DeleteUserRequest request) {
        if (request.getListUserId() == null || request.getListUserId().isEmpty()) {
            throw new IllegalArgumentException("Danh sách studentId không được null hoặc rỗng");
        }

        classRepository.deleteStudentFromClass(classId, request.getListUserId());
    }

    @Transactional
    public void handleRequestJoinClass(HandleJoinRequestRequest request) {
        try {
            List<ClassRequest> requests = classRequestRepository.findAllById(request.getClassRequestId());

            if (requests.isEmpty()) {
                throw new IllegalArgumentException("Không có request nào hợp lệ");
            }

            if (request.getStatus().equalsIgnoreCase("REJECTED")) {
                classRequestRepository.deleteAllInBatch(requests);
            } else {
                List<StudentClass> studentClasses = requests.stream()
                        .map(req -> {

                            StudentClassId id = StudentClassId.builder()
                                    .studentId(req.getStudent().getUserId())
                                    .classId(req.getClassEntity().getClassId())
                                    .build();

                            return StudentClass.builder()
                                    .id(id)
                                    .student(req.getStudent())
                                    .classEntity(req.getClassEntity())
                                    .joinedAt(LocalDateTime.now())
                                    .build();
                        })
                        .toList();

                studentClassRepository.saveAll(studentClasses);
                classRequestRepository.deleteAllInBatch(requests);
            }

        } catch (Exception e) {
            System.err.println("Error in handleRequestJoinClass: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

}
