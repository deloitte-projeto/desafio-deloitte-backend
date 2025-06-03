package com.projeto.deloitte.repository;

import com.projeto.deloitte.enums.TipoUsuario;
import com.projeto.deloitte.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByTipoUsuario(TipoUsuario tipoUsuario);
} 