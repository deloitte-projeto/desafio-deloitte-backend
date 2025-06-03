package com.projeto.deloitte.controller;

import com.projeto.deloitte.dto.AgendamentoRequestDTO;
import com.projeto.deloitte.dto.AgendamentoResponseDTO;
import com.projeto.deloitte.service.AgendamentoService;
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
@RequestMapping("/api/agendamentos")
@Tag(name = "Agendamento", description = "Endpoints para gerenciamento de agendamentos")
public class AgendamentoController {

    @Autowired
    private AgendamentoService agendamentoService;

    @Operation(summary = "Cria um novo agendamento", description = "Permite que um cliente crie um novo agendamento. Requer ROLE_CLIENTE.")
    @ApiResponse(responseCode = "201", description = "Agendamento criado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AgendamentoResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos ou horário indisponível")
    @ApiResponse(responseCode = "401", description = "Não autorizado")
    @ApiResponse(responseCode = "403", description = "Acesso proibido (requer ROLE_CLIENTE)")
    @PreAuthorize("hasRole('CLIENTE')")
    @PostMapping
    public ResponseEntity<AgendamentoResponseDTO> createAgendamento(@Valid @RequestBody AgendamentoRequestDTO agendamentoRequestDTO) {
        AgendamentoResponseDTO createdAgendamento = agendamentoService.createAgendamento(agendamentoRequestDTO);
        return new ResponseEntity<>(createdAgendamento, HttpStatus.CREATED);
    }

    @Operation(summary = "Obtém agendamentos por cliente", description = "Retorna uma lista de agendamentos de um cliente específico.")
    @ApiResponse(responseCode = "200", description = "Lista de agendamentos obtida com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<AgendamentoResponseDTO>> getAgendamentosByCliente(
            @Parameter(description = "ID do cliente") @PathVariable Long clienteId
    ) {
        List<AgendamentoResponseDTO> agendamentos = agendamentoService.getAgendamentosByCliente(clienteId);
        return ResponseEntity.ok(agendamentos);
    }

    @Operation(summary = "Cancela um agendamento", description = "Permite que um cliente, profissional ou admin cancele um agendamento. Requer ROLE_CLIENTE (próprio agendamento), ROLE_PROFISSIONAL (próprio agendamento) ou ROLE_ADMIN.")
    @ApiResponse(responseCode = "200", description = "Agendamento cancelado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AgendamentoResponseDTO.class)))
    @ApiResponse(responseCode = "401", description = "Não autorizado")
    @ApiResponse(responseCode = "403", description = "Acesso proibido (requer permissão)")
    @ApiResponse(responseCode = "404", description = "Agendamento não encontrado")
    @PreAuthorize("hasAnyRole('CLIENTE', 'PROFISSIONAL', 'ADMIN')")
    @PutMapping("/cancel/{id}")
    public ResponseEntity<AgendamentoResponseDTO> cancelAgendamento(@Parameter(description = "ID do agendamento a ser cancelado") @PathVariable Long id) {
        AgendamentoResponseDTO canceledAgendamento = agendamentoService.cancelAgendamento(id);
        return ResponseEntity.ok(canceledAgendamento);
    }

    @Operation(summary = "Obtém a agenda de um profissional", description = "Retorna uma lista de agendamentos para um profissional em um período específico.")
    @ApiResponse(responseCode = "200", description = "Agenda do profissional obtida com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    @GetMapping("/profissional/{profissionalId}")
    public ResponseEntity<List<AgendamentoResponseDTO>> getAgendaProfissional(
            @Parameter(description = "ID do profissional") @PathVariable Long profissionalId,
            @Parameter(description = "Data de início do período (formato YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Data de fim do período (formato YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<AgendamentoResponseDTO> agenda = agendamentoService.getAgendaProfissional(profissionalId, startDate, endDate);
        return ResponseEntity.ok(agenda);
    }

    @Operation(summary = "Marca um agendamento como concluído", description = "Altera o status de um agendamento para concluído. Requer ROLE_PROFISSIONAL ou ROLE_ADMIN.")
    @ApiResponse(responseCode = "200", description = "Agendamento concluído com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AgendamentoResponseDTO.class)))
    @ApiResponse(responseCode = "401", description = "Não autorizado")
    @ApiResponse(responseCode = "403", description = "Acesso proibido (requer ROLE_PROFISSIONAL ou ROLE_ADMIN)")
    @ApiResponse(responseCode = "404", description = "Agendamento não encontrado")
    @PreAuthorize("hasAnyRole('PROFISSIONAL', 'ADMIN')")
    @PutMapping("/{id}/complete")
    public ResponseEntity<AgendamentoResponseDTO> completeAgendamento(@Parameter(description = "ID do agendamento a ser concluído") @PathVariable Long id) {
        AgendamentoResponseDTO completedAgendamento = agendamentoService.completeAgendamento(id);
        return ResponseEntity.ok(completedAgendamento);
    }
} 