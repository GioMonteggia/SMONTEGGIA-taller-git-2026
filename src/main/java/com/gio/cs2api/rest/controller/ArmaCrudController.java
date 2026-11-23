package com.gio.cs2api.rest.controller;

import com.gio.cs2api.domain.Arma;
import com.gio.cs2api.service.ArmaService;

import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/armas")
@Tag(
        name = "CRUD de armas",
        description = "Crea, lista, actualiza y elimina armas persistidas en H2.")
public class ArmaCrudController {

    private final ArmaService armaService;

    public ArmaCrudController(ArmaService armaService) {
        this.armaService = armaService;
    }

    @Operation(summary = "Listar todas las armas persistidas")
    @GetMapping
    public List<Arma> listar() {
        return armaService.listar();
    }

    @Operation(summary = "Consultar un arma por su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Arma encontrada."),
        @ApiResponse(responseCode = "404", description = "No existe el arma.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Arma> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(armaService.buscarPorId(id));
    }

    @Operation(
            summary = "Crear un arma",
            description = "El campo tipo admite rifle o pistola. "
                    + "tipo, nombre y precio son obligatorios.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Arma creada."),
        @ApiResponse(responseCode = "400", description = "Datos inválidos.")
    })
    @PostMapping
    public ResponseEntity<Arma> crear(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(
                                    name = "Rifle",
                                    value = """
                                            {
                                              "tipo": "rifle",
                                              "nombre": "AK-47",
                                              "precio": 2700,
                                              "dano": 36,
                                              "cargador": 30
                                            }
                                            """)))
            @RequestBody
            Map<String, Object> datos) {

        Arma guardada = armaService.crear(datos);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
    }

    @Operation(
            summary = "Actualizar un arma",
            description = "Conserva la munición actual del cargador.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Arma actualizada."),
        @ApiResponse(responseCode = "400", description = "Datos inválidos."),
        @ApiResponse(responseCode = "404", description = "No existe el arma.")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Arma> actualizar(
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(
                                    name = "Cambio de precio",
                                    value = """
                                            {
                                              "tipo": "rifle",
                                              "nombre": "AK-47",
                                              "precio": 3100
                                            }
                                            """)))
            @RequestBody
            Map<String, Object> datos) {

        return ResponseEntity.ok(armaService.actualizar(id, datos));
    }

    @Operation(summary = "Eliminar un arma por su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Arma eliminada."),
        @ApiResponse(responseCode = "404", description = "No existe el arma.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        armaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
