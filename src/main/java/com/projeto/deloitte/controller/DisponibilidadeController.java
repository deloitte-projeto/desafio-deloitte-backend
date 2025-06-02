package com.projeto.deloitte.controller;

import com.projeto.deloitte.dto.DisponibilidadeRequestDTO;
import com.projeto.deloitte.dto.DisponibilidadeResponseDTO;
import com.projeto.deloitte.dto.TimeSlotDTO;
import com.projeto.deloitte.service.DisponibilidadeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/disponibilidades")
public class DisponibilidadeController {

    @Autowired
    private DisponibilidadeService disponibilidadeService;

    @PreAuthorize("hasRole('PROFISSIONAL')")
    @PostMapping
    public ResponseEntity<DisponibilidadeResponseDTO> createDisponibilidade(@Valid @RequestBody DisponibilidadeRequestDTO disponibilidadeRequestDTO) {
        DisponibilidadeResponseDTO createdDisponibilidade = disponibilidadeService.createDisponibilidade(disponibilidadeRequestDTO);
        return new ResponseEntity<>(createdDisponibilidade, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DisponibilidadeResponseDTO> getDisponibilidadeById(@PathVariable Long id) {
        DisponibilidadeResponseDTO disponibilidade = disponibilidadeService.getDisponibilidadeById(id);
        return ResponseEntity.ok(disponibilidade);
    }

    @GetMapping
    public ResponseEntity<List<DisponibilidadeResponseDTO>> getAllDisponibilidades() {
        List<DisponibilidadeResponseDTO> disponibilidades = disponibilidadeService.getAllDisponibilidades();
        return ResponseEntity.ok(disponibilidades);
    }

    @GetMapping("/profissional/{profissionalId}")
    public ResponseEntity<List<DisponibilidadeResponseDTO>> getDisponibilidadesByProfissional(@PathVariable Long profissionalId) {
        List<DisponibilidadeResponseDTO> disponibilidades = disponibilidadeService.getDisponibilidadesByProfissional(profissionalId);
        return ResponseEntity.ok(disponibilidades);
    }

    @PreAuthorize("hasRole('PROFISSIONAL')")
    @PutMapping("/{id}")
    public ResponseEntity<DisponibilidadeResponseDTO> updateDisponibilidade(@PathVariable Long id, @Valid @RequestBody DisponibilidadeRequestDTO disponibilidadeRequestDTO) {
        DisponibilidadeResponseDTO updatedDisponibilidade = disponibilidadeService.updateDisponibilidade(id, disponibilidadeRequestDTO);
        return ResponseEntity.ok(updatedDisponibilidade);
    }

    @PreAuthorize("hasRole('PROFISSIONAL')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDisponibilidade(@PathVariable Long id) {
        disponibilidadeService.deleteDisponibilidade(id);
        return new ResponseEntity<>("Disponibilidade deletada com sucesso!", HttpStatus.OK);
    }

    @GetMapping("/slots")
    public ResponseEntity<List<TimeSlotDTO>> generateAvailableSlots(
            @RequestParam Long profissionalId,
            @RequestParam Long servicoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<TimeSlotDTO> slots = disponibilidadeService.generateAvailableSlots(profissionalId, servicoId, date);
        return ResponseEntity.ok(slots);
    }
} 