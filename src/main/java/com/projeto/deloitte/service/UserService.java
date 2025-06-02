package com.projeto.deloitte.service;

import com.projeto.deloitte.dto.UserRequestDTO;
import com.projeto.deloitte.dto.UserResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface UserService {
    UserResponseDTO registerUser(UserRequestDTO userRequestDTO);
    UserResponseDTO getCurrentUser();
    UserResponseDTO updateUserProfile(Long userId, UserRequestDTO userRequestDTO);
    Page<UserResponseDTO> getAllUsers(Pageable pageable);
    UserResponseDTO getUserById(Long userId);
    void deleteUser(Long userId);
} 