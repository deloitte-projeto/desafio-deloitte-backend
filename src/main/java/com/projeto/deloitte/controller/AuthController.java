package com.projeto.deloitte.controller;

import com.projeto.deloitte.dto.JwtAuthResponseDTO;
import com.projeto.deloitte.dto.LoginRequestDTO;
import com.projeto.deloitte.dto.UserRequestDTO;
import com.projeto.deloitte.dto.UserResponseDTO;
import com.projeto.deloitte.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        UserResponseDTO registeredUser = authService.register(userRequestDTO);
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        JwtAuthResponseDTO jwtResponse = authService.login(loginRequestDTO);
        return new ResponseEntity<>(jwtResponse, HttpStatus.OK);
    }
} 