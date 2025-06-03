package com.microservice.grupo.repository;


import com.microservice.grupo.enums.StatusGrupoModel;
import com.microservice.grupo.model.GrupoTrabalhoModel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GrupoTrabalhoRepository extends JpaRepository<GrupoTrabalhoModel, Long> {
    List<GrupoTrabalhoModel> findByStatus(StatusGrupoModel status);
    Optional<GrupoTrabalhoModel> findByNome(String nome);
    Optional<GrupoTrabalhoModel> findByProjetoId(Long projetoId);
    List<GrupoTrabalhoModel> findByProfessorCoordenadorId(Long professorId);
}
