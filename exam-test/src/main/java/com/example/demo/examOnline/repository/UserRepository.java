package com.example.demo.examOnline.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.examOnline.domain.User;
import com.example.demo.examOnline.dto.response.UserResponseDTO;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    @Query(
        "SELECT new com.example.demo.examOnline.dto.response.UserResponseDTO ("+
        " s.userId, s.email, s.fullName, s.phoneNumber) "+
        " FROM Class c JOIN c.students s where c.classId = :classId"
    )
    List<UserResponseDTO> findListStudentByClassId(@Param("classId") Integer classId);
}
