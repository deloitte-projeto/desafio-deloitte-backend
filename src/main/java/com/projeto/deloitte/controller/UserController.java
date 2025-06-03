package com.projeto.deloitte.controller;

import com.projeto.deloitte.dto.UserRequestDTO;
import com.projeto.deloitte.dto.UserResponseDTO;
import com.projeto.deloitte.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Usuários", description = "Endpoints para gerenciamento de usuários")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "Obtém os dados do usuário autenticado", description = "Retorna as informações do usuário atualmente logado.")
    @ApiResponse(responseCode = "200", description = "Dados do usuário obtidos com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class)))
    @ApiResponse(responseCode = "401", description = "Não autorizado")
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser() {
        UserResponseDTO currentUser = userService.getCurrentUser();
        return ResponseEntity.ok(currentUser);
    }

    @Operation(summary = "Atualiza o perfil de um usuário", description = "Permite que um usuário atualize seu próprio perfil. Requer autenticação.")
    @ApiResponse(responseCode = "200", description = "Perfil atualizado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos")
    @ApiResponse(responseCode = "401", description = "Não autorizado")
    @ApiResponse(responseCode = "403", description = "Acesso proibido")
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUserProfile(@Parameter(description = "ID do usuário a ser atualizado") @PathVariable Long id, @Valid @RequestBody UserRequestDTO userRequestDTO) {
        UserResponseDTO updatedUser = userService.updateUserProfile(id, userRequestDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "Lista todos os usuários", description = "Retorna uma lista de todos os usuários registrados. Requer ROLE_ADMIN.")
    @ApiResponse(responseCode = "200", description = "Lista de usuários obtida com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    @ApiResponse(responseCode = "401", description = "Não autorizado")
    @ApiResponse(responseCode = "403", description = "Acesso proibido (requer ROLE_ADMIN)")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers(
    ) {
        List<UserResponseDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Obtém um usuário por ID", description = "Retorna as informações de um usuário específico pelo ID. Requer ROLE_ADMIN.")
    @ApiResponse(responseCode = "200", description = "Usuário encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class)))
    @ApiResponse(responseCode = "401", description = "Não autorizado")
    @ApiResponse(responseCode = "403", description = "Acesso proibido (requer ROLE_ADMIN)")
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@Parameter(description = "ID do usuário a ser buscado") @PathVariable Long id) {
        UserResponseDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Deleta um usuário por ID", description = "Deleta um usuário do sistema pelo ID. Requer ROLE_ADMIN.")
    @ApiResponse(responseCode = "200", description = "Usuário deletado com sucesso")
    @ApiResponse(responseCode = "401", description = "Não autorizado")
    @ApiResponse(responseCode = "403", description = "Acesso proibido (requer ROLE_ADMIN)")
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@Parameter(description = "ID do usuário a ser deletado") @PathVariable Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>("Usuário deletado com sucesso!", HttpStatus.OK);
    }

    @Operation(summary = "Obtém uma lista de todos os profissionais", description = "Retorna uma lista de todos os usuários com o perfil PROFISSIONAL.")
    @ApiResponse(responseCode = "200", description = "Lista de profissionais obtida com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class, type = "array")))
    @ApiResponse(responseCode = "401", description = "Não autorizado")
    @ApiResponse(responseCode = "403", description = "Acesso proibido (apenas CLIENTES e ADMINS)")
    @GetMapping("/professionals")
    public ResponseEntity<List<UserResponseDTO>> getProfessionals() {
        List<UserResponseDTO> professionals = userService.getProfessionals();
        return ResponseEntity.ok(professionals);
    }
} 