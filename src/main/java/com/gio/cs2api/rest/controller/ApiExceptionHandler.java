package com.gio.cs2api.rest.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> manejarArgumentoInvalido(
            IllegalArgumentException exception) {

        Map<String, String> error = Map.of(
                "error",
                exception.getMessage() == null
                        ? "Solicitud inválida"
                        : exception.getMessage()
        );

        return ResponseEntity.badRequest().body(error);
    }
}
