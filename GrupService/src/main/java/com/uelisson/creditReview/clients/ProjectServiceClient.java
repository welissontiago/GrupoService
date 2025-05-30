package com.uelisson.creditReview.clients;

import com.uelisson.creditReview.dto.ProjectDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "project-service", path = "/api/projetos")
public interface ProjectServiceClient {
    @GetMapping("/{id}")
    ProjectDTO getProjetoById(@PathVariable("id") Long id);
}