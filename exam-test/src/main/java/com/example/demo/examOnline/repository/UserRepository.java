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
    Optional<User> findByFullName(String fullName);
    Optional<User> findByUserCode(String userCode);

    @Query("SELECT COUNT(u) > 0 FROM User u WHERE (:email IS NOT NULL AND u.email = :email) AND u.userId <> :userId")
    Boolean checkEmailIsExist(@Param("userId") Integer userId, @Param("email") String email);

   
    

}
