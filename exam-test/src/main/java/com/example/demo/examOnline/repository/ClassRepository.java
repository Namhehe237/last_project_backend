package com.example.demo.examOnline.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.examOnline.domain.Classes;
import com.example.demo.examOnline.dto.response.ClassOptionResponse;
import com.example.demo.examOnline.dto.response.ClassResponseDTO;
import com.example.demo.examOnline.dto.response.UserResponseDTO;

public interface ClassRepository extends JpaRepository<Classes, Integer> {

        @Query("SELECT NEW com.example.demo.examOnline.dto.response.ClassResponseDTO(" +
                        "c.classId, c.className, c.classCode, c.description, c.teacher.fullName, c.teacher.email, c.createdAt, null, "
                        +
                        "CAST(COUNT(sc) AS integer)) " +
                        "FROM Classes c " +
                        "LEFT JOIN c.studentClasses sc " +
                        "WHERE c.classId = :classId " +
                        "GROUP BY c.classId, c.className, c.classCode, c.description, c.teacher.fullName, c.teacher.email, c.createdAt")
        ClassResponseDTO getClassInformationDetail(@Param("classId") Integer classId);

        @Query("SELECT NEW com.example.demo.examOnline.dto.response.ClassResponseDTO(" +
                        "c.classId, c.className, c.classCode, c.description, c.teacher.fullName, c.teacher.email, c.createdAt, null, null) FROM Classes c WHERE c.teacher.userId =:teacherId")
        Page<ClassResponseDTO> findClassOfTeacher(@Param("teacherId") Integer teacherId, Pageable pageable);

        @Query("SELECT NEW com.example.demo.examOnline.dto.response.UserResponseDTO(" +
                        "u.userId, u.email, u.fullName, u.phoneNumber) " +
                        "FROM StudentClass sc JOIN sc.student u " +
                        "WHERE sc.classEntity.classId = :classId")
        Page<UserResponseDTO> findStudentOfClass(@Param("classId") Integer classId, Pageable pageable);

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

        @Query("SELECT NEW com.example.demo.examOnline.dto.response.ClassResponseDTO(" +
                        "c.classId, c.className, c.classCode, c.description, " +
                        "c.teacher.fullName, c.teacher.email, c.createdAt) " +
                        "FROM Classes c")
        Page<ClassResponseDTO> getListClass(Pageable pageable);

        @Transactional
        @Modifying
        @Query("DELETE FROM Classes c WHERE c.classId IN :classIds")
        void deleteClass(@Param("classIds") List<Integer> classIds);

        // Tìm lớp học theo mã lớp
        Optional<Classes> findByClassCode(String classCode);

        Optional<Classes> findByClassName(String className);

        @Query("SELECT NEW com.example.demo.examOnline.dto.response.ClassOptionResponse(c.classId, c.className) "
                        + "FROM Classes c WHERE c.teacher.userId = :teacherId ORDER BY c.className ASC")
        List<ClassOptionResponse> findClassOptionsByTeacherId(@Param("teacherId") Integer teacherId);
}
