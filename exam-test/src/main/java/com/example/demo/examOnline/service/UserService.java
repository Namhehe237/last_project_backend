package com.example.demo.examOnline.service;


import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.examOnline.dto.response.UserResponseDTO;
import com.example.demo.examOnline.repository.UserRepository;


@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }

    public List<UserResponseDTO> getListStudentOfClass(Integer classId){

        List<UserResponseDTO> userList = userRepository.findListStudentByClassId(classId);

        return userList;
    }
}