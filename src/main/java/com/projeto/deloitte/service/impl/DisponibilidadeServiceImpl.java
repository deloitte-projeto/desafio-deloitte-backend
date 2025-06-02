package com.projeto.deloitte.service.impl;

import com.projeto.deloitte.dto.DisponibilidadeRequestDTO;
import com.projeto.deloitte.dto.DisponibilidadeResponseDTO;
import com.projeto.deloitte.dto.TimeSlotDTO;
import com.projeto.deloitte.enums.DiaDaSemana;
import com.projeto.deloitte.enums.TipoUsuario;
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
import java.util.stream.Collectors;

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

    @Override
    public DisponibilidadeResponseDTO createDisponibilidade(DisponibilidadeRequestDTO disponibilidadeRequestDTO) {
        User currentUser = getCurrentAuthenticatedUser();
        if (currentUser.getTipoUsuario() != TipoUsuario.PROFISSIONAL) {
            throw new RuntimeException("Apenas PROFISSIONAIS podem cadastrar disponibilidade."); // TODO: Exceção customizada
        }

        Disponibilidade disponibilidade = DisponibilidadeMapper.toEntity(disponibilidadeRequestDTO);
        disponibilidade.setProfissional(currentUser);

        // TODO: Adicionar validação para não haver sobreposição de horários de disponibilidade para o mesmo profissional e dia

        Disponibilidade savedDisponibilidade = disponibilidadeRepository.save(disponibilidade);
        return DisponibilidadeMapper.toResponseDTO(savedDisponibilidade);
    }

    @Override
    public DisponibilidadeResponseDTO getDisponibilidadeById(Long id) {
        Disponibilidade disponibilidade = disponibilidadeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Disponibilidade não encontrada com o ID: " + id)); // TODO: Exceção customizada
        return DisponibilidadeMapper.toResponseDTO(disponibilidade);
    }

    @Override
    public List<DisponibilidadeResponseDTO> getAllDisponibilidades() {
        return disponibilidadeRepository.findAll().stream()
                .map(DisponibilidadeMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DisponibilidadeResponseDTO> getDisponibilidadesByProfissional(Long profissionalId) {
        User profissional = userRepository.findById(profissionalId)
                .orElseThrow(() -> new UsernameNotFoundException("Profissional não encontrado com o ID: " + profissionalId));

        if (profissional.getTipoUsuario() != TipoUsuario.PROFISSIONAL) {
            throw new RuntimeException("O ID fornecido não pertence a um profissional."); // TODO: Exceção customizada
        } 
        return disponibilidadeRepository.findByProfissional(profissional).stream()
                .map(DisponibilidadeMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public DisponibilidadeResponseDTO updateDisponibilidade(Long id, DisponibilidadeRequestDTO disponibilidadeRequestDTO) {
        User currentUser = getCurrentAuthenticatedUser();

        Disponibilidade existingDisponibilidade = disponibilidadeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Disponibilidade não encontrada com o ID: " + id)); // TODO: Exceção customizada

        // Validar se o profissional logado é o dono da disponibilidade ou um ADMIN
        if (!existingDisponibilidade.getProfissional().getId().equals(currentUser.getId()) &&
            currentUser.getTipoUsuario() != TipoUsuario.ADMIN) {
            throw new RuntimeException("Você não tem permissão para atualizar esta disponibilidade."); // TODO: Exceção customizada
        }

        // TODO: Adicionar validação para não haver sobreposição de horários de disponibilidade para o mesmo profissional e dia

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
                .orElseThrow(() -> new RuntimeException("Disponibilidade não encontrada com o ID: " + id)); // TODO: Exceção customizada

        // Validar se o profissional logado é o dono da disponibilidade ou um ADMIN
        if (!disponibilidadeToDelete.getProfissional().getId().equals(currentUser.getId()) &&
            currentUser.getTipoUsuario() != TipoUsuario.ADMIN) {
            throw new RuntimeException("Você não tem permissão para deletar esta disponibilidade."); // TODO: Exceção customizada
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
                .orElseThrow(() -> new UsernameNotFoundException("Profissional não encontrado."));

        if (profissional.getTipoUsuario() != TipoUsuario.PROFISSIONAL) {
            throw new RuntimeException("O ID fornecido não pertence a um profissional."); // TODO: Exceção customizada
        }

        Servico servico = servicoRepository.findById(servicoId)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado.")); // TODO: Exceção customizada

        // Validar se o serviço pertence ao profissional
        if (!servico.getProfissional().getId().equals(profissionalId)) {
            throw new RuntimeException("O serviço não pertence a este profissional."); // TODO: Exceção customizada
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
                    // Verifica sobreposição de horários
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
        // Opcional: Ordenar os slots por horário de início
        availableSlots.sort(Comparator.comparing(TimeSlotDTO::getStartTime));
        return availableSlots;
    }
} 