package com.example.demo.examOnline.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.examOnline.dto.response.ClassResponseDTO;
import com.example.demo.examOnline.repository.ClassRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClassService {

    private final ClassRepository classRepository;

    public List<ClassResponseDTO> getListClassOfTeacher(Integer teacherId){
        
        List<ClassResponseDTO> listClass = classRepository.findClassByTeacherId(teacherId);

        if (listClass.isEmpty()){
            throw new RuntimeException("Giáo viên chưa nhận lớp nào");
        }

        return listClass;

    }

}
