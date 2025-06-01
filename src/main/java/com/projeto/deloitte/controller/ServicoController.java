package com.projeto.deloitte.controller;

import com.projeto.deloitte.dto.ServicoRequestDTO;
import com.projeto.deloitte.dto.ServicoResponseDTO;
import com.projeto.deloitte.service.ServicoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servicos")
public class ServicoController {

    @Autowired
    private ServicoService servicoService;

    @PreAuthorize("hasRole('PROFISSIONAL')")
    @PostMapping
    public ResponseEntity<ServicoResponseDTO> createServico(@Valid @RequestBody ServicoRequestDTO servicoRequestDTO) {
        ServicoResponseDTO createdServico = servicoService.createServico(servicoRequestDTO);
        return new ResponseEntity<>(createdServico, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServicoResponseDTO> getServicoById(@PathVariable Long id) {
        ServicoResponseDTO servico = servicoService.getServicoById(id);
        return ResponseEntity.ok(servico);
    }

    @GetMapping
    public ResponseEntity<List<ServicoResponseDTO>> getAllServicos() {
        List<ServicoResponseDTO> servicos = servicoService.getAllServicos();
        return ResponseEntity.ok(servicos);
    }

    @PreAuthorize("hasRole('PROFISSIONAL')")
    @PutMapping("/{id}")
    public ResponseEntity<ServicoResponseDTO> updateServico(@PathVariable Long id, @Valid @RequestBody ServicoRequestDTO servicoRequestDTO) {
        ServicoResponseDTO updatedServico = servicoService.updateServico(id, servicoRequestDTO);
        return ResponseEntity.ok(updatedServico);
    }

    @PreAuthorize("hasRole('PROFISSIONAL')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteServico(@PathVariable Long id) {
        servicoService.deleteServico(id);
        return new ResponseEntity<>("Serviço deletado com sucesso!", HttpStatus.OK);
    }

    @GetMapping("/profissional/{profissionalId}")
    public ResponseEntity<List<ServicoResponseDTO>> getServicosByProfissional(@PathVariable Long profissionalId) {
        List<ServicoResponseDTO> servicos = servicoService.getServicosByProfissional(profissionalId);
        return ResponseEntity.ok(servicos);
    }
} 