package com.projeto.deloitte.service.impl;

import com.projeto.deloitte.dto.AgendamentoRequestDTO;
import com.projeto.deloitte.dto.AgendamentoResponseDTO;
import com.projeto.deloitte.enums.StatusAgendamento;
import com.projeto.deloitte.enums.TipoUsuario;
import com.projeto.deloitte.enums.DiaDaSemana;
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
            throw new RuntimeException("Apenas CLIENTES podem realizar agendamentos."); // TODO: Exceção customizada
        }

        User profissional = userRepository.findById(agendamentoRequestDTO.getProfissionalId())
                .orElseThrow(() -> new RuntimeException("Profissional não encontrado.")); // TODO: Exceção customizada

        if (profissional.getTipoUsuario() != TipoUsuario.PROFISSIONAL) {
            throw new RuntimeException("O ID fornecido não pertence a um profissional."); // TODO: Exceção customizada
        }

        Servico servico = servicoRepository.findById(agendamentoRequestDTO.getServicoId())
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado.")); // TODO: Exceção customizada

        if (!servico.getProfissional().getId().equals(profissional.getId())) {
            throw new RuntimeException("O serviço não pertence ao profissional selecionado."); // TODO: Exceção customizada
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
            throw new RuntimeException("Horário fora da disponibilidade do profissional."); // TODO: Exceção customizada
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
            throw new RuntimeException("Horário já ocupado."); // TODO: Exceção customizada
        }

        Agendamento agendamento = AgendamentoMapper.toEntity(agendamentoRequestDTO);
        agendamento.setCliente(cliente);
        agendamento.setProfissional(profissional);
        agendamento.setServico(servico);
        agendamento.setDataHoraFim(dataHoraFim);

        Agendamento savedAgendamento = agendamentoRepository.save(agendamento);
        return AgendamentoMapper.toResponseDTO(savedAgendamento);
    }

    @Override
    public List<AgendamentoResponseDTO> getAgendamentosByCliente(Long clienteId) {
        User cliente = userRepository.findById(clienteId)
                .orElseThrow(() -> new UsernameNotFoundException("Cliente não encontrado.")); // TODO: Exceção customizada

        if (cliente.getTipoUsuario() != TipoUsuario.CLIENTE) {
            throw new RuntimeException("O ID fornecido não pertence a um cliente."); // TODO: Exceção customizada
        }
        
        // TODO: Adicionar validação para que apenas o cliente logado ou ADMIN possa ver seus agendamentos

        return agendamentoRepository.findByCliente(cliente).stream()
                .map(AgendamentoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AgendamentoResponseDTO cancelAgendamento(Long agendamentoId) {
        Agendamento agendamento = agendamentoRepository.findById(agendamentoId)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado.")); // TODO: Exceção customizada

        User currentUser = getCurrentAuthenticatedUser();

        // Validar se o usuário logado é o cliente do agendamento, o profissional ou um ADMIN
        if (!agendamento.getCliente().getId().equals(currentUser.getId()) &&
            !agendamento.getProfissional().getId().equals(currentUser.getId()) &&
            currentUser.getTipoUsuario() != TipoUsuario.ADMIN) {
            throw new RuntimeException("Você não tem permissão para cancelar este agendamento."); // TODO: Exceção customizada
        }

        // TODO: Implementar regra de antecedência mínima para cancelamento

        if (agendamento.getCliente().getId().equals(currentUser.getId())) {
            agendamento.setStatus(StatusAgendamento.CANCELADO_CLIENTE);
        } else if (agendamento.getProfissional().getId().equals(currentUser.getId())) {
            agendamento.setStatus(StatusAgendamento.CANCELADO_PROFISSIONAL);
        } else if (currentUser.getTipoUsuario() == TipoUsuario.ADMIN) {
             agendamento.setStatus(StatusAgendamento.CANCELADO_PROFISSIONAL); // Ou criar um status CANCELADO_ADMIN
        }

        Agendamento canceledAgendamento = agendamentoRepository.save(agendamento);
        return AgendamentoMapper.toResponseDTO(canceledAgendamento);
    }

    @Override
    public List<AgendamentoResponseDTO> getAgendaProfissional(
            Long profissionalId,
            LocalDate startDate,
            LocalDate endDate
    ) {
        User profissional = userRepository.findById(profissionalId)
                .orElseThrow(() -> new UsernameNotFoundException("Profissional não encontrado.")); // TODO: Exceção customizada

        if (profissional.getTipoUsuario() != TipoUsuario.PROFISSIONAL) {
            throw new RuntimeException("O ID fornecido não pertence a um profissional."); // TODO: Exceção customizada
        }

        // TODO: Adicionar validação para que apenas o profissional logado ou ADMIN possa ver sua agenda

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        return agendamentoRepository.findByProfissionalAndDataHoraInicioBetween(
                profissional, startDateTime, endDateTime
        ).stream()
                .map(AgendamentoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AgendamentoResponseDTO completeAgendamento(Long agendamentoId) {
        Agendamento agendamento = agendamentoRepository.findById(agendamentoId)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado.")); // TODO: Exceção customizada

        User currentUser = getCurrentAuthenticatedUser();

        // Validar se o usuário logado é o profissional do agendamento ou um ADMIN
        if (!agendamento.getProfissional().getId().equals(currentUser.getId()) &&
            currentUser.getTipoUsuario() != TipoUsuario.ADMIN) {
            throw new RuntimeException("Você não tem permissão para concluir este agendamento."); // TODO: Exceção customizada
        }

        if (agendamento.getStatus() != StatusAgendamento.AGENDADO) {
            throw new RuntimeException("Apenas agendamentos com status AGENDADO podem ser concluídos."); // TODO: Exceção customizada
        }

        agendamento.setStatus(StatusAgendamento.CONCLUIDO);
        Agendamento completedAgendamento = agendamentoRepository.save(agendamento);
        return AgendamentoMapper.toResponseDTO(completedAgendamento);
    }

    // Método auxiliar para mapear DayOfWeek para DiaDaSemana
    private DiaDaSemana mapDayOfWeekToDiaDaSemana(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> DiaDaSemana.SEGUNDA;
            case TUESDAY -> DiaDaSemana.TERCA;
            case WEDNESDAY -> DiaDaSemana.QUARTA;
            case THURSDAY -> DiaDaSemana.QUINTA;
            case FRIDAY -> DiaDaSemana.SEXTA;
            case SATURDAY -> DiaDaSemana.SABADO;
            case SUNDAY -> DiaDaSemana.DOMINGO;
        };
    }
} 