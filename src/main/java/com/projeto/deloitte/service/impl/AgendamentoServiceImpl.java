package com.projeto.deloitte.service.impl;

import com.projeto.deloitte.dto.AgendamentoRequestDTO;
import com.projeto.deloitte.dto.AgendamentoResponseDTO;
import com.projeto.deloitte.enums.StatusAgendamento;
import com.projeto.deloitte.enums.TipoUsuario;
import com.projeto.deloitte.enums.DiaDaSemana;
import com.projeto.deloitte.exception.ResourceNotFoundException;
import com.projeto.deloitte.exception.UnauthorizedAccessException;
import com.projeto.deloitte.exception.ValidationException;
import com.projeto.deloitte.exception.ScheduleConflictException;
import com.projeto.deloitte.mapper.AgendamentoMapper;
import com.projeto.deloitte.model.Agendamento;
import com.projeto.deloitte.model.Disponibilidade;
import com.projeto.deloitte.model.Servico;
import com.projeto.deloitte.model.User;
import com.projeto.deloitte.repository.AgendamentoRepository;
import com.projeto.deloitte.repository.DisponibilidadeRepository;
import com.projeto.deloitte.repository.ServicoRepository;
import com.projeto.deloitte.repository.UserRepository;
import com.projeto.deloitte.service.AgendamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AgendamentoServiceImpl implements AgendamentoService {

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServicoRepository servicoRepository;

    @Autowired
    private DisponibilidadeRepository disponibilidadeRepository;

    private User getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário logado não encontrado."));
    }

    @Override
    public AgendamentoResponseDTO createAgendamento(AgendamentoRequestDTO agendamentoRequestDTO) {
        User cliente = getCurrentAuthenticatedUser();
        if (cliente.getTipoUsuario() != TipoUsuario.CLIENTE) {
            throw new UnauthorizedAccessException("Apenas CLIENTES podem realizar agendamentos.");
        }

        User profissional = userRepository.findById(agendamentoRequestDTO.getProfissionalId())
                .orElseThrow(() -> new ResourceNotFoundException("Profissional", "ID", agendamentoRequestDTO.getProfissionalId()));

        if (profissional.getTipoUsuario() != TipoUsuario.PROFISSIONAL) {
            throw new ValidationException("O ID fornecido não pertence a um profissional.");
        }

        Servico servico = servicoRepository.findById(agendamentoRequestDTO.getServicoId())
                .orElseThrow(() -> new ResourceNotFoundException("Serviço", "ID", agendamentoRequestDTO.getServicoId()));

        if (!servico.getProfissional().getId().equals(profissional.getId())) {
            throw new ValidationException("O serviço não pertence ao profissional selecionado.");
        }

        LocalDateTime dataHoraInicio = agendamentoRequestDTO.getDataHoraInicio();
        LocalDateTime dataHoraFim = dataHoraInicio.plusMinutes(servico.getDuracaoEmMinutos());

        // Validar disponibilidade do profissional
        DayOfWeek dayOfWeek = dataHoraInicio.getDayOfWeek();
        DiaDaSemana diaDaSemana = mapDayOfWeekToDiaDaSemana(dayOfWeek);

        List<Disponibilidade> disponibilidades = disponibilidadeRepository.findByProfissionalAndDiaDaSemana(
                profissional, diaDaSemana
        );

        boolean isAvailable = disponibilidades.stream().anyMatch(disp ->
                !dataHoraInicio.isBefore(LocalDateTime.of(dataHoraInicio.toLocalDate(), disp.getHoraInicio())) &&
                !dataHoraFim.isAfter(LocalDateTime.of(dataHoraFim.toLocalDate(), disp.getHoraFim()))
        );

        if (!isAvailable) {
            throw new ValidationException("Horário fora da disponibilidade do profissional.");
        }

        // Validar conflitos de agendamento
        List<Agendamento> conflitos = agendamentoRepository.findByProfissionalAndDataHoraInicioBetween(
                profissional,
                dataHoraInicio.toLocalDate().atStartOfDay(),
                dataHoraInicio.toLocalDate().atTime(23, 59, 59)
        ).stream().filter(existingAgendamento ->
                !(dataHoraFim.isBefore(existingAgendamento.getDataHoraInicio()) ||
                  dataHoraInicio.isAfter(existingAgendamento.getDataHoraFim()))
        ).collect(Collectors.toList());

        if (!conflitos.isEmpty()) {
            throw new ScheduleConflictException("Horário já ocupado.");
        }

        Agendamento agendamento = AgendamentoMapper.toEntity(agendamentoRequestDTO);
        agendamento.setCliente(cliente);
        agendamento.setProfissional(profissional);
        agendamento.setServico(servico);
        agendamento.setDataHoraFim(dataHoraFim);
        agendamento.setStatus(StatusAgendamento.AGENDADO);

        Agendamento savedAgendamento = agendamentoRepository.save(agendamento);
        return AgendamentoMapper.toResponseDTO(savedAgendamento);
    }

    @Override
    public Page<AgendamentoResponseDTO> getAgendamentosByCliente(Long clienteId, Pageable pageable) {
        User cliente = userRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "ID", clienteId));

        if (cliente.getTipoUsuario() != TipoUsuario.CLIENTE) {
            throw new ValidationException("O ID fornecido não pertence a um cliente.");
        }

        Page<Agendamento> agendamentoPage = agendamentoRepository.findByCliente(cliente, pageable);
        return agendamentoPage.map(AgendamentoMapper::toResponseDTO);
    }

    @Override
    public AgendamentoResponseDTO cancelAgendamento(Long agendamentoId) {
        Agendamento agendamento = agendamentoRepository.findById(agendamentoId)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento", "ID", agendamentoId));

        User currentUser = getCurrentAuthenticatedUser();

        if (!agendamento.getCliente().getId().equals(currentUser.getId()) &&
            !agendamento.getProfissional().getId().equals(currentUser.getId()) &&
            currentUser.getTipoUsuario() != TipoUsuario.ADMIN) {
            throw new UnauthorizedAccessException("Você não tem permissão para cancelar este agendamento.");
        }

        if (agendamento.getCliente().getId().equals(currentUser.getId())) {
            agendamento.setStatus(StatusAgendamento.CANCELADO_CLIENTE);
        } else if (agendamento.getProfissional().getId().equals(currentUser.getId())) {
            agendamento.setStatus(StatusAgendamento.CANCELADO_PROFISSIONAL);
        } else if (currentUser.getTipoUsuario() == TipoUsuario.ADMIN) {
             agendamento.setStatus(StatusAgendamento.CANCELADO_PROFISSIONAL);
        }

        Agendamento canceledAgendamento = agendamentoRepository.save(agendamento);
        return AgendamentoMapper.toResponseDTO(canceledAgendamento);
    }

    @Override
    public Page<AgendamentoResponseDTO> getAgendaProfissional(
            Long profissionalId,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    ) {
        User profissional = userRepository.findById(profissionalId)
                .orElseThrow(() -> new ResourceNotFoundException("Profissional", "ID", profissionalId));

        if (profissional.getTipoUsuario() != TipoUsuario.PROFISSIONAL) {
            throw new ValidationException("O ID fornecido não pertence a um profissional.");
        }

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        Page<Agendamento> agendamentoPage = agendamentoRepository.findByProfissionalAndDataHoraInicioBetween(
                profissional, startDateTime, endDateTime, pageable
        );
        return agendamentoPage.map(AgendamentoMapper::toResponseDTO);
    }

    @Override
    public AgendamentoResponseDTO completeAgendamento(Long agendamentoId) {
        Agendamento agendamento = agendamentoRepository.findById(agendamentoId)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento", "ID", agendamentoId));

        User currentUser = getCurrentAuthenticatedUser();

        if (!agendamento.getProfissional().getId().equals(currentUser.getId()) &&
            currentUser.getTipoUsuario() != TipoUsuario.ADMIN) {
            throw new UnauthorizedAccessException("Você não tem permissão para concluir este agendamento.");
        }

        if (agendamento.getStatus() != StatusAgendamento.AGENDADO) {
            throw new ValidationException("Apenas agendamentos com status AGENDADO podem ser concluídos.");
        }

        agendamento.setStatus(StatusAgendamento.CONCLUIDO);
        Agendamento completedAgendamento = agendamentoRepository.save(agendamento);
        return AgendamentoMapper.toResponseDTO(completedAgendamento);
    }

    private DiaDaSemana mapDayOfWeekToDiaDaSemana(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> DiaDaSemana.MONDAY;
            case TUESDAY -> DiaDaSemana.TUESDAY;
            case WEDNESDAY -> DiaDaSemana.WEDNESDAY;
            case THURSDAY -> DiaDaSemana.THURSDAY;
            case FRIDAY -> DiaDaSemana.FRIDAY;
            case SATURDAY -> DiaDaSemana.SATURDAY;
            case SUNDAY -> DiaDaSemana.SUNDAY;
        };
    }
} 