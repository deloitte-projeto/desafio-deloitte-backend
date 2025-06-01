package com.projeto.deloitte.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalTime;

import com.projeto.deloitte.enums.DiaDaSemana;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Disponibilidade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profissional_id", nullable = false)
    private User profissional;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiaDaSemana diaDaSemana;

    private LocalTime horaInicio;
    private LocalTime horaFim;
} 