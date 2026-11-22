
package com.gio.cs2api.service;

import com.gio.cs2api.domain.AccionCombate;
import com.gio.cs2api.domain.Arma;
import com.gio.cs2api.domain.ArmaNoEncontradaException;
import com.gio.cs2api.domain.Jugador;
import com.gio.cs2api.domain.MunicionAgotadaException;
import com.gio.cs2api.domain.Rifle;
import com.gio.cs2api.repository.ArmaRepository;
import com.gio.cs2api.repository.JugadorRepository;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SimulacionService {

    private final ArmaRepository armaRepository;
    private final JugadorRepository jugadorRepository;

    public SimulacionService(
            ArmaRepository armaRepository,
            JugadorRepository jugadorRepository) {
        this.armaRepository = armaRepository;
        this.jugadorRepository = jugadorRepository;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> consultarAcciones(Long armaId) {
        Arma arma = armaRepository.findById(armaId)
                .orElseThrow(() -> new ArmaNoEncontradaException(
                        "No existe un arma con ID " + armaId));

        AccionCombate accion = arma;

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("id", arma.getId());
        respuesta.put("nombre", arma.getNombre());
        respuesta.put("tipo", arma.getClass().getSimpleName());
        respuesta.put("descripcion", accion.describirAcciones());
        respuesta.put("municionActual", arma.getMunicionActual());
        respuesta.put("municionMax", arma.getMunicionMax());

        return respuesta;
    }

    @Transactional
    public Map<String, Object> disparar(
            Long rifleId,
            Long jugadorId,
            int distancia) {

        if (distancia < 0) {
            throw new IllegalArgumentException(
                    "La distancia no puede ser negativa");
        }

        Arma arma = armaRepository.findById(rifleId)
                .orElseThrow(() -> new ArmaNoEncontradaException(
                        "No existe un arma con ID " + rifleId));

        if (!(arma instanceof Rifle rifle)) {
            throw new IllegalArgumentException(
                    "La simulación de daño requiere un rifle");
        }

        if (rifle.getMunicionActual() <= 0) {
            throw new MunicionAgotadaException(
                    "No queda munición en " + rifle.getNombre());
        }

        Jugador objetivo = jugadorRepository.findById(jugadorId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe un jugador con ID " + jugadorId));

        int dano = calcularDanioEfectivo(rifle, distancia);

        if (dano <= 0) {
            throw new IllegalArgumentException(
                    "El daño calculado debe ser mayor que 0");
        }

        rifle.disparar(distancia);

        String resultado = rifle.getNombre()
                + " dispara a " + distancia
                + " m y causa " + dano + " de daño.";

        int vidaRestante = Math.max(
                0,
                objetivo.getVida() - dano);

        objetivo.actualizarEstadoCombate(
                vidaRestante,
                objetivo.getMunicionReserva());

        armaRepository.save(rifle);
        jugadorRepository.save(objetivo);

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("mensaje", resultado);
        respuesta.put("rifleId", rifle.getId());
        respuesta.put("municionRestante", rifle.getMunicionActual());
        respuesta.put("jugadorId", objetivo.getId());
        respuesta.put("danoRecibido", dano);
        respuesta.put("vidaRestante", objetivo.getVida());

        return respuesta;
    }

    private int calcularDanioEfectivo(Rifle rifle, int distancia) {
        float factor = Math.max(0.5f, 1f - distancia / 100f);

        return Math.round(
                rifle.getDano() * factor * rifle.getPrecision());
    }

    @Transactional
    public Map<String, Object> recargar(
            Long armaId,
            Long jugadorId) {

        Arma arma = armaRepository.findById(armaId)
                .orElseThrow(() -> new ArmaNoEncontradaException(
                        "No existe un arma con ID " + armaId));

        Jugador jugador = jugadorRepository.findById(jugadorId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe un jugador con ID " + jugadorId));

        int cantidadNecesaria =
                arma.getMunicionMax() - arma.getMunicionActual();

        if (cantidadNecesaria <= 0) {
            throw new IllegalArgumentException(
                    "El cargador ya está lleno");
        }

        if (jugador.getMunicionReserva() < cantidadNecesaria) {
            throw new MunicionAgotadaException(
                    "El jugador no tiene suficiente munición de reserva");
        }

        int reservaRestante =
                jugador.getMunicionReserva() - cantidadNecesaria;

        jugador.actualizarEstadoCombate(
                jugador.getVida(),
                reservaRestante);

        AccionCombate accion = arma;
        String resultado = accion.recargar();

        armaRepository.save(arma);
        jugadorRepository.save(jugador);

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("mensaje", resultado);
        respuesta.put("armaId", arma.getId());
        respuesta.put("municionActual", arma.getMunicionActual());
        respuesta.put("jugadorId", jugador.getId());
        respuesta.put("municionReserva", jugador.getMunicionReserva());

        return respuesta;
    }
}
