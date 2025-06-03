package com.projeto.deloitte.repository;

import com.projeto.deloitte.model.Disponibilidade;
import com.projeto.deloitte.model.User;
import com.projeto.deloitte.enums.DiaDaSemana;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DisponibilidadeRepository extends JpaRepository<Disponibilidade, Long> {
    List<Disponibilidade> findByProfissional(User profissional);
    List<Disponibilidade> findByProfissionalAndDiaDaSemana(User profissional, DiaDaSemana diaDaSemana);
} 