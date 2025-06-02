package com.projeto.deloitte.dto;

import com.projeto.deloitte.enums.StatusAgendamento;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AgendamentoResponseDTO {
    private Long id;
    private Long clienteId;
    private String clienteNome;
    private Long profissionalId;
    private String profissionalNome;
    private Long servicoId;
    private String servicoNome;
    private LocalDateTime dataHoraInicio;
    private LocalDateTime dataHoraFim;
    private StatusAgendamento status;
} 