package com.gio.cs2api.rest.controller;

import com.gio.cs2api.domain.ArmaNoEncontradaException;
import com.gio.cs2api.domain.ExcepcionDominioCS2;
import com.gio.cs2api.domain.MunicionAgotadaException;
import com.gio.cs2api.service.CompaneroNoDisponibleException;
import com.gio.cs2api.service.RechazoCompaneroException;
import com.gio.cs2api.service.ValidadorCompanero.ResultadoValidacion;

import java.util.LinkedHashMap;
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

    @ExceptionHandler(RechazoCompaneroException.class)
    public ResponseEntity<Map<String, Object>> manejarRechazoCompanero(
            RechazoCompaneroException exception) {

        ResultadoValidacion resultado = exception.getResultado();

        Map<String, Object> error = new LinkedHashMap<>();
        error.put("error", exception.getMessage());
        error.put("rechazadoPor", exception.getRol());
        error.put("regla", exception.getRegla());
        error.put("mensajeCompanero", resultado.mensaje());
        error.put("codigoHttpCompanero", resultado.codigoHttp());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(CompaneroNoDisponibleException.class)
    public ResponseEntity<Map<String, Object>> manejarCompaneroNoDisponible(
            CompaneroNoDisponibleException exception) {

        Map<String, Object> error = new LinkedHashMap<>();
        error.put("error", exception.getMessage());
        error.put("rechazadoPor", exception.getRol());
        error.put("motivo", "sin respuesta");

        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(error);
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