package com.projeto.deloitte.mapper;

import com.projeto.deloitte.dto.AgendamentoRequestDTO;
import com.projeto.deloitte.dto.AgendamentoResponseDTO;
import com.projeto.deloitte.model.Agendamento;
import com.projeto.deloitte.model.User;
import com.projeto.deloitte.model.Servico;
import com.projeto.deloitte.enums.StatusAgendamento;

public class AgendamentoMapper {

    public static Agendamento toEntity(AgendamentoRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        Agendamento agendamento = new Agendamento();
        agendamento.setDataHoraInicio(dto.getDataHoraInicio());
        // cliente, profissional, servico e dataHoraFim serão setados no Service
        agendamento.setStatus(StatusAgendamento.AGENDADO); // Status inicial
        return agendamento;
    }

    public static AgendamentoResponseDTO toResponseDTO(Agendamento agendamento) {
        if (agendamento == null) {
            return null;
        }
        AgendamentoResponseDTO dto = new AgendamentoResponseDTO();
        dto.setId(agendamento.getId());
        dto.setDataHoraInicio(agendamento.getDataHoraInicio());
        dto.setDataHoraFim(agendamento.getDataHoraFim());
        dto.setStatus(agendamento.getStatus());

        if (agendamento.getCliente() != null) {
            dto.setClienteId(agendamento.getCliente().getId());
            dto.setClienteNome(agendamento.getCliente().getNome());
        }
        if (agendamento.getProfissional() != null) {
            dto.setProfissionalId(agendamento.getProfissional().getId());
            dto.setProfissionalNome(agendamento.getProfissional().getNome());
        }
        if (agendamento.getServico() != null) {
            dto.setServicoId(agendamento.getServico().getId());
            dto.setServicoNome(agendamento.getServico().getNome());
        }
        return dto;
    }
} 