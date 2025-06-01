package com.projeto.deloitte.service;

import com.projeto.deloitte.dto.UserRequestDTO;
import com.projeto.deloitte.dto.UserResponseDTO;

import java.util.List;

public interface UserService {
    UserResponseDTO registerUser(UserRequestDTO userRequestDTO);
    UserResponseDTO getCurrentUser();
    UserResponseDTO updateUserProfile(Long userId, UserRequestDTO userRequestDTO);
    List<UserResponseDTO> getAllUsers();
    UserResponseDTO getUserById(Long userId);
    void deleteUser(Long userId);
} 