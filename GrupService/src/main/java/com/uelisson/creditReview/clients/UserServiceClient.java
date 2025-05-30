package com.uelisson.creditReview.clients;

import com.uelisson.creditReview.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", path = "/api/usuarios")
public interface UserServiceClient {
    @GetMapping("/{id}")
    UserDTO getUsuarioById(@PathVariable("id") Long id);
}
