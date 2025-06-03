package com.projeto.deloitte.service.impl;

import com.projeto.deloitte.dto.UserRequestDTO;
import com.projeto.deloitte.dto.UserResponseDTO;
import com.projeto.deloitte.exception.ResourceNotFoundException;
import com.projeto.deloitte.exception.UnauthorizedAccessException;
import com.projeto.deloitte.exception.ValidationException;
import com.projeto.deloitte.mapper.UserMapper;
import com.projeto.deloitte.model.User;
import com.projeto.deloitte.repository.UserRepository;
import com.projeto.deloitte.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.projeto.deloitte.enums.TipoUsuario.ADMIN;
import com.projeto.deloitte.enums.TipoUsuario;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário logado não encontrado."));

        return user;
    }

    @Override
    public UserResponseDTO registerUser(UserRequestDTO userRequestDTO) {
        if (userRepository.findByEmail(userRequestDTO.getEmail()).isPresent()) {
            throw new ValidationException("Email já cadastrado!");
        }

        User user = UserMapper.toEntity(userRequestDTO);
        user.setSenha(passwordEncoder.encode(userRequestDTO.getSenha()));

        User savedUser = userRepository.save(user);
        return UserMapper.toResponseDTO(savedUser);
    }

    @Override
    public UserResponseDTO getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "email", null));

        return UserMapper.toResponseDTO(user);
    }

    @Override
    public UserResponseDTO updateUserProfile(Long userId, UserRequestDTO userRequestDTO) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "ID", userId));

        User currentUser = getCurrentAuthenticatedUser();

        if (!existingUser.getId().equals(currentUser.getId()) && currentUser.getTipoUsuario() != ADMIN) {
            throw new UnauthorizedAccessException("Você não tem permissão para atualizar o perfil deste usuário.");
        }

        existingUser.setNome(userRequestDTO.getNome());
        existingUser.setEmail(userRequestDTO.getEmail());

        if (userRequestDTO.getSenha() != null && !userRequestDTO.getSenha().isEmpty()) {
            existingUser.setSenha(passwordEncoder.encode(userRequestDTO.getSenha()));
        }

        existingUser.setTipoUsuario(userRequestDTO.getTipoUsuario());
        User updatedUser = userRepository.save(existingUser);
        return UserMapper.toResponseDTO(updatedUser);
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDTO getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "ID", userId));
        return UserMapper.toResponseDTO(user);
    }

    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "ID", userId));
        userRepository.delete(user);
    }

    @Override
    public List<UserResponseDTO> getProfessionals() {
        return userRepository.findByTipoUsuario(TipoUsuario.PROFISSIONAL).stream()
                .map(UserMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
} 