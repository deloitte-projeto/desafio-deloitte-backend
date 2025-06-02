package com.projeto.deloitte.service;

import com.projeto.deloitte.dto.AgendamentoRequestDTO;
import com.projeto.deloitte.dto.AgendamentoResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface AgendamentoService {
    AgendamentoResponseDTO createAgendamento(AgendamentoRequestDTO agendamentoRequestDTO);
    Page<AgendamentoResponseDTO> getAgendamentosByCliente(Long clienteId, Pageable pageable);
    AgendamentoResponseDTO cancelAgendamento(Long agendamentoId);
    Page<AgendamentoResponseDTO> getAgendaProfissional(
            Long profissionalId,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    );
    AgendamentoResponseDTO completeAgendamento(Long agendamentoId);
} 