package com.projeto.deloitte.service.impl;

import com.projeto.deloitte.dto.ServicoRequestDTO;
import com.projeto.deloitte.dto.ServicoResponseDTO;
import com.projeto.deloitte.enums.TipoUsuario;
import com.projeto.deloitte.mapper.ServicoMapper;
import com.projeto.deloitte.model.Servico;
import com.projeto.deloitte.model.User;
import com.projeto.deloitte.repository.ServicoRepository;
import com.projeto.deloitte.repository.UserRepository;
import com.projeto.deloitte.service.ServicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServicoServiceImpl implements ServicoService {

    @Autowired
    private ServicoRepository servicoRepository;

    @Autowired
    private UserRepository userRepository;

    private User getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário logado não encontrado."));
    }

    @Override
    public ServicoResponseDTO createServico(ServicoRequestDTO servicoRequestDTO) {
        User currentUser = getCurrentAuthenticatedUser();
        if (currentUser.getTipoUsuario() != TipoUsuario.PROFISSIONAL) {
            throw new RuntimeException("Apenas PROFISSIONAIS podem cadastrar serviços."); // TODO: Criar exceção customizada
        }

        Servico servico = ServicoMapper.toEntity(servicoRequestDTO);
        servico.setProfissional(currentUser);

        Servico savedServico = servicoRepository.save(servico);
        return ServicoMapper.toResponseDTO(savedServico);
    }

    @Override
    public ServicoResponseDTO getServicoById(Long id) {
        Servico servico = servicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado com o ID: " + id)); // TODO: Exceção customizada
        return ServicoMapper.toResponseDTO(servico);
    }

    @Override
    public List<ServicoResponseDTO> getAllServicos() {
        return servicoRepository.findAll().stream()
                .map(ServicoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ServicoResponseDTO updateServico(Long id, ServicoRequestDTO servicoRequestDTO) {
        User currentUser = getCurrentAuthenticatedUser();

        Servico existingServico = servicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado com o ID: " + id)); // TODO: Exceção customizada

        // Validar se o profissional logado é o dono do serviço ou um ADMIN
        if (!existingServico.getProfissional().getId().equals(currentUser.getId()) &&
            currentUser.getTipoUsuario() != TipoUsuario.ADMIN) { // TODO: Adicionar ROLE_ADMIN
            throw new RuntimeException("Você não tem permissão para atualizar este serviço."); // TODO: Exceção customizada
        }

        existingServico.setNome(servicoRequestDTO.getNome());
        existingServico.setDescricao(servicoRequestDTO.getDescricao());
        existingServico.setDuracaoEmMinutos(servicoRequestDTO.getDuracaoEmMinutos());

        Servico updatedServico = servicoRepository.save(existingServico);
        return ServicoMapper.toResponseDTO(updatedServico);
    }

    @Override
    public void deleteServico(Long id) {
        User currentUser = getCurrentAuthenticatedUser();

        Servico servicoToDelete = servicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado com o ID: " + id)); // TODO: Exceção customizada

        // Validar se o profissional logado é o dono do serviço ou um ADMIN
        if (!servicoToDelete.getProfissional().getId().equals(currentUser.getId()) &&
            currentUser.getTipoUsuario() != TipoUsuario.ADMIN) { // TODO: Adicionar ROLE_ADMIN
            throw new RuntimeException("Você não tem permissão para deletar este serviço."); // TODO: Exceção customizada
        }

        servicoRepository.delete(servicoToDelete);
    }

    @Override
    public List<ServicoResponseDTO> getServicosByProfissional(Long profissionalId) {
        User profissional = userRepository.findById(profissionalId)
                .orElseThrow(() -> new UsernameNotFoundException("Profissional não encontrado com o ID: " + profissionalId));

        if (profissional.getTipoUsuario() != TipoUsuario.PROFISSIONAL) {
            throw new RuntimeException("O ID fornecido não pertence a um profissional."); // TODO: Exceção customizada
        }

        return servicoRepository.findByProfissional(profissional).stream()
                .map(ServicoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
} 