package com.projeto.deloitte.service;

import com.projeto.deloitte.dto.DisponibilidadeRequestDTO;
import com.projeto.deloitte.dto.DisponibilidadeResponseDTO;
import com.projeto.deloitte.dto.TimeSlotDTO;


import java.time.LocalDate;
import java.util.List;

public interface DisponibilidadeService {
    DisponibilidadeResponseDTO createDisponibilidade(DisponibilidadeRequestDTO disponibilidadeRequestDTO);
    DisponibilidadeResponseDTO getDisponibilidadeById(Long id);
    List<DisponibilidadeResponseDTO> getAllDisponibilidades();
    List<DisponibilidadeResponseDTO> getDisponibilidadesByProfissional(Long profissionalId);
    DisponibilidadeResponseDTO updateDisponibilidade(Long id, DisponibilidadeRequestDTO disponibilidadeRequestDTO);
    void deleteDisponibilidade(Long id);
    
    List<TimeSlotDTO> generateAvailableSlots(
            Long profissionalId,
            Long servicoId,
            LocalDate date
    );
} 