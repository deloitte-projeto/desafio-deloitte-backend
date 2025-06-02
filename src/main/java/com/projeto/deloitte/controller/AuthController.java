package com.projeto.deloitte.controller;

import com.projeto.deloitte.dto.JwtAuthResponseDTO;
import com.projeto.deloitte.dto.LoginRequestDTO;
import com.projeto.deloitte.dto.UserRequestDTO;
import com.projeto.deloitte.dto.UserResponseDTO;
import com.projeto.deloitte.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Autenticação", description = "Endpoints para registro e login de usuários")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Operation(summary = "Registra um novo usuário", description = "Permite que um novo usuário (cliente ou profissional) se registre no sistema.")
    @ApiResponse(responseCode = "201", description = "Usuário registrado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos")
    @ApiResponse(responseCode = "409", description = "Email já registrado")
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        UserResponseDTO registeredUser = authService.register(userRequestDTO);
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

    @Operation(summary = "Autentica um usuário", description = "Permite que um usuário existente faça login e receba um token JWT para acesso.")
    @ApiResponse(responseCode = "200", description = "Login bem-sucedido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = JwtAuthResponseDTO.class)))
    @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos")
    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        JwtAuthResponseDTO jwtResponse = authService.login(loginRequestDTO);
        return new ResponseEntity<>(jwtResponse, HttpStatus.OK);
    }
} 