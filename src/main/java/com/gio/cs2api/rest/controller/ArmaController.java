
package com.gio.cs2api.rest.controller;

import com.gio.cs2api.service.ArmaDemostracionService;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ArmaController {

    private final ArmaDemostracionService armaDemostracionService;

    public ArmaController(
            ArmaDemostracionService armaDemostracionService) {
        this.armaDemostracionService = armaDemostracionService;
    }

    @GetMapping("/armas/{tipo}")
    public ResponseEntity<Map<String, Object>> crearArma(
            @PathVariable String tipo,
            @RequestParam(defaultValue = "Arma CS2") String nombre,
            @RequestParam(defaultValue = "100") float precio,
            @RequestParam(required = false) Integer dano,
            @RequestParam(required = false) Integer cargador) {

        return ResponseEntity.ok(
                armaDemostracionService.crearArma(
                        tipo, nombre, precio, dano, cargador));
    }

    @GetMapping("/polimorfismo")
    public ResponseEntity<Map<String, Object>> demostrarPolimorfismo() {
        return ResponseEntity.ok(
                armaDemostracionService.demostrarPolimorfismo());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> manejarError(
            IllegalArgumentException exception) {

        Map<String, Object> error = Map.of(
                "error",
                exception.getMessage() == null
                        ? "Parámetros inválidos"
                        : exception.getMessage());

        return ResponseEntity.badRequest().body(error);
    }
}
