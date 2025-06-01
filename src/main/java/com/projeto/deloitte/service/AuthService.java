package com.projeto.deloitte.service;

import com.projeto.deloitte.dto.JwtAuthResponseDTO;
import com.projeto.deloitte.dto.LoginRequestDTO;
import com.projeto.deloitte.dto.UserRequestDTO;
import com.projeto.deloitte.dto.UserResponseDTO;

public interface AuthService {
    UserResponseDTO register(UserRequestDTO userRequestDTO);
    JwtAuthResponseDTO login(LoginRequestDTO loginRequestDTO);
} 