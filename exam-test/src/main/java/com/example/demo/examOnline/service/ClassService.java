package com.example.demo.examOnline.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.examOnline.domain.Class;
import com.example.demo.examOnline.domain.User;
import com.example.demo.examOnline.dto.request.DeleteStudentRequest;
import com.example.demo.examOnline.dto.response.ClassResponseDTO;
import com.example.demo.examOnline.repository.ClassRepository;
import com.example.demo.examOnline.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ClassService {

    private final ClassRepository classRepository;
    private final UserRepository userRepository;

    public List<ClassResponseDTO> getListClassOfTeacher(Integer teacherId) {

        List<ClassResponseDTO> listClass = classRepository.findClassByTeacherId(teacherId);

        return listClass;

    }


    public void deleteStudent(Integer classId, List<Integer> listStudentId) {
        if (listStudentId == null || listStudentId.isEmpty()) {
            throw new IllegalArgumentException("Danh sách studentId không được null hoặc rỗng");
        }

        // Kiểm tra class tồn tại (optional nhưng nên có)
        if (!classRepository.existsById(classId)) {
            throw new RuntimeException("Class not found with id: " + classId);
        }

        // Xóa relationship trực tiếp ở database
        classRepository.removeStudentsFromClass(classId, listStudentId);
    }

}
