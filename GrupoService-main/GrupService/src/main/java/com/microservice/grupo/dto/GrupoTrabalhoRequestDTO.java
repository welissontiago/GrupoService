package com.microservice.grupo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public class GrupoTrabalhoRequestDTO {

    @NotBlank(message = "Nome do grupo é obrigatório.")
    @Size(min = 3, max = 100, message = "Nome do grupo deve ter entre 3 e 100 caracteres.")
    private String nome;

    @NotNull(message = "ID do professor coordenador é obrigatório.")
    private Long professorId;

    private List<Long> alunoIds;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Long getProfessorId() { return professorId; }
    public void setProfessorId(Long professorId) { this.professorId = professorId; }
    public List<Long> getAlunoIds() { return alunoIds; }
    public void setAlunoIds(List<Long> alunoIds) { this.alunoIds = alunoIds; }
}
