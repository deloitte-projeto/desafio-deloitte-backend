package com.projeto.deloitte.service.impl;

import com.projeto.deloitte.dto.DisponibilidadeRequestDTO;
import com.projeto.deloitte.dto.DisponibilidadeResponseDTO;
import com.projeto.deloitte.dto.TimeSlotDTO;
import com.projeto.deloitte.enums.DiaDaSemana;
import com.projeto.deloitte.enums.TipoUsuario;
import com.projeto.deloitte.exception.AvailabilityConflictException;
import com.projeto.deloitte.exception.ResourceNotFoundException;
import com.projeto.deloitte.exception.UnauthorizedAccessException;
import com.projeto.deloitte.exception.ValidationException;
import com.projeto.deloitte.mapper.DisponibilidadeMapper;
import com.projeto.deloitte.model.Agendamento;
import com.projeto.deloitte.model.Disponibilidade;
import com.projeto.deloitte.model.Servico;
import com.projeto.deloitte.model.User;
import com.projeto.deloitte.repository.AgendamentoRepository;
import com.projeto.deloitte.repository.DisponibilidadeRepository;
import com.projeto.deloitte.repository.ServicoRepository;
import com.projeto.deloitte.repository.UserRepository;
import com.projeto.deloitte.service.DisponibilidadeService;
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
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;


@Service
public class DisponibilidadeServiceImpl implements DisponibilidadeService {

    @Autowired
    private DisponibilidadeRepository disponibilidadeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServicoRepository servicoRepository;

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    // Helper method to get the current authenticated user
    private User getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário logado não encontrado."));
    }

    // Método auxiliar para verificar sobreposição de horários de disponibilidade
    private void checkDisponibilidadeOverlap(User profissional, DiaDaSemana diaDaSemana, LocalTime horaInicio, LocalTime horaFim, Long currentDisponibilidadeId) {
        List<Disponibilidade> existingDisponibilidades = disponibilidadeRepository.findByProfissionalAndDiaDaSemana(
                profissional, diaDaSemana
        );

        for (Disponibilidade disp : existingDisponibilidades) {
            // Ignora a própria disponibilidade ao atualizar
            if (currentDisponibilidadeId != null && Objects.equals(disp.getId(), currentDisponibilidadeId)) {
                continue;
            }

            // Verifica se os novos horários se sobrepõem aos existentes
            // Critério de sobreposição: (Inicio1 < Fim2) AND (Fim1 > Inicio2)
            if (horaInicio.isBefore(disp.getHoraFim()) && horaFim.isAfter(disp.getHoraInicio())) {
                throw new AvailabilityConflictException("Já existe um bloco de disponibilidade para este dia que se sobrepõe ao horário informado.");
            }
        }
    }

    @Override
    public DisponibilidadeResponseDTO createDisponibilidade(DisponibilidadeRequestDTO disponibilidadeRequestDTO) {
        User currentUser = getCurrentAuthenticatedUser();
        if (currentUser.getTipoUsuario() != TipoUsuario.PROFISSIONAL) {
            throw new UnauthorizedAccessException("Apenas PROFISSIONAIS podem cadastrar disponibilidade.");
        }

        checkDisponibilidadeOverlap(
                currentUser,
                disponibilidadeRequestDTO.getDiaDaSemana(),
                disponibilidadeRequestDTO.getHoraInicio(),
                disponibilidadeRequestDTO.getHoraFim(),
                null // Não há ID de disponibilidade existente para criação
        );

        Disponibilidade disponibilidade = DisponibilidadeMapper.toEntity(disponibilidadeRequestDTO);
        disponibilidade.setProfissional(currentUser);

        Disponibilidade savedDisponibilidade = disponibilidadeRepository.save(disponibilidade);
        return DisponibilidadeMapper.toResponseDTO(savedDisponibilidade);
    }

    @Override
    public DisponibilidadeResponseDTO getDisponibilidadeById(Long id) {
        Disponibilidade disponibilidade = disponibilidadeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disponibilidade", "ID", id));
        return DisponibilidadeMapper.toResponseDTO(disponibilidade);
    }

    @Override
    public Page<DisponibilidadeResponseDTO> getAllDisponibilidades(Pageable pageable) {
        Page<Disponibilidade> disponibilidadesPage = disponibilidadeRepository.findAll(pageable);
        return disponibilidadesPage.map(DisponibilidadeMapper::toResponseDTO);
    }

    @Override
    public Page<DisponibilidadeResponseDTO> getDisponibilidadesByProfissional(Long profissionalId, Pageable pageable) {
        User profissional = userRepository.findById(profissionalId)
                .orElseThrow(() -> new ResourceNotFoundException("Profissional", "ID", profissionalId));

        if (profissional.getTipoUsuario() != TipoUsuario.PROFISSIONAL) {
            throw new ValidationException("O ID fornecido não pertence a um profissional.");
        } 
        Page<Disponibilidade> disponibilidadesPage = disponibilidadeRepository.findByProfissional(profissional, pageable);
        return disponibilidadesPage.map(DisponibilidadeMapper::toResponseDTO);
    }

    @Override
    public DisponibilidadeResponseDTO updateDisponibilidade(Long id, DisponibilidadeRequestDTO disponibilidadeRequestDTO) {
        User currentUser = getCurrentAuthenticatedUser();

        Disponibilidade existingDisponibilidade = disponibilidadeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disponibilidade", "ID", id));

        // Validar se o profissional logado é o dono da disponibilidade ou um ADMIN
        if (!existingDisponibilidade.getProfissional().getId().equals(currentUser.getId()) &&
            currentUser.getTipoUsuario() != TipoUsuario.ADMIN) {
            throw new UnauthorizedAccessException("Você não tem permissão para atualizar esta disponibilidade.");
        }

        // Adicionar validação para não haver sobreposição de horários de disponibilidade para o mesmo profissional e dia
        checkDisponibilidadeOverlap(
                currentUser,
                disponibilidadeRequestDTO.getDiaDaSemana(),
                disponibilidadeRequestDTO.getHoraInicio(),
                disponibilidadeRequestDTO.getHoraFim(),
                id // Passa o ID da disponibilidade que está sendo atualizada para ignorá-la na validação
        );

        existingDisponibilidade.setDiaDaSemana(disponibilidadeRequestDTO.getDiaDaSemana());
        existingDisponibilidade.setHoraInicio(disponibilidadeRequestDTO.getHoraInicio());
        existingDisponibilidade.setHoraFim(disponibilidadeRequestDTO.getHoraFim());

        Disponibilidade updatedDisponibilidade = disponibilidadeRepository.save(existingDisponibilidade);
        return DisponibilidadeMapper.toResponseDTO(updatedDisponibilidade);
    }

    @Override
    public void deleteDisponibilidade(Long id) {
        User currentUser = getCurrentAuthenticatedUser();

        Disponibilidade disponibilidadeToDelete = disponibilidadeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disponibilidade", "ID", id));

        if (!disponibilidadeToDelete.getProfissional().getId().equals(currentUser.getId()) &&
            currentUser.getTipoUsuario() != TipoUsuario.ADMIN) {
            throw new UnauthorizedAccessException("Você não tem permissão para deletar esta disponibilidade.");
        }

        disponibilidadeRepository.delete(disponibilidadeToDelete);
    }

    @Override
    public List<TimeSlotDTO> generateAvailableSlots(
            Long profissionalId,
            Long servicoId,
            LocalDate date
    ) {
        User profissional = userRepository.findById(profissionalId)
                .orElseThrow(() -> new ResourceNotFoundException("Profissional", "ID", profissionalId));

        if (profissional.getTipoUsuario() != TipoUsuario.PROFISSIONAL) {
            throw new ValidationException("O ID fornecido não pertence a um profissional.");
        }

        Servico servico = servicoRepository.findById(servicoId)
                .orElseThrow(() -> new ResourceNotFoundException("Serviço", "ID", servicoId));

        if (!servico.getProfissional().getId().equals(profissionalId)) {
            throw new ValidationException("O serviço não pertence a este profissional.");
        }

        DayOfWeek dayOfWeek = date.getDayOfWeek();
        DiaDaSemana diaDaSemana = DiaDaSemana.valueOf(dayOfWeek.name());

        List<Disponibilidade> disponibilidadesDoDia = disponibilidadeRepository.findByProfissionalAndDiaDaSemana(
                profissional,
                diaDaSemana
        );

        List<Agendamento> agendamentosDoDia = agendamentoRepository.findByProfissionalAndDataHoraInicioBetween(
                profissional,
                date.atStartOfDay(),
                date.atTime(LocalTime.MAX)
        );

        List<TimeSlotDTO> availableSlots = new ArrayList<>();
        int duracaoServico = servico.getDuracaoEmMinutos();

        for (Disponibilidade disp : disponibilidadesDoDia) {
            LocalTime currentSlotTime = disp.getHoraInicio();
            while (currentSlotTime.plusMinutes(duracaoServico).isBefore(disp.getHoraFim()) ||
                   currentSlotTime.plusMinutes(duracaoServico).equals(disp.getHoraFim())) {

                LocalDateTime slotStartDateTime = LocalDateTime.of(date, currentSlotTime);
                LocalDateTime slotEndDateTime = slotStartDateTime.plusMinutes(duracaoServico);

                boolean isConflict = false;
                for (Agendamento agendamento : agendamentosDoDia) {
                    if (!(slotEndDateTime.isBefore(agendamento.getDataHoraInicio()) ||
                            slotStartDateTime.isAfter(agendamento.getDataHoraFim()))) {
                        isConflict = true;
                        break;
                    }
                }

                if (!isConflict) {
                    availableSlots.add(new TimeSlotDTO(slotStartDateTime, slotEndDateTime));
                }
                currentSlotTime = currentSlotTime.plusMinutes(duracaoServico);
            }
        }
        availableSlots.sort(Comparator.comparing(TimeSlotDTO::getStartTime));
        return availableSlots;
    }
} 