package com.projeto.deloitte.service;

import com.projeto.deloitte.dto.AgendamentoRequestDTO;
import com.projeto.deloitte.dto.AgendamentoResponseDTO;


import java.time.LocalDate;
import java.util.List;

public interface AgendamentoService {
    AgendamentoResponseDTO createAgendamento(AgendamentoRequestDTO agendamentoRequestDTO);
    List<AgendamentoResponseDTO> getAgendamentosByCliente(Long clienteId);
    AgendamentoResponseDTO cancelAgendamento(Long agendamentoId);
    List<AgendamentoResponseDTO> getAgendaProfissional(
            Long profissionalId,
            LocalDate startDate,
            LocalDate endDate
    );
    AgendamentoResponseDTO completeAgendamento(Long agendamentoId);
} 