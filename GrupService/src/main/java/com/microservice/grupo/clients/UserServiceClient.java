package com.microservice.grupo.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.microservice.grupo.dto.UserDTO;

@FeignClient(name = "user-service", path = "/api/usuarios")
public interface UserServiceClient {
    @GetMapping("/{id}")
    UserDTO getUsuarioById(@PathVariable("id") Long id);
}
