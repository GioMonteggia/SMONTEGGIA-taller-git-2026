
package com.gio.cs2api.rest.controller;

import com.gio.cs2api.service.RifleDemostracionService;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rifles")
public class RifleController {

    private final RifleDemostracionService rifleService;

    public RifleController(RifleDemostracionService rifleService) {
        this.rifleService = rifleService;
    }

    @GetMapping("/{nombre}")
    public ResponseEntity<Map<String, Object>> detalle(
            @PathVariable String nombre,
            @RequestParam(defaultValue = "2700") float precio,
            @RequestParam(defaultValue = "36") int dano,
            @RequestParam(defaultValue = "30") int cargador) {

        return ResponseEntity.ok(
                rifleService.detalle(nombre, precio, dano, cargador));
    }

    @GetMapping("/{nombre}/disparar")
    public ResponseEntity<Map<String, Object>> disparar(
            @PathVariable String nombre,
            @RequestParam(defaultValue = "2700") float precio,
            @RequestParam(defaultValue = "30") int cargador,
            @RequestParam(required = false) Integer distancia,
            @RequestParam(defaultValue = "1") int veces) {

        return ResponseEntity.ok(
                rifleService.disparar(
                        nombre, precio, cargador, distancia, veces));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> manejarArgumentoInvalido(
            IllegalArgumentException exception) {

        Map<String, Object> error = Map.of(
                "error",
                exception.getMessage() == null
                        ? "Parámetros inválidos"
                        : exception.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
