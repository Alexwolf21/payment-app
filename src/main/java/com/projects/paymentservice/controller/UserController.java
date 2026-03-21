package com.projects.paymentservice.controller;

import com.projects.paymentservice.dto.UserRequest;
import com.projects.paymentservice.entity.User;
import com.projects.paymentservice.service.UserService;
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
    public ResponseEntity<User> registerUser(@RequestBody UserRequest request){
        User createdUser = userService.registerUser(request);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId){
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/email/{emailId}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String emailId){
        User user = userService.getUserByEmail(emailId);
        return ResponseEntity.ok(user);
    }
}
