package com.projeto.deloitte.dto;

import com.projeto.deloitte.enums.DiaDaSemana;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

@Data
public class DisponibilidadeRequestDTO {
    @NotNull
    private DiaDaSemana diaDaSemana;
    @NotNull
    private LocalTime horaInicio;
    @NotNull
    private LocalTime horaFim;
} 