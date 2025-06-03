package com.microservice.grupo.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.microservice.grupo.dto.ProjectDTO;

@FeignClient(name = "project-service", path = "/api/projetos")
public interface ProjectServiceClient {
    @GetMapping("/{id}")
    ProjectDTO getProjetoById(@PathVariable("id") Long id);
}