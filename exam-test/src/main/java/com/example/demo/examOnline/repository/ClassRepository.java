package com.example.demo.examOnline.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.examOnline.domain.Classes;
import com.example.demo.examOnline.dto.response.ClassResponseDTO;
import com.example.demo.examOnline.dto.response.UserResponseDTO;

public interface ClassRepository extends JpaRepository<Classes, Integer> {

        @Query("SELECT NEW com.example.demo.examOnline.dto.response.ClassResponseDTO(" +
                        "c.classId, c.className, c.classCode, c.description, c.teacher.fullName, c.teacher.phoneNumber) FROM Classes c WHERE c.classId = :classId ")
        ClassResponseDTO getClassInformationDetail(@Param("classId") Integer classId);

        @Query("SELECT NEW com.example.demo.examOnline.dto.response.ClassResponseDTO(" +
                        "c.classId, c.className, c.classCode) FROM Classes c WHERE c.teacher.userId =:teacherId")
        List<ClassResponseDTO> findClassOfTeacher(@Param("teacherId") Integer teacherId);

        @Query("SELECT NEW com.example.demo.examOnline.dto.response.UserResponseDTO(" +
                        "u.userId, u.email,u.fullName, u.phoneNumber) FROM StudentClass sc JOIN sc.student u WHERE sc.classEntity.classId = :classId")
        List<UserResponseDTO> findStudentOfClass(@Param("classId") Integer classId);

        @Transactional
        @Modifying
        @Query(value = "DELETE FROM STUDENT_CLASSES WHERE class_id = :classId and student_id IN (:studentIds)", nativeQuery = true)
        void deleteStudentFromClass(@Param("classId") Integer classId, @Param("studentIds") List<Integer> studentIds);

        @Query("""
                SELECT COUNT(c) > 0
                FROM Classes c
                WHERE (:classCode IS NOT NULL AND c.classCode = :classCode)
                AND c.classId <> :classId
                        """)
        Boolean checkClassCodeIsExist(@Param("classCode") String classCode,
                        @Param("classId") Integer classId);

}
