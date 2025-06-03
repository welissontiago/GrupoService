package com.microservice.grupo.controllers;

import com.microservice.grupo.dto.GrupoTrabalhoRequestDTO;
import com.microservice.grupo.dto.GrupoTrabalhoResponseDTO;
import com.microservice.grupo.enums.StatusGrupoModel;
import com.microservice.grupo.exception.GrupoNaoEncontradoException;
import com.microservice.grupo.service.GestaoGrupoService;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/grupos")
public class GrupoController {

    private static final Logger log = LoggerFactory.getLogger(GrupoController.class);
    private final GestaoGrupoService gestaoGrupoService;

    @Autowired
    public GrupoController(GestaoGrupoService gestaoGrupoService) {
        this.gestaoGrupoService = gestaoGrupoService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> criarGrupo(@Valid @RequestBody GrupoTrabalhoRequestDTO requestDTO) {
        try {
            GrupoTrabalhoResponseDTO novoGrupo = gestaoGrupoService.criarNovoGrupo(requestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoGrupo);
        } catch (IllegalArgumentException e) {
            log.warn("Falha ao criar grupo: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<GrupoTrabalhoResponseDTO>> listarGrupos(
            @RequestParam(required = false) StatusGrupoModel status) {
        if (status != null) {
            return ResponseEntity.ok(gestaoGrupoService.buscarPorStatus(status));
        }
        return ResponseEntity.ok(gestaoGrupoService.listarTodos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<GrupoTrabalhoResponseDTO> buscarGrupoPorId(@PathVariable Long id) {
        return gestaoGrupoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> atualizarGrupo(
            @PathVariable Long id, @Valid @RequestBody GrupoTrabalhoRequestDTO requestDTO) {
        try {
            GrupoTrabalhoResponseDTO grupoAtualizado = gestaoGrupoService.atualizarGrupo(id, requestDTO);
            return ResponseEntity.ok(grupoAtualizado);
        } catch (GrupoNaoEncontradoException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            log.warn("Falha ao atualizar grupo ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deletarGrupo(@PathVariable Long id) {
        try {
            gestaoGrupoService.deletarGrupo(id);
            return ResponseEntity.noContent().build();
        } catch (GrupoNaoEncontradoException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            log.warn("Falha ao deletar grupo ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{grupoId}/alunos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> adicionarAlunosAoGrupo(
            @PathVariable Long grupoId, @RequestBody List<Long> alunoIds) {
        try {
            if (alunoIds == null || alunoIds.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Lista de IDs de alunos não pode ser vazia."));
            }
            GrupoTrabalhoResponseDTO grupoAtualizado = gestaoGrupoService.adicionarAlunos(grupoId, alunoIds);
            return ResponseEntity.ok(grupoAtualizado);
        } catch (GrupoNaoEncontradoException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            log.warn("Falha ao adicionar alunos ao grupo ID {}: {}", grupoId, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{grupoId}/alunos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> removerAlunosDoGrupo(
            @PathVariable Long grupoId, @RequestBody List<Long> alunoIds) {
        try {
            if (alunoIds == null || alunoIds.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Lista de IDs de alunos para remover não pode ser vazia."));
            }
            GrupoTrabalhoResponseDTO grupoAtualizado = gestaoGrupoService.removerAlunos(grupoId, alunoIds);
            return ResponseEntity.ok(grupoAtualizado);
        } catch (GrupoNaoEncontradoException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{grupoId}/associar-projeto/{projetoId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> associarProjeto(
            @PathVariable Long grupoId, @PathVariable Long projetoId) {
        try {
            GrupoTrabalhoResponseDTO grupoAtualizado = gestaoGrupoService.associarProjetoAoGrupo(grupoId, projetoId);
            return ResponseEntity.ok(grupoAtualizado);
        } catch (GrupoNaoEncontradoException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException | IllegalArgumentException e) {
            log.warn("Falha ao associar projeto {} ao grupo {}: {}", projetoId, grupoId, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


    @PutMapping("/{grupoId}/desassociar-projeto")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> desassociarProjeto(@PathVariable Long grupoId) {
        try {
            GrupoTrabalhoResponseDTO grupoAtualizado = gestaoGrupoService.desassociarProjetoDoGrupo(grupoId);
            return ResponseEntity.ok(grupoAtualizado);
        } catch (GrupoNaoEncontradoException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
