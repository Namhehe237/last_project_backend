package com.example.demo.examOnline.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.examOnline.domain.ClassRequest;

public interface ClassRequestRepository extends JpaRepository<ClassRequest , Integer>{
    
}
