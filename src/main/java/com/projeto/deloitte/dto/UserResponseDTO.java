package com.projeto.deloitte.dto;

import com.projeto.deloitte.enums.TipoUsuario;
import lombok.Data;

@Data
public class UserResponseDTO {
    private Long id;
    private String nome;
    private String email;
    private TipoUsuario tipoUsuario;
} 