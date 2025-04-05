package com.gt_enterprise.auth_service.controller;

import com.gt_enterprise.auth_service.dto.*;
import com.gt_enterprise.auth_service.entity.User;
import org.aspectj.weaver.patterns.IToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gt_enterprise.auth_service.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<SuccessfulResponse<RegisterResponse>> register(@RequestBody RegisterRequest registerRequest) {
        User createdUser = authService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(new SuccessfulResponse<>(201, "User created successfully", new RegisterResponse(
                createdUser.getId(),
                createdUser.getUsername(),
                createdUser.getEmail(),
                createdUser.getRoles())));
    }

    @PostMapping("/login")
    public ResponseEntity<SuccessfulResponse<AuthenticationResponse>> login(@RequestBody AuthenticationRequest authenticationRequest) {
        String response = authService.login(authenticationRequest);
        return ResponseEntity.ok(new SuccessfulResponse<>(200, "Login successful", new AuthenticationResponse(response)));
    }
}

