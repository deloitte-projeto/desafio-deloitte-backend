package com.projeto.deloitte.dto;

import lombok.Data;

@Data
public class ServicoResponseDTO {
    private Long id;
    private String nome;
    private String descricao;
    private Integer duracaoEmMinutos;
    private Long profissionalId;
    private String profissionalNome;
} 