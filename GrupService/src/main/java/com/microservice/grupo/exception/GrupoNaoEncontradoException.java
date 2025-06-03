package com.microservice.grupo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class GrupoNaoEncontradoException extends RuntimeException {
    public GrupoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
