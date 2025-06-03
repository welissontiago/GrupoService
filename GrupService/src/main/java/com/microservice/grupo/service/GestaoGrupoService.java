package com.microservice.grupo.service;

import com.microservice.grupo.clients.ProjectServiceClient;
import com.microservice.grupo.clients.UserServiceClient;
import com.microservice.grupo.dto.GrupoTrabalhoRequestDTO;
import com.microservice.grupo.dto.GrupoTrabalhoResponseDTO;
import com.microservice.grupo.dto.ProjectDTO;
import com.microservice.grupo.dto.UserDTO;
import com.microservice.grupo.enums.StatusGrupoModel;
import com.microservice.grupo.exception.GrupoNaoEncontradoException;
import com.microservice.grupo.model.GrupoTrabalhoModel;
import com.microservice.grupo.repository.GrupoTrabalhoRepository;

import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GestaoGrupoService {

    private static final Logger log = LoggerFactory.getLogger(GestaoGrupoService.class);
    private final GrupoTrabalhoRepository grupoRepository;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    @Autowired
    public GestaoGrupoService(GrupoTrabalhoRepository grupoRepository,
                              UserServiceClient userServiceClient,
                              ProjectServiceClient projectServiceClient) {
        this.grupoRepository = grupoRepository;
        this.userServiceClient = userServiceClient;
        this.projectServiceClient = projectServiceClient;
    }

    private GrupoTrabalhoResponseDTO toResponseDTO(GrupoTrabalhoModel grupo) {
        if (grupo == null) return null;
        GrupoTrabalhoResponseDTO dto = new GrupoTrabalhoResponseDTO(
                grupo.getId(),
                grupo.getNome(),
                grupo.getStatus(),
                grupo.getProfessorCoordenadorId(),
                grupo.getProjetoId(),
                grupo.getAlunoIds() != null ? new ArrayList<>(grupo.getAlunoIds()) : Collections.emptyList()
        );

        if (grupo.getProfessorCoordenadorId() != null) {
            try {
                UserDTO user = userServiceClient.getUsuarioById(grupo.getProfessorCoordenadorId());
                if (user != null) {
                    dto.setNomeProfessorCoordenador(user.getNome());
                }
            } catch (FeignException e) {
                log.error("Falha ao buscar nome do professor coordenador ID {}: {}", grupo.getProfessorCoordenadorId(), e.getMessage());
                dto.setNomeProfessorCoordenador("Professor não encontrado/serviço indisponível");
            }
        }

        if (grupo.getProjetoId() != null) {
            try {
                ProjectDTO project = projectServiceClient.getProjetoById(grupo.getProjetoId());
                if (project != null) {
                    dto.setNomeProjeto(project.getNome());
                }
            } catch (FeignException e) {
                log.error("Falha ao buscar nome do projeto ID {}: {}", grupo.getProjetoId(), e.getMessage());
                dto.setNomeProjeto("Projeto não encontrado/serviço indisponível");
            }
        }

        if (grupo.getAlunoIds() != null && !grupo.getAlunoIds().isEmpty()) {
            List<Map<String, Object>> alunosDetalhesList = new ArrayList<>();
            for (Long alunoId : grupo.getAlunoIds()) {
                try {
                    UserDTO alunoUser = userServiceClient.getUsuarioById(alunoId);
                    if (alunoUser != null) {
                        Map<String, Object> alunoMap = new HashMap<>();
                        alunoMap.put("id", alunoUser.getId());
                        alunoMap.put("nome", alunoUser.getNome());
                        alunosDetalhesList.add(alunoMap);
                    }
                } catch (FeignException e) {
                    log.error("Falha ao buscar detalhes do aluno ID {}: {}", alunoId, e.getMessage());
                    Map<String, Object> alunoErrorMap = new HashMap<>();
                    alunoErrorMap.put("id", alunoId);
                    alunoErrorMap.put("nome", "Aluno não encontrado/serviço indisponível");
                    alunosDetalhesList.add(alunoErrorMap);
                }
            }
            dto.setAlunosDetalhes(alunosDetalhesList);
        } else {
            dto.setAlunosDetalhes(Collections.emptyList());
        }

        return dto;
    }

    @Transactional
    public GrupoTrabalhoResponseDTO criarNovoGrupo(GrupoTrabalhoRequestDTO requestDTO) {
        if (grupoRepository.findByNome(requestDTO.getNome()).isPresent()) {
            throw new IllegalArgumentException("Um grupo com o nome '" + requestDTO.getNome() + "' já existe.");
        }

        GrupoTrabalhoModel novoGrupo = new GrupoTrabalhoModel();
        novoGrupo.setNome(requestDTO.getNome());
        novoGrupo.setProfessorCoordenadorId(requestDTO.getProfessorId());
        if (requestDTO.getAlunoIds() != null) {
            novoGrupo.setAlunoIds(new ArrayList<>(requestDTO.getAlunoIds()));
        }
        novoGrupo.setStatus(StatusGrupoModel.DISPONIVEL);

        GrupoTrabalhoModel grupoSalvo = grupoRepository.save(novoGrupo);
        log.info("Grupo criado: ID {}, Nome: {}", grupoSalvo.getId(), grupoSalvo.getNome());
        return toResponseDTO(grupoSalvo);
    }

    @Transactional(readOnly = true)
    public List<GrupoTrabalhoResponseDTO> listarTodos() {
        return grupoRepository.findAll().stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<GrupoTrabalhoResponseDTO> buscarPorId(Long id) {
        return grupoRepository.findById(id).map(this::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public List<GrupoTrabalhoResponseDTO> buscarPorStatus(StatusGrupoModel status) {
        return grupoRepository.findByStatus(status).stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Transactional
    public GrupoTrabalhoResponseDTO atualizarGrupo(Long grupoId, GrupoTrabalhoRequestDTO requestDTO) {
        GrupoTrabalhoModel grupoExistente = grupoRepository.findById(grupoId)
                .orElseThrow(() -> new GrupoNaoEncontradoException("Grupo com ID " + grupoId + " não encontrado."));

        if (requestDTO.getNome() != null && !requestDTO.getNome().equals(grupoExistente.getNome())) {
            if (grupoRepository.findByNome(requestDTO.getNome()).filter(g -> !g.getId().equals(grupoId)).isPresent()) {
                throw new IllegalArgumentException("Outro grupo já existe com o nome: " + requestDTO.getNome());
            }
            grupoExistente.setNome(requestDTO.getNome());
        }
        if (requestDTO.getProfessorId() != null) {
            grupoExistente.setProfessorCoordenadorId(requestDTO.getProfessorId());
        }
        if (requestDTO.getAlunoIds() != null) {
            grupoExistente.setAlunoIds(new ArrayList<>(requestDTO.getAlunoIds()));
        }

        GrupoTrabalhoModel grupoAtualizado = grupoRepository.save(grupoExistente);
        log.info("Grupo atualizado: ID {}", grupoAtualizado.getId());
        return toResponseDTO(grupoAtualizado);
    }

    @Transactional
    public void deletarGrupo(Long grupoId) {
        GrupoTrabalhoModel grupo = grupoRepository.findById(grupoId)
                .orElseThrow(() -> new GrupoNaoEncontradoException("Grupo com ID " + grupoId + " não encontrado."));
        if (grupo.getStatus() == StatusGrupoModel.INDISPONIVEL && grupo.getProjetoId() != null) {
            throw new IllegalStateException("Grupo ID " + grupoId + " está associado ao projeto ID " + grupo.getProjetoId() + " e não pode ser deletado.");
        }
        grupoRepository.delete(grupo);
        log.info("Grupo deletado: ID {}", grupoId);
    }

    @Transactional
    public GrupoTrabalhoResponseDTO adicionarAlunos(Long grupoId, List<Long> alunoIdsParaAdicionar) {
        GrupoTrabalhoModel grupo = grupoRepository.findById(grupoId)
                .orElseThrow(() -> new GrupoNaoEncontradoException("Grupo com ID " + grupoId + " não encontrado."));
        List<Long> alunosAtuais = grupo.getAlunoIds();
        boolean modificado = false;
        for (Long alunoId : alunoIdsParaAdicionar) {
            if (!alunosAtuais.contains(alunoId)) {
                alunosAtuais.add(alunoId);
                modificado = true;
            }
        }
        if (modificado) {
            grupo.setAlunoIds(alunosAtuais);
            grupoRepository.save(grupo);
            log.info("Alunos adicionados ao grupo ID {}: {}", grupoId, alunoIdsParaAdicionar);
        }
        return toResponseDTO(grupo);
    }

    @Transactional
    public GrupoTrabalhoResponseDTO removerAlunos(Long grupoId, List<Long> alunoIdsParaRemover) {
        GrupoTrabalhoModel grupo = grupoRepository.findById(grupoId)
                .orElseThrow(() -> new GrupoNaoEncontradoException("Grupo com ID " + grupoId + " não encontrado."));

        boolean modificado = grupo.getAlunoIds().removeAll(alunoIdsParaRemover);
        if (modificado) {
            grupoRepository.save(grupo);
            log.info("Alunos removidos do grupo ID {}: {}", grupoId, alunoIdsParaRemover);
        }
        return toResponseDTO(grupo);
    }

    @Transactional
    public GrupoTrabalhoResponseDTO associarProjetoAoGrupo(Long grupoId, Long projetoId) {
        GrupoTrabalhoModel grupo = grupoRepository.findById(grupoId)
                .orElseThrow(() -> new GrupoNaoEncontradoException("Grupo com ID " + grupoId + " não encontrado."));

        if (grupo.getStatus() != StatusGrupoModel.DISPONIVEL) {
            throw new IllegalStateException("Grupo ID " + grupoId + " não está DISPONÍVEL para associação. Status atual: " + grupo.getStatus());
        }
        if (grupo.getProjetoId() != null) {
            throw new IllegalStateException("Grupo ID " + grupoId + " já está associado ao projeto ID " + grupo.getProjetoId());
        }


        grupo.setProjetoId(projetoId);
        grupo.setStatus(StatusGrupoModel.INDISPONIVEL);
        GrupoTrabalhoModel grupoSalvo = grupoRepository.save(grupo);
        log.info("Projeto ID {} associado ao Grupo ID {}. Status do grupo alterado para INDISPONIVEL.", projetoId, grupoId);
        return toResponseDTO(grupoSalvo);
    }

    @Transactional
    public GrupoTrabalhoResponseDTO desassociarProjetoDoGrupo(Long grupoId) {
        GrupoTrabalhoModel grupo = grupoRepository.findById(grupoId)
                .orElseThrow(() -> new GrupoNaoEncontradoException("Grupo com ID " + grupoId + " não encontrado."));

        if (grupo.getProjetoId() == null) {
            log.warn("Tentativa de desassociar projeto do grupo ID {}, mas nenhum projeto está associado.", grupoId);
            return toResponseDTO(grupo);
        }

        Long projetoIdAnterior = grupo.getProjetoId();
        grupo.setProjetoId(null);
        grupo.setStatus(StatusGrupoModel.DISPONIVEL);
        GrupoTrabalhoModel grupoSalvo = grupoRepository.save(grupo);
        log.info("Projeto ID {} desassociado do Grupo ID {}. Status do grupo alterado para DISPONIVEL.", projetoIdAnterior, grupoId);
        return toResponseDTO(grupoSalvo);
    }

}
