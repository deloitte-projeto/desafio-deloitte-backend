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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/agendamentos")
@Tag(name = "Agendamentos", description = "Endpoints para agendamento e gerenciamento de consultas/serviços")
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

    @Operation(summary = "Obtém agendamentos por cliente com paginação e ordenação", description = "Retorna uma lista paginada e ordenada de agendamentos de um cliente específico. Requer ROLE_CLIENTE ou ROLE_ADMIN.")
    @ApiResponse(responseCode = "200", description = "Agendamentos do cliente obtidos com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    @ApiResponse(responseCode = "401", description = "Não autorizado")
    @ApiResponse(responseCode = "403", description = "Acesso proibido (requer ROLE_CLIENTE ou ROLE_ADMIN)")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<Page<AgendamentoResponseDTO>> getAgendamentosByCliente(
            @Parameter(description = "ID do cliente") @PathVariable Long clienteId,
            @Parameter(description = "Número da página (0-indexed, padrão: 0)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página (padrão: 10)")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Critério de ordenação (ex: dataHoraInicio,status,asc/desc)")
            @RequestParam(defaultValue = "id,asc") String[] sort
    ) {
        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        Page<AgendamentoResponseDTO> agendamentos = agendamentoService.getAgendamentosByCliente(clienteId, pageable);
        return ResponseEntity.ok(agendamentos);
    }

    @Operation(summary = "Cancela um agendamento", description = "Permite cancelar um agendamento. Pode ser usado por cliente, profissional ou admin.")
    @ApiResponse(responseCode = "200", description = "Agendamento cancelado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AgendamentoResponseDTO.class)))
    @ApiResponse(responseCode = "401", description = "Não autorizado")
    @ApiResponse(responseCode = "403", description = "Acesso proibido")
    @ApiResponse(responseCode = "404", description = "Agendamento não encontrado")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    @PutMapping("/{id}/cancel")
    public ResponseEntity<AgendamentoResponseDTO> cancelAgendamento(@Parameter(description = "ID do agendamento a ser cancelado") @PathVariable Long id) {
        AgendamentoResponseDTO canceledAgendamento = agendamentoService.cancelAgendamento(id);
        return ResponseEntity.ok(canceledAgendamento);
    }

    @Operation(summary = "Obtém a agenda de um profissional com paginação e ordenação", description = "Retorna uma lista paginada e ordenada de agendamentos de um profissional em um período específico. Requer ROLE_PROFISSIONAL ou ROLE_ADMIN.")
    @ApiResponse(responseCode = "200", description = "Agenda do profissional obtida com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    @ApiResponse(responseCode = "401", description = "Não autorizado")
    @ApiResponse(responseCode = "403", description = "Acesso proibido (requer ROLE_PROFISSIONAL ou ROLE_ADMIN)")
    @ApiResponse(responseCode = "404", description = "Profissional não encontrado")
    @PreAuthorize("hasAnyRole('PROFISSIONAL', 'ADMIN')")
    @GetMapping("/profissional/{profissionalId}/agenda")
    public ResponseEntity<Page<AgendamentoResponseDTO>> getAgendaProfissional(
            @Parameter(description = "ID do profissional") @PathVariable Long profissionalId,
            @Parameter(description = "Data de início (formato YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Data de fim (formato YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "Número da página (0-indexed, padrão: 0)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página (padrão: 10)")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Critério de ordenação (ex: dataHoraInicio,status,asc/desc)")
            @RequestParam(defaultValue = "id,asc") String[] sort
    ) {
        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        Page<AgendamentoResponseDTO> agenda = agendamentoService.getAgendaProfissional(profissionalId, startDate, endDate, pageable);
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