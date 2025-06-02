package com.projeto.deloitte.service;

import com.projeto.deloitte.dto.DisponibilidadeRequestDTO;
import com.projeto.deloitte.dto.DisponibilidadeResponseDTO;
import com.projeto.deloitte.dto.TimeSlotDTO; // Será criado posteriormente
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface DisponibilidadeService {
    DisponibilidadeResponseDTO createDisponibilidade(DisponibilidadeRequestDTO disponibilidadeRequestDTO);
    DisponibilidadeResponseDTO getDisponibilidadeById(Long id);
    Page<DisponibilidadeResponseDTO> getAllDisponibilidades(Pageable pageable);
    Page<DisponibilidadeResponseDTO> getDisponibilidadesByProfissional(Long profissionalId, Pageable pageable);
    DisponibilidadeResponseDTO updateDisponibilidade(Long id, DisponibilidadeRequestDTO disponibilidadeRequestDTO);
    void deleteDisponibilidade(Long id);
    
    // Método para gerar slots de horários disponíveis
    List<TimeSlotDTO> generateAvailableSlots(
            Long profissionalId,
            Long servicoId,
            LocalDate date
    );
} 