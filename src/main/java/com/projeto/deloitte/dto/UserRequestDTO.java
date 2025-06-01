package com.projeto.deloitte.dto;

import com.projeto.deloitte.enums.TipoUsuario;
import lombok.Data;

@Data
public class UserRequestDTO {
    private String nome;
    private String email;
    private String senha;
    private TipoUsuario tipoUsuario;
} 