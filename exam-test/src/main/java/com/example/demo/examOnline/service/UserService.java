package com.example.demo.examOnline.service;

import lombok.RequiredArgsConstructor;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.examOnline.domain.User;
import com.example.demo.examOnline.dto.request.UpdateUserInformationRequest;
import com.example.demo.examOnline.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }

    public void updateUserInformation(Integer userId, UpdateUserInformationRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không có user với id: " + userId));

        Boolean checkEmailIsExist = userRepository.checkEmailIsExist(userId, request.getEmail());

        if (checkEmailIsExist) {
            throw new DataIntegrityViolationException("email đã được sử dụng");
        }

        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getPassword() != null) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }

        userRepository.save(user);
    }

    public User getUserInformationDetail(Integer userId){
        return userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("Không có user này"));
    }

    public Integer getUserIdByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(User::getUserId)
                .orElse(null);
    }

    public void updateAvatarUrl(Integer userId, String avatarUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        user.setAvatarUrl(avatarUrl);
        userRepository.save(user);
    }
    
}