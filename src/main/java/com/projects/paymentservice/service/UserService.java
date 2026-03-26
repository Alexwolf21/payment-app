package com.projects.paymentservice.service;

import com.projects.paymentservice.dto.UserRequest;
import com.projects.paymentservice.dto.UserResponse;
import com.projects.paymentservice.entity.User;
import com.projects.paymentservice.exception.DuplicateResourceException;
import com.projects.paymentservice.exception.ResourceNotFoundException;
import com.projects.paymentservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserResponse registerUser(UserRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.findByEmail(email).isPresent()) {
            throw new DuplicateResourceException("User already exists with email: " + email);
        }

        LocalDateTime now = LocalDateTime.now();

        User user = User.builder()
                .name(request.getName().trim())
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .createdAt(now)
                .updatedAt(now)
                .build();

        User savedUser = userRepository.save(user);
        return toUserResponse(savedUser);
    }

    public UserResponse getUserById(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("Invalid user id");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        return toUserResponse(user);
    }

    public UserResponse getUserByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }

        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        return toUserResponse(user);
    }

    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}