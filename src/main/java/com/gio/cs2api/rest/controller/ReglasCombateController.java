package com.gio.cs2api.rest.controller;

import com.gio.cs2api.service.ReglasCombateService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reglas")
@Tag(
        name = "Reglas publicadas por A1",
        description = "Dos reglas que otros servicios pueden invocar sin ver el código: "
                + "validación de disparo y validación de recarga parcial. "
                + "Un rechazo se devuelve con HTTP 400 y aceptada=false.")
public class ReglasCombateController {

    private final ReglasCombateService reglasCombateService;

    public ReglasCombateController(
            ReglasCombateService reglasCombateService) {
        this.reglasCombateService = reglasCombateService;
    }

    @Operation(
            summary = "Validar regla de disparo",
            description = "Valida los datos de un disparo sin consumir munición ni modificar la base de datos.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "La regla acepta el disparo.",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(
                                implementation = ReglasCombateService.ResultadoDisparo.class),
                        examples = @ExampleObject(
                                name = "Disparo aceptado",
                                value = """
                                        {
                                          "aceptada": true,
                                          "regla": "DISPARO",
                                          "mensaje": "La regla acepta el disparo.",
                                          "danoEfectivo": 15
                                        }
                                        """))),
        @ApiResponse(
                responseCode = "400",
                description = "El disparo se rechaza por incumplir una regla.",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(
                                implementation = ReglasCombateService.ResultadoDisparo.class),
                        examples = @ExampleObject(
                                name = "Disparo rechazado por falta de munición",
                                value = """
                                        {
                                          "aceptada": false,
                                          "regla": "DISPARO",
                                          "mensaje": "El rifle no tiene munición.",
                                          "danoEfectivo": null
                                        }
                                        """)))
    })
    @PostMapping("/disparo")
    public ResponseEntity<ReglasCombateService.ResultadoDisparo> validarDisparo(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SolicitudDisparo.class),
                            examples = @ExampleObject(
                                    name = "Datos del disparo",
                                    value = """
                                            {
                                              "municionActual": 29,
                                              "danoBase": 36,
                                              "precision": 0.85,
                                              "distancia": 50
                                            }
                                            """)))
            @RequestBody
            SolicitudDisparo solicitud) {

        var resultado = reglasCombateService.validarDisparo(
                solicitud.municionActual(),
                solicitud.danoBase(),
                solicitud.precision(),
                solicitud.distancia());

        if (resultado.aceptada()) {
            return ResponseEntity.ok(resultado);
        }

        return ResponseEntity.badRequest().body(resultado);
    }

    @Operation(
            summary = "Validar regla de recarga",
            description = "Valida si un arma puede recargarse parcialmente sin modificar la munición ni la base de datos.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "La regla acepta la recarga.",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(
                                implementation = ReglasCombateService.ResultadoRecarga.class),
                        examples = @ExampleObject(
                                name = "Recarga parcial aceptada",
                                value = """
                                        {
                                          "aceptada": true,
                                          "regla": "RECARGA",
                                          "mensaje": "La regla acepta la recarga.",
                                          "municionATransferir": 5
                                        }
                                        """))),
        @ApiResponse(
                responseCode = "400",
                description = "La recarga se rechaza por incumplir una regla.",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(
                                implementation = ReglasCombateService.ResultadoRecarga.class),
                        examples = @ExampleObject(
                                name = "Recarga rechazada por falta de reserva",
                                value = """
                                        {
                                          "aceptada": false,
                                          "regla": "RECARGA",
                                          "mensaje": "No hay munición de reserva para recargar.",
                                          "municionATransferir": 0
                                        }
                                        """)))
    })
    @PostMapping("/recarga")
    public ResponseEntity<ReglasCombateService.ResultadoRecarga> validarRecarga(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SolicitudRecarga.class),
                            examples = @ExampleObject(
                                    name = "Datos de la recarga",
                                    value = """
                                            {
                                              "municionActual": 29,
                                              "municionMax": 30,
                                              "municionReserva": 90
                                            }
                                            """)))
            @RequestBody
            SolicitudRecarga solicitud) {

        var resultado = reglasCombateService.validarRecarga(
                solicitud.municionActual(),
                solicitud.municionMax(),
                solicitud.municionReserva());

        if (resultado.aceptada()) {
            return ResponseEntity.ok(resultado);
        }

        return ResponseEntity.badRequest().body(resultado);
    }

    public record SolicitudDisparo(
            Integer municionActual,
            Integer danoBase,
            Float precision,
            Integer distancia) {
    }

    public record SolicitudRecarga(
            Integer municionActual,
            Integer municionMax,
            Integer municionReserva) {
    }
}