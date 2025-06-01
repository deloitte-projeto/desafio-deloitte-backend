package com.projeto.deloitte.mapper;

import com.projeto.deloitte.dto.ServicoRequestDTO;
import com.projeto.deloitte.dto.ServicoResponseDTO;
import com.projeto.deloitte.model.Servico;
import com.projeto.deloitte.model.User;

public class ServicoMapper {

    public static Servico toEntity(ServicoRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        Servico servico = new Servico();
        servico.setNome(dto.getNome());
        servico.setDescricao(dto.getDescricao());
        servico.setDuracaoEmMinutos(dto.getDuracaoEmMinutos());
        // O campo 'profissional' será setado no Service, pois depende do usuário logado.
        return servico;
    }

    public static ServicoResponseDTO toResponseDTO(Servico servico) {
        if (servico == null) {
            return null;
        }
        ServicoResponseDTO dto = new ServicoResponseDTO();
        dto.setId(servico.getId());
        dto.setNome(servico.getNome());
        dto.setDescricao(servico.getDescricao());
        dto.setDuracaoEmMinutos(servico.getDuracaoEmMinutos());
        if (servico.getProfissional() != null) {
            dto.setProfissionalId(servico.getProfissional().getId());
            dto.setProfissionalNome(servico.getProfissional().getNome());
        }
        return dto;
    }
} 