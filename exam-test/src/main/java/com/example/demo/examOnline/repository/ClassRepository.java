package com.example.demo.examOnline.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.examOnline.domain.Class;
import com.example.demo.examOnline.dto.response.ClassResponseDTO;

public interface ClassRepository extends JpaRepository<Class, Integer> {
    
    @Query(
        "SELECT new com.example.demo.examOnline.dto.response.ClassResponseDTO("+
        "c.classId, c.className, c.classCode) "+ 
        "FROM Class c where c.teacher.userId = :teacherId "
    )
    List<ClassResponseDTO> findClassByTeacherId(@Param("teacherId") Integer teacherId);

}
