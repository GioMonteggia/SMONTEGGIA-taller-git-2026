
package com.gio.cs2api.rest.controller;

import com.gio.cs2api.service.JugadorDemostracionService;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jugadores")
public class JugadorController {

    private final JugadorDemostracionService jugadorService;

    public JugadorController(
            JugadorDemostracionService jugadorService) {
        this.jugadorService = jugadorService;
    }

    @GetMapping("/demo")
    public ResponseEntity<Map<String, Object>> mostrarJugador() {
        return ResponseEntity.ok(jugadorService.mostrarJugador());
    }

    @GetMapping("/demo/disparar")
    public ResponseEntity<Map<String, Object>> dispararTodas() {
        return ResponseEntity.ok(jugadorService.dispararTodas());
    }

    @GetMapping("/demo/recargar")
    public ResponseEntity<Map<String, Object>> recargarTodas() {
        return ResponseEntity.ok(jugadorService.recargarTodas());
    }

    @GetMapping("/demo/tienda")
    public ResponseEntity<Map<String, Object>> mostrarTienda() {
        return ResponseEntity.ok(jugadorService.mostrarTienda());
    }

    @GetMapping("/demo/disparar/{posicion}")
    public ResponseEntity<Map<String, Object>> dispararArma(
            @PathVariable int posicion) {
        return ResponseEntity.ok(jugadorService.dispararArma(posicion));
    }
}
