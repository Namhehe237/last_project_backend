package com.example.demo.examOnline.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.examOnline.domain.ClassRequest;
import com.example.demo.examOnline.domain.Classes;
import com.example.demo.examOnline.dto.response.RequestJoinClassResponse;

public interface ClassRequestRepository extends JpaRepository<ClassRequest, Integer> {

    @Query("SELECT NEW com.example.demo.examOnline.dto.response.RequestJoinClassResponse(" +
            " cr.requestId, cr.student.fullName) " +
            "FROM ClassRequest cr " +
            "WHERE cr.classEntity.classId = :classId")
    Page<RequestJoinClassResponse> getRequestOfClass(@Param("classId") Integer classId, Pageable pageable);
}
