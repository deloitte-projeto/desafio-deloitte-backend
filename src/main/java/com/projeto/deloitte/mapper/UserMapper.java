package com.projeto.deloitte.mapper;

import com.projeto.deloitte.dto.UserRequestDTO;
import com.projeto.deloitte.dto.UserResponseDTO;
import com.projeto.deloitte.model.User;

public class UserMapper {
    public static User toEntity(UserRequestDTO dto) {
        if (dto == null) return null;
        User user = new User();
        user.setNome(dto.getNome());
        user.setEmail(dto.getEmail());
        user.setSenha(dto.getSenha());
        user.setTipoUsuario(dto.getTipoUsuario());
        return user;
    }

    public static UserResponseDTO toResponseDTO(User user) {
        if (user == null) return null;
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setNome(user.getNome());
        dto.setEmail(user.getEmail());
        dto.setTipoUsuario(user.getTipoUsuario());
        return dto;
    }
} 