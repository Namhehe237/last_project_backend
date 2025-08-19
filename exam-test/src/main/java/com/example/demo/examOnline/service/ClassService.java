package com.example.demo.examOnline.service;

import java.util.List;

import javax.management.RuntimeErrorException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.example.demo.examOnline.domain.Classes;
import com.example.demo.examOnline.dto.request.DeleteStudentRequest;
import com.example.demo.examOnline.dto.request.UpdateClassInformationRequest;
import com.example.demo.examOnline.dto.response.ClassResponseDTO;
import com.example.demo.examOnline.dto.response.UserResponseDTO;
import com.example.demo.examOnline.repository.ClassRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClassService {
    private final ClassRepository classRepository;

    public ClassResponseDTO getClassInformationDetail(Integer classId) {
        return classRepository.getClassInformationDetail(classId);
    }

    public void updateClassInfomationDetail(Integer classId, UpdateClassInformationRequest request){

        Boolean checkExist = classRepository.checkClassCodeIsExist(request.getClassCode(), classId);

        if (checkExist){
         throw  new DataIntegrityViolationException("Class code đã có");
        }

        Classes classes = classRepository.findById(classId)
        .orElseThrow(() -> new RuntimeException("Không tìm thấy class với id : "+ classId));


        if (request.getClassCode() != null) classes.setClassCode(request.getClassCode());
        if (request.getClassName() != null) classes.setClassName(request.getClassName());
        if (request.getDescription() != null) classes.setDescription(request.getDescription());

        classRepository.save(classes);
    }

    public List<ClassResponseDTO> getClassOfTeacher(Integer teacherId) {

        List<ClassResponseDTO> listClasses = classRepository.findClassOfTeacher(teacherId);

        return listClasses;
    }

    public List<UserResponseDTO> getStudentOfClass(Integer classId) {
        List<UserResponseDTO> listStudent = classRepository.findStudentOfClass(classId);

        return listStudent;
    }

    public void deleteStudentFromClass(Integer classId, DeleteStudentRequest request) {
        if (request.getListStudentId() == null || request.getListStudentId().isEmpty()) {
            throw new IllegalArgumentException("Danh sách studentId không được null hoặc rỗng");
        }

        classRepository.deleteStudentFromClass(classId, request.getListStudentId());
    }

}
