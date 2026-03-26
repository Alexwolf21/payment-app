package com.projects.paymentservice.controller;

import com.projects.paymentservice.dto.UserRequest;
import com.projects.paymentservice.dto.UserResponse;
import com.projects.paymentservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody UserRequest request) {
        UserResponse createdUser = userService.registerUser(request);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId) {
        UserResponse user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/email/{emailId}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String emailId){
        UserResponse user = userService.getUserByEmail(emailId);
        return ResponseEntity.ok(user);
    }
}