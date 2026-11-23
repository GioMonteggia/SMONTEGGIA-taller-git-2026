package com.gio.cs2api.rest.controller;

import com.gio.cs2api.domain.Jugador;
import com.gio.cs2api.service.JugadorService;
import com.gio.cs2api.service.SimulacionService;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/simulacion")
@Tag(
        name = "Simulación persistida",
        description = "Crea jugadores, consulta acciones por AccionCombate, "
                + "dispara y recarga parcialmente con persistencia en H2.")
public class SimulacionController {

    private final SimulacionService simulacionService;
    private final JugadorService jugadorService;

    public SimulacionController(
            SimulacionService simulacionService,
            JugadorService jugadorService) {
        this.simulacionService = simulacionService;
        this.jugadorService = jugadorService;
    }

    @Operation(summary = "Crear un jugador persistido")
    @PostMapping("/jugadores")
    public ResponseEntity<Jugador> crearJugador(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = CrearJugadorRequest.class),
                            examples = @ExampleObject(
                                    name = "Jugador",
                                    value = """
                                            {
                                              "nombre": "Jugador prueba",
                                              "vida": 100,
                                              "municionReserva": 90
                                            }
                                            """)))
            @RequestBody
            CrearJugadorRequest solicitud) {

        Jugador guardado = jugadorService.crear(
                solicitud.nombre(),
                solicitud.vida(),
                solicitud.municionReserva());

        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    @Operation(summary = "Consultar un jugador por su ID")
    @GetMapping("/jugadores/{id}")
    public ResponseEntity<Jugador> buscarJugador(@PathVariable Long id) {
        return jugadorService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Consultar acciones mediante AccionCombate",
            description = "Usa una referencia de tipo AccionCombate sobre el arma persistida.")
    @GetMapping("/acciones/{armaId}")
    public ResponseEntity<Map<String, Object>> consultarAcciones(
            @PathVariable Long armaId) {
        return ResponseEntity.ok(
                simulacionService.consultarAcciones(armaId));
    }

    @Operation(
            summary = "Disparar un rifle contra un jugador",
            description = "Regla de daño: factor máximo(0.5, 1 - distancia/100) "
                    + "por la precisión del rifle. Consume una bala y persiste el resultado.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Disparo simulado."),
        @ApiResponse(responseCode = "400", description = "Distancia inválida."),
        @ApiResponse(responseCode = "404", description = "No existe el arma."),
        @ApiResponse(responseCode = "409", description = "Munición agotada.")
    })
    @PostMapping("/disparar")
    public ResponseEntity<Map<String, Object>> disparar(
            @RequestParam Long rifleId,
            @RequestParam Long jugadorId,
            @RequestParam(defaultValue = "50") int distancia) {

        return ResponseEntity.ok(
                simulacionService.disparar(rifleId, jugadorId, distancia));
    }

    @Operation(
            summary = "Recargar parcialmente desde la reserva",
            description = "Transfiere como máximo el espacio libre del cargador "
                    + "y descuenta del jugador sólo lo transferido.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Recarga realizada."),
        @ApiResponse(responseCode = "400", description = "Cargador lleno."),
        @ApiResponse(responseCode = "404", description = "No existe el arma."),
        @ApiResponse(responseCode = "409", description = "Sin reserva disponible.")
    })
    @PostMapping("/recargar")
    public ResponseEntity<Map<String, Object>> recargar(
            @RequestParam Long armaId,
            @RequestParam Long jugadorId) {

        return ResponseEntity.ok(
                simulacionService.recargar(armaId, jugadorId));
    }

    @Operation(
            summary = "Recarga por el endpoint de acciones",
            description = "Alias del endpoint de recarga para el recurso de acciones.")
    @PostMapping("/acciones/{armaId}/recargar")
    public ResponseEntity<Map<String, Object>> recargarPorInterfaz(
            @PathVariable Long armaId,
            @RequestParam Long jugadorId) {

        return ResponseEntity.ok(
                simulacionService.recargar(armaId, jugadorId));
    }

    public record CrearJugadorRequest(
            String nombre,
            Integer vida,
            Integer municionReserva) {
    }
}
