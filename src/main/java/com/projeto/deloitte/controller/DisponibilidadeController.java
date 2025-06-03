package com.projeto.deloitte.controller;

import com.projeto.deloitte.dto.DisponibilidadeRequestDTO;
import com.projeto.deloitte.dto.DisponibilidadeResponseDTO;
import com.projeto.deloitte.dto.TimeSlotDTO;
import com.projeto.deloitte.service.DisponibilidadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/disponibilidades")
@Tag(name = "Disponibilidade", description = "Endpoints para gerenciamento da disponibilidade de profissionais e geração de slots")
public class DisponibilidadeController {

    @Autowired
    private DisponibilidadeService disponibilidadeService;

    @Operation(summary = "Cria um novo bloco de disponibilidade", description = "Permite que um profissional defina um novo bloco de horários de trabalho. Requer ROLE_PROFISSIONAL.")
    @ApiResponse(responseCode = "201", description = "Disponibilidade criada com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DisponibilidadeResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos")
    @ApiResponse(responseCode = "401", description = "Não autorizado")
    @ApiResponse(responseCode = "403", description = "Acesso proibido (requer ROLE_PROFISSIONAL)")
    @PreAuthorize("hasRole('PROFISSIONAL')")
    @PostMapping
    public ResponseEntity<DisponibilidadeResponseDTO> createDisponibilidade(@Valid @RequestBody DisponibilidadeRequestDTO disponibilidadeRequestDTO) {
        DisponibilidadeResponseDTO createdDisponibilidade = disponibilidadeService.createDisponibilidade(disponibilidadeRequestDTO);
        return new ResponseEntity<>(createdDisponibilidade, HttpStatus.CREATED);
    }

    @Operation(summary = "Obtém um bloco de disponibilidade por ID", description = "Retorna as informações de um bloco de disponibilidade específico pelo ID.")
    @ApiResponse(responseCode = "200", description = "Disponibilidade encontrada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DisponibilidadeResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Disponibilidade não encontrada")
    @GetMapping("/{id}")
    public ResponseEntity<DisponibilidadeResponseDTO> getDisponibilidadeById(@Parameter(description = "ID do bloco de disponibilidade") @PathVariable Long id) {
        DisponibilidadeResponseDTO disponibilidade = disponibilidadeService.getDisponibilidadeById(id);
        return ResponseEntity.ok(disponibilidade);
    }

    @Operation(summary = "Lista todos os blocos de disponibilidade", description = "Retorna uma lista de todos os blocos de disponibilidade registrados.")
    @ApiResponse(responseCode = "200", description = "Lista de disponibilidades obtida com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    @GetMapping
    public ResponseEntity<List<DisponibilidadeResponseDTO>> getAllDisponibilidades(
    ) {
        List<DisponibilidadeResponseDTO> disponibilidades = disponibilidadeService.getAllDisponibilidades();
        return ResponseEntity.ok(disponibilidades);
    }

    @Operation(summary = "Lista blocos de disponibilidade por profissional", description = "Retorna uma lista de blocos de disponibilidade definidos por um profissional específico.")
    @ApiResponse(responseCode = "200", description = "Disponibilidades do profissional obtidas com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    @ApiResponse(responseCode = "404", description = "Profissional não encontrado")
    @GetMapping("/profissional/{profissionalId}")
    public ResponseEntity<List<DisponibilidadeResponseDTO>> getDisponibilidadesByProfissional(
            @Parameter(description = "ID do profissional") @PathVariable Long profissionalId
    ) {
        List<DisponibilidadeResponseDTO> disponibilidades = disponibilidadeService.getDisponibilidadesByProfissional(profissionalId);
        return ResponseEntity.ok(disponibilidades);
    }

    @Operation(summary = "Atualiza um bloco de disponibilidade existente", description = "Permite que um profissional atualize um bloco de disponibilidade. Requer ROLE_PROFISSIONAL.")
    @ApiResponse(responseCode = "200", description = "Disponibilidade atualizada com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DisponibilidadeResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos")
    @ApiResponse(responseCode = "401", description = "Não autorizado")
    @ApiResponse(responseCode = "403", description = "Acesso proibido (requer ROLE_PROFISSIONAL ou ser o proprietário da disponibilidade)")
    @ApiResponse(responseCode = "404", description = "Disponibilidade não encontrada")
    @PreAuthorize("hasRole('PROFISSIONAL')")
    @PutMapping("/{id}")
    public ResponseEntity<DisponibilidadeResponseDTO> updateDisponibilidade(@Parameter(description = "ID do bloco de disponibilidade a ser atualizado") @PathVariable Long id, @Valid @RequestBody DisponibilidadeRequestDTO disponibilidadeRequestDTO) {
        DisponibilidadeResponseDTO updatedDisponibilidade = disponibilidadeService.updateDisponibilidade(id, disponibilidadeRequestDTO);
        return ResponseEntity.ok(updatedDisponibilidade);
    }

    @Operation(summary = "Deleta um bloco de disponibilidade", description = "Permite que um profissional delete um bloco de disponibilidade. Requer ROLE_PROFISSIONAL.")
    @ApiResponse(responseCode = "200", description = "Disponibilidade deletada com sucesso")
    @ApiResponse(responseCode = "401", description = "Não autorizado")
    @ApiResponse(responseCode = "403", description = "Acesso proibido (requer ROLE_PROFISSIONAL ou ser o proprietário da disponibilidade)")
    @ApiResponse(responseCode = "404", description = "Disponibilidade não encontrada")
    @PreAuthorize("hasRole('PROFISSIONAL')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDisponibilidade(@Parameter(description = "ID do bloco de disponibilidade a ser deletado") @PathVariable Long id) {
        disponibilidadeService.deleteDisponibilidade(id);
        return new ResponseEntity<>("Disponibilidade deletada com sucesso!", HttpStatus.OK);
    }

    @Operation(summary = "Gera slots de horários disponíveis", description = "Retorna uma lista de slots de horários disponíveis para um profissional e serviço em uma data específica, excluindo horários já agendados.")
    @ApiResponse(responseCode = "200", description = "Slots gerados com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TimeSlotDTO.class)))
    @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos")
    @ApiResponse(responseCode = "404", description = "Profissional ou Serviço não encontrado")
    @GetMapping("/slots")
    public ResponseEntity<List<TimeSlotDTO>> generateAvailableSlots(
            @Parameter(description = "ID do profissional") @RequestParam Long profissionalId,
            @Parameter(description = "ID do serviço") @RequestParam Long servicoId,
            @Parameter(description = "Data para a qual gerar os slots (formato YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<TimeSlotDTO> slots = disponibilidadeService.generateAvailableSlots(profissionalId, servicoId, date);
        return ResponseEntity.ok(slots);
    }
} 