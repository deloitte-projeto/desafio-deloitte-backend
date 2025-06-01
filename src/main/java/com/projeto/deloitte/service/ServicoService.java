package com.projeto.deloitte.service;

import com.projeto.deloitte.dto.ServicoRequestDTO;
import com.projeto.deloitte.dto.ServicoResponseDTO;

import java.util.List;

public interface ServicoService {
    ServicoResponseDTO createServico(ServicoRequestDTO servicoRequestDTO);
    ServicoResponseDTO getServicoById(Long id);
    List<ServicoResponseDTO> getAllServicos();
    ServicoResponseDTO updateServico(Long id, ServicoRequestDTO servicoRequestDTO);
    void deleteServico(Long id);
    List<ServicoResponseDTO> getServicosByProfissional(Long profissionalId);
} 