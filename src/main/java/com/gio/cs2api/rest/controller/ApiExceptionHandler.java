package com.gio.cs2api.rest.controller;

import com.gio.cs2api.domain.ArmaNoEncontradaException;
import com.gio.cs2api.domain.ExcepcionDominioCS2;
import com.gio.cs2api.domain.MunicionAgotadaException;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ArmaNoEncontradaException.class)
    public ResponseEntity<Map<String, String>> manejarArmaNoEncontrada(
            ArmaNoEncontradaException exception) {
        return respuesta(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(MunicionAgotadaException.class)
    public ResponseEntity<Map<String, String>> manejarMunicionAgotada(
            MunicionAgotadaException exception) {
        return respuesta(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(ExcepcionDominioCS2.class)
    public ResponseEntity<Map<String, String>> manejarExcepcionDominio(
            ExcepcionDominioCS2 exception) {
        return respuesta(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> manejarArgumentoInvalido(
            IllegalArgumentException exception) {
        return respuesta(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    private ResponseEntity<Map<String, String>> respuesta(
            HttpStatus estado, String mensaje) {
        Map<String, String> error = Map.of(
                "error",
                mensaje == null ? "Error del dominio" : mensaje
        );

        return ResponseEntity.status(estado).body(error);
    }
}