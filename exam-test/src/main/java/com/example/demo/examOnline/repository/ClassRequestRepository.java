package com.example.demo.examOnline.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.examOnline.domain.ClassRequest;
import com.example.demo.examOnline.domain.Classes;

public interface ClassRequestRepository extends JpaRepository<ClassRequest , Integer>{
    
}
