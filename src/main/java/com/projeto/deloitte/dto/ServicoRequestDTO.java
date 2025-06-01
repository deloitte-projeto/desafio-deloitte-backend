package com.projeto.deloitte.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Data
public class ServicoRequestDTO {
    @NotBlank
    private String nome;
    private String descricao;
    @NotNull
    @Positive
    private Integer duracaoEmMinutos;
} 