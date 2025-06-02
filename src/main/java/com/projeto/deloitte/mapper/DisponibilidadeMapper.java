package com.projeto.deloitte.mapper;

import com.projeto.deloitte.dto.DisponibilidadeRequestDTO;
import com.projeto.deloitte.dto.DisponibilidadeResponseDTO;
import com.projeto.deloitte.model.Disponibilidade;
import com.projeto.deloitte.model.User;

public class DisponibilidadeMapper {

    public static Disponibilidade toEntity(DisponibilidadeRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        Disponibilidade disponibilidade = new Disponibilidade();
        disponibilidade.setDiaDaSemana(dto.getDiaDaSemana());
        disponibilidade.setHoraInicio(dto.getHoraInicio());
        disponibilidade.setHoraFim(dto.getHoraFim());
        // O campo 'profissional' será setado no Service, pois depende do usuário logado.
        return disponibilidade;
    }

    public static DisponibilidadeResponseDTO toResponseDTO(Disponibilidade disponibilidade) {
        if (disponibilidade == null) {
            return null;
        }
        DisponibilidadeResponseDTO dto = new DisponibilidadeResponseDTO();
        dto.setId(disponibilidade.getId());
        dto.setDiaDaSemana(disponibilidade.getDiaDaSemana());
        dto.setHoraInicio(disponibilidade.getHoraInicio());
        dto.setHoraFim(disponibilidade.getHoraFim());
        if (disponibilidade.getProfissional() != null) {
            dto.setProfissionalId(disponibilidade.getProfissional().getId());
            dto.setProfissionalNome(disponibilidade.getProfissional().getNome());
        }
        return dto;
    }
} 