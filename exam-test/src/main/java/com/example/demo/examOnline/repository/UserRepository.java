package com.example.demo.examOnline.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.examOnline.domain.User;
import com.example.demo.examOnline.domain.enums.RoleName;
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

        @Query("SELECT COUNT(u) > 0 FROM User u WHERE (:email IS NOT NULL AND u.email = :email)")
    Boolean checkMail( @Param("email") String email);

    @Query("SELECT NEW com.example.demo.examOnline.dto.response.UserResponseDTO(" +
            "u.userId, u.email, u.fullName, u.phoneNumber, u.roleName) " +
            "FROM User u " +
            "WHERE (:role IS NULL OR u.roleName = :role)")
    Page<UserResponseDTO> findUserListWithRole(@Param("role") RoleName roleName, Pageable pageable);

    @Transactional
    @Modifying
    @Query("DELETE FROM User u WHERE u.userId IN :userIds")
    void deleteUser(@Param("userIds") List<Integer> userIds);

}
