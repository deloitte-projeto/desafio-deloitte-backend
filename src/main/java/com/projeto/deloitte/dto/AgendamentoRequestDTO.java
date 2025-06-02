package com.projeto.deloitte.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class AgendamentoRequestDTO {
    @NotNull
    private Long profissionalId;
    @NotNull
    private Long servicoId;
    @NotNull
    private LocalDateTime dataHoraInicio;
} 