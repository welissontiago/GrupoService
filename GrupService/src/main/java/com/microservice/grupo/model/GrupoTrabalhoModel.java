package com.microservice.grupo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.microservice.grupo.enums.StatusGrupoModel;

@Entity
@Table(name = "grupos_trabalho")
public class GrupoTrabalhoModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome do grupo não pode ser vazio")
    @Column(nullable = false, unique = true)
    private String nome;

    @NotNull(message = "Status do grupo não pode ser nulo")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusGrupoModel status = StatusGrupoModel.DISPONIVEL;

    @Column(name = "professor_coordenador_id")
    private Long professorCoordenadorId;

    @Column(name = "projeto_id", unique = true, nullable = true)
    private Long projetoId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "grupo_trabalho_alunos", joinColumns = @JoinColumn(name = "grupo_id"))
    @Column(name = "aluno_id")
    private List<Long> alunoIds = new ArrayList<>();

    public GrupoTrabalhoModel() {
    }

    public GrupoTrabalhoModel(String nome, Long professorCoordenadorId, List<Long> alunoIds) {
        this.nome = nome;
        this.professorCoordenadorId = professorCoordenadorId;
        if (alunoIds != null) {
            this.alunoIds.addAll(alunoIds);
        }
        this.status = StatusGrupoModel.DISPONIVEL;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public StatusGrupoModel getStatus() { return status; }
    public void setStatus(StatusGrupoModel status) { this.status = status; }
    public Long getProfessorCoordenadorId() { return professorCoordenadorId; }
    public void setProfessorCoordenadorId(Long professorCoordenadorId) { this.professorCoordenadorId = professorCoordenadorId; }
    public Long getProjetoId() { return projetoId; }
    public void setProjetoId(Long projetoId) { this.projetoId = projetoId; }
    public List<Long> getAlunoIds() { return alunoIds; }
    public void setAlunoIds(List<Long> alunoIds) { this.alunoIds = alunoIds; }
}
