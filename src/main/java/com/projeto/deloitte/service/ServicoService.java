package com.projeto.deloitte.service;

import com.projeto.deloitte.dto.ServicoRequestDTO;
import com.projeto.deloitte.dto.ServicoResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;



public interface ServicoService {
    ServicoResponseDTO createServico(ServicoRequestDTO servicoRequestDTO);
    ServicoResponseDTO getServicoById(Long id);
    Page<ServicoResponseDTO> getAllServicos(Pageable pageable);
    ServicoResponseDTO updateServico(Long id, ServicoRequestDTO servicoRequestDTO);
    void deleteServico(Long id);
    Page<ServicoResponseDTO> getServicosByProfissional(Long profissionalId, Pageable pageable);
} 