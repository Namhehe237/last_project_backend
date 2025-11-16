package com.example.demo.examOnline.repository;

import com.example.demo.examOnline.domain.StudentClass;
import com.example.demo.examOnline.domain.StudentClassId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentClassRepository extends JpaRepository<StudentClass, StudentClassId> {
    
    // Tìm tất cả lớp học mà học sinh đã tham gia
    @Query("SELECT sc FROM StudentClass sc WHERE sc.student.userId = :studentId")
    List<StudentClass> findByStudentId(@Param("studentId") Integer studentId);
    
    // Kiểm tra học sinh đã tham gia lớp học chưa
    @Query("SELECT sc FROM StudentClass sc WHERE sc.student.userId = :studentId AND sc.classEntity.classId = :classId")
    Optional<StudentClass> findByStudentIdAndClassId(@Param("studentId") Integer studentId, @Param("classId") Integer classId);
    
    // Đếm số học sinh trong một lớp
    @Query("SELECT COUNT(sc) FROM StudentClass sc WHERE sc.classEntity.classId = :classId")
    Long countStudentsByClassId(@Param("classId") Integer classId);
    
    // Xóa học sinh khỏi lớp học
    @Modifying
    @Transactional
    @Query("DELETE FROM StudentClass sc WHERE sc.student.userId = :studentId AND sc.classEntity.classId = :classId")
    void deleteByStudentIdAndClassEntityClassId(@Param("studentId") Integer studentId, @Param("classId") Integer classId);
    
    // Lấy danh sách học sinh trong lớp
    @Query("SELECT sc FROM StudentClass sc WHERE sc.classEntity.classId = :classId")
    List<StudentClass> findByClassId(@Param("classId") Integer classId);
}
