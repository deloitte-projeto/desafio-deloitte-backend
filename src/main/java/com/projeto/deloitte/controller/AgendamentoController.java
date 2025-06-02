package com.projeto.deloitte.controller;

import com.projeto.deloitte.dto.AgendamentoRequestDTO;
import com.projeto.deloitte.dto.AgendamentoResponseDTO;
import com.projeto.deloitte.service.AgendamentoService;
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
public class AgendamentoController {

    @Autowired
    private AgendamentoService agendamentoService;

    @PreAuthorize("hasRole('CLIENTE')")
    @PostMapping
    public ResponseEntity<AgendamentoResponseDTO> createAgendamento(@Valid @RequestBody AgendamentoRequestDTO agendamentoRequestDTO) {
        AgendamentoResponseDTO createdAgendamento = agendamentoService.createAgendamento(agendamentoRequestDTO);
        return new ResponseEntity<>(createdAgendamento, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<AgendamentoResponseDTO>> getAgendamentosByCliente(@PathVariable Long clienteId) {
        List<AgendamentoResponseDTO> agendamentos = agendamentoService.getAgendamentosByCliente(clienteId);
        return ResponseEntity.ok(agendamentos);
    }

    @PreAuthorize("hasAnyRole('CLIENTE', 'PROFISSIONAL', 'ADMIN')")
    @PutMapping("/{id}/cancel")
    public ResponseEntity<AgendamentoResponseDTO> cancelAgendamento(@PathVariable Long id) {
        AgendamentoResponseDTO canceledAgendamento = agendamentoService.cancelAgendamento(id);
        return ResponseEntity.ok(canceledAgendamento);
    }

    @PreAuthorize("hasAnyRole('PROFISSIONAL', 'ADMIN')")
    @GetMapping("/profissional/{profissionalId}/agenda")
    public ResponseEntity<List<AgendamentoResponseDTO>> getAgendaProfissional(
            @PathVariable Long profissionalId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<AgendamentoResponseDTO> agenda = agendamentoService.getAgendaProfissional(profissionalId, startDate, endDate);
        return ResponseEntity.ok(agenda);
    }

    @PreAuthorize("hasAnyRole('PROFISSIONAL', 'ADMIN')")
    @PutMapping("/{id}/complete")
    public ResponseEntity<AgendamentoResponseDTO> completeAgendamento(@PathVariable Long id) {
        AgendamentoResponseDTO completedAgendamento = agendamentoService.completeAgendamento(id);
        return ResponseEntity.ok(completedAgendamento);
    }
} 