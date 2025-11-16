package com.example.demo.examOnline.repository;

import com.example.demo.examOnline.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    @Query("SELECT p FROM Post p WHERE p.classEntity.classId = :classId ORDER BY p.createdAt DESC")
    List<Post> findByClassIdOrderByCreatedAtDesc(@Param("classId") Integer classId);

    List<Post> findByClassEntityClassIdAndTeacherUserId(Integer classId, Integer teacherId);
}

