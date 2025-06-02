package com.projeto.deloitte.repository;

import com.projeto.deloitte.model.Agendamento;
import com.projeto.deloitte.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {
    List<Agendamento> findByProfissionalAndDataHoraInicioBetween(User profissional, LocalDateTime startOfDay, LocalDateTime endOfDay);
} 