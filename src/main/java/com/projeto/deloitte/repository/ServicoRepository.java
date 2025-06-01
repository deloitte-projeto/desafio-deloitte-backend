package com.projeto.deloitte.repository;

import com.projeto.deloitte.model.Servico;
import com.projeto.deloitte.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServicoRepository extends JpaRepository<Servico, Long> {
    List<Servico> findByProfissional(User profissional);
} 