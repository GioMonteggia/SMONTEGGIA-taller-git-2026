package com.gio.cs2api.rest.controller;

import java.util.List;

import com.gio.cs2api.domain.Jugada;
import com.gio.cs2api.service.EscenarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/escenarios")
@Tag(
        name = "Escenario POO-09",
        description = "Disparo con lanzamiento de granada. A2 valida la regla "
                + "económica del arma y A3 valida munición y granada no "
                + "reutilizada. La jugada se guarda en H2 sólo si los dos "
                + "aceptan.")
public class EscenarioController {

    private final EscenarioService escenarioService;

    public EscenarioController(EscenarioService escenarioService) {
        this.escenarioService = escenarioService;
    }

    @Operation(
            summary = "Ejecutar el escenario con las dos APIs ajenas",
            description = "A2 valida el precio del arma y A3 valida munición "
                    + "y granada no reutilizada. El cliente sólo habla con A1.")
    @ApiResponses({
        @ApiResponse(responseCode = "201",
                description = "A2 y A3 aceptaron; jugada persistida.",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = Jugada.class))),
        @ApiResponse(responseCode = "400",
                description = "Datos del escenario inválidos."),
        @ApiResponse(responseCode = "409",
                description = "Un compañero rechazó; H2 queda igual.",
                content = @Content(
                        mediaType = "application/json",
                        examples = @ExampleObject(
                                name = "Rechazo de A2",
                                value = """
                                        {
                                          "error": "A2 rechazó el escenario: El precio $25000.00 viola la regla económica de CS2 (rango reglamentario: $0 a $16,000).",
                                          "rechazadoPor": "A2",
                                          "regla": "El precio del arma debe estar entre $0 y $16.000.",
                                          "mensajeCompanero": "El precio $25000.00 viola la regla económica de CS2 (rango reglamentario: $0 a $16,000).",
                                          "codigoHttpCompanero": 422
                                        }
                                        """))),
        @ApiResponse(responseCode = "502",
                description = "A2 o A3 no respondió; H2 queda igual.")
    })
    @PostMapping
    public ResponseEntity<Jugada> ejecutar(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = EscenarioService.SolicitudEscenario.class),
                            examples = @ExampleObject(
                                    name = "Escenario feliz",
                                    value = """
                                            {
                                              "rifleId": 1,
                                              "jugadorId": 1,
                                              "distancia": 50,
                                              "granadaLanzada": false
                                            }
                                            """)))
            @RequestBody
            EscenarioService.SolicitudEscenario solicitud) {

        Jugada jugada = escenarioService.ejecutar(solicitud);
        return ResponseEntity.status(HttpStatus.CREATED).body(jugada);
    }

    @Operation(
            summary = "Listar jugadas aceptadas",
            description = "Devuelve las jugadas persistidas por el escenario.")
    @GetMapping
    public List<Jugada> listar() {
        return escenarioService.listar();
    }

    @Operation(
            summary = "Consultar una jugada por ID",
            description = "Permite verificar lo que quedó guardado en H2.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Jugada encontrada."),
        @ApiResponse(responseCode = "400", description = "No existe esa jugada.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Jugada> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(escenarioService.buscar(id));
    }
}
