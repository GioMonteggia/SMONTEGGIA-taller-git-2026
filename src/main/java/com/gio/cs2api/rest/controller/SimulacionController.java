package com.gio.cs2api.rest.controller;

import com.gio.cs2api.domain.Jugador;
import com.gio.cs2api.repository.JugadorRepository;
import com.gio.cs2api.service.SimulacionService;

import java.util.Map;

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
public class SimulacionController {

    private final SimulacionService simulacionService;
    private final JugadorRepository jugadorRepository;

    public SimulacionController(
            SimulacionService simulacionService,
            JugadorRepository jugadorRepository) {
        this.simulacionService = simulacionService;
        this.jugadorRepository = jugadorRepository;
    }

    @PostMapping("/jugadores")
    public ResponseEntity<Jugador> crearJugador(
            @RequestBody CrearJugadorRequest solicitud) {

        int vida = solicitud.vida() == null ? 100 : solicitud.vida();
        int reserva = solicitud.municionReserva() == null
                ? 90 : solicitud.municionReserva();

        Jugador jugador = new Jugador(
                solicitud.nombre(), vida, reserva);

        Jugador guardado = jugadorRepository.save(jugador);

        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    @GetMapping("/jugadores/{id}")
    public ResponseEntity<Jugador> buscarJugador(@PathVariable Long id) {
        return jugadorRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/acciones/{armaId}")
    public ResponseEntity<Map<String, Object>> consultarAcciones(
            @PathVariable Long armaId) {
        return ResponseEntity.ok(
                simulacionService.consultarAcciones(armaId));
    }
    @PostMapping("/disparar")
    public ResponseEntity<Map<String, Object>> disparar(
            @RequestParam Long rifleId,
            @RequestParam Long jugadorId,
            @RequestParam(defaultValue = "50") int distancia) {

        return ResponseEntity.ok(
                simulacionService.disparar(rifleId, jugadorId, distancia));
    }

    @PostMapping("/recargar")
    public ResponseEntity<Map<String, Object>> recargar(
            @RequestParam Long armaId,
            @RequestParam Long jugadorId) {

        return ResponseEntity.ok(
                simulacionService.recargar(armaId, jugadorId));
    }

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