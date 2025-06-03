package com.microservice.grupo.dto;


import java.util.List;
import java.util.Map;

import com.microservice.grupo.enums.StatusGrupoModel;

public class GrupoTrabalhoResponseDTO {
    private Long id;
    private String nome;
    private StatusGrupoModel status;
    private Long professorCoordenadorId;
    private String nomeProfessorCoordenador;
    private Long projetoId;
    private String nomeProjeto;
    private List<Long> alunoIds;
    private List<Map<String, Object>> alunosDetalhes;


    public GrupoTrabalhoResponseDTO(Long id, String nome, StatusGrupoModel status, Long professorCoordenadorId, Long projetoId, List<Long> alunoIds) {
        this.id = id;
        this.nome = nome;
        this.status = status;
        this.professorCoordenadorId = professorCoordenadorId;
        this.projetoId = projetoId;
        this.alunoIds = alunoIds;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public StatusGrupoModel getStatus() { return status; }
    public void setStatus(StatusGrupoModel status) { this.status = status; }
    public Long getProfessorCoordenadorId() { return professorCoordenadorId; }
    public void setProfessorCoordenadorId(Long professorCoordenadorId) { this.professorCoordenadorId = professorCoordenadorId; }
    public String getNomeProfessorCoordenador() { return nomeProfessorCoordenador; }
    public void setNomeProfessorCoordenador(String nomeProfessorCoordenador) { this.nomeProfessorCoordenador = nomeProfessorCoordenador; }
    public Long getProjetoId() { return projetoId; }
    public void setProjetoId(Long projetoId) { this.projetoId = projetoId; }
    public String getNomeProjeto() { return nomeProjeto; }
    public void setNomeProjeto(String nomeProjeto) { this.nomeProjeto = nomeProjeto; }
    public List<Long> getAlunoIds() { return alunoIds; }
    public void setAlunoIds(List<Long> alunoIds) { this.alunoIds = alunoIds; }
    public List<Map<String, Object>> getAlunosDetalhes() { return alunosDetalhes; }
    public void setAlunosDetalhes(List<Map<String, Object>> alunosDetalhes) { this.alunosDetalhes = alunosDetalhes; }
}