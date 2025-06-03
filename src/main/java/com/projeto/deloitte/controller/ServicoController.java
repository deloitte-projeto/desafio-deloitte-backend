package com.projeto.deloitte.controller;

import com.projeto.deloitte.dto.ServicoRequestDTO;
import com.projeto.deloitte.dto.ServicoResponseDTO;
import com.projeto.deloitte.service.ServicoService;
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
@RequestMapping("/api/servicos")
@Tag(name = "Serviços", description = "Endpoints para gerenciamento de serviços e especialidades")
public class ServicoController {

    @Autowired
    private ServicoService servicoService;

    @Operation(summary = "Cria um novo serviço", description = "Permite que um profissional cadastre um novo serviço que ele oferece. Requer ROLE_PROFISSIONAL.")
    @ApiResponse(responseCode = "201", description = "Serviço criado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServicoResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos")
    @ApiResponse(responseCode = "401", description = "Não autorizado")
    @ApiResponse(responseCode = "403", description = "Acesso proibido (requer ROLE_PROFISSIONAL)")
    @PreAuthorize("hasRole('PROFISSIONAL')")
    @PostMapping
    public ResponseEntity<ServicoResponseDTO> createServico(@Valid @RequestBody ServicoRequestDTO servicoRequestDTO) {
        ServicoResponseDTO createdServico = servicoService.createServico(servicoRequestDTO);
        return new ResponseEntity<>(createdServico, HttpStatus.CREATED);
    }

    @Operation(summary = "Obtém um serviço por ID", description = "Retorna as informações de um serviço específico pelo ID.")
    @ApiResponse(responseCode = "200", description = "Serviço encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServicoResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Serviço não encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<ServicoResponseDTO> getServicoById(@Parameter(description = "ID do serviço a ser buscado") @PathVariable Long id) {
        ServicoResponseDTO servico = servicoService.getServicoById(id);
        return ResponseEntity.ok(servico);
    }

    @Operation(summary = "Lista todos os serviços", description = "Retorna uma lista de todos os serviços disponíveis no sistema.")
    @ApiResponse(responseCode = "200", description = "Lista de serviços obtida com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    @GetMapping
    public ResponseEntity<List<ServicoResponseDTO>> getAllServicos(
    ) {
        List<ServicoResponseDTO> servicos = servicoService.getAllServicos();
        return ResponseEntity.ok(servicos);
    }

    @Operation(summary = "Atualiza um serviço existente", description = "Permite que um profissional atualize um serviço que ele oferece. Requer ROLE_PROFISSIONAL.")
    @ApiResponse(responseCode = "200", description = "Serviço atualizado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServicoResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos")
    @ApiResponse(responseCode = "401", description = "Não autorizado")
    @ApiResponse(responseCode = "403", description = "Acesso proibido (requer ROLE_PROFISSIONAL ou ser o proprietário do serviço)")
    @ApiResponse(responseCode = "404", description = "Serviço não encontrado")
    @PreAuthorize("hasRole('PROFISSIONAL')")
    @PutMapping("/{id}")
    public ResponseEntity<ServicoResponseDTO> updateServico(@Parameter(description = "ID do serviço a ser atualizado") @PathVariable Long id, @Valid @RequestBody ServicoRequestDTO servicoRequestDTO) {
        ServicoResponseDTO updatedServico = servicoService.updateServico(id, servicoRequestDTO);
        return ResponseEntity.ok(updatedServico);
    }

    @Operation(summary = "Deleta um serviço", description = "Permite que um profissional delete um serviço que ele oferece. Requer ROLE_PROFISSIONAL.")
    @ApiResponse(responseCode = "200", description = "Serviço deletado com sucesso")
    @ApiResponse(responseCode = "401", description = "Não autorizado")
    @ApiResponse(responseCode = "403", description = "Acesso proibido (requer ROLE_PROFISSIONAL ou ser o proprietário do serviço)")
    @ApiResponse(responseCode = "404", description = "Serviço não encontrado")
    @PreAuthorize("hasRole('PROFISSIONAL')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteServico(@Parameter(description = "ID do serviço a ser deletado") @PathVariable Long id) {
        servicoService.deleteServico(id);
        return new ResponseEntity<>("Serviço deletado com sucesso!", HttpStatus.OK);
    }

    @Operation(summary = "Lista serviços por profissional", description = "Retorna uma lista de serviços oferecidos por um profissional específico.")
    @ApiResponse(responseCode = "200", description = "Serviços do profissional obtidos com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    @ApiResponse(responseCode = "404", description = "Profissional não encontrado")
    @GetMapping("/profissional/{profissionalId}")
    public ResponseEntity<List<ServicoResponseDTO>> getServicosByProfissional(
            @Parameter(description = "ID do profissional") @PathVariable Long profissionalId
    ) {
        List<ServicoResponseDTO> servicos = servicoService.getServicosByProfissional(profissionalId);
        return ResponseEntity.ok(servicos);
    }
} 