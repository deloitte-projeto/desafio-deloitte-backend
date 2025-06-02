package com.projeto.deloitte.dto;

import com.projeto.deloitte.enums.DiaDaSemana;
import lombok.Data;
import java.time.LocalTime;

@Data
public class DisponibilidadeResponseDTO {
    private Long id;
    private Long profissionalId;
    private String profissionalNome;
    private DiaDaSemana diaDaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFim;
} 