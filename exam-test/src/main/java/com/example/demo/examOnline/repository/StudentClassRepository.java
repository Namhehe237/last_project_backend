package com.example.demo.examOnline.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.examOnline.domain.StudentClass;

public interface StudentClassRepository extends JpaRepository<StudentClass, Integer> {
    
}
