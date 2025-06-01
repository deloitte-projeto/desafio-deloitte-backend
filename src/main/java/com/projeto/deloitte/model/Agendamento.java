package com.projeto.deloitte.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import com.projeto.deloitte.enums.StatusAgendamento;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Agendamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private User cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profissional_id", nullable = false)
    private User profissional;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servico_id", nullable = false)
    private Servico servico;

    private LocalDateTime dataHoraInicio;
    private LocalDateTime dataHoraFim;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAgendamento status;
} 