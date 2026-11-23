package com.gio.cs2api.service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.gio.cs2api.domain.Arma;
import com.gio.cs2api.domain.ArmaNoEncontradaException;
import com.gio.cs2api.domain.Jugada;
import com.gio.cs2api.domain.Jugador;
import com.gio.cs2api.domain.Rifle;
import com.gio.cs2api.repository.ArmaRepository;
import com.gio.cs2api.repository.JugadaRepository;
import com.gio.cs2api.repository.JugadorRepository;
import com.gio.cs2api.service.ValidadorCompanero.ResultadoValidacion;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Escenario propio: disparo con recarga parcial. A2 valida el disparo y A3
 * valida la recarga. Sólo se escribe en H2 cuando los dos aceptan.
 */
@Service
public class EscenarioService {

    private final ArmaRepository armaRepository;
    private final JugadorRepository jugadorRepository;
    private final JugadaRepository jugadaRepository;
    private final ValidadorCompanero a2;
    private final ValidadorCompanero a3;

    public EscenarioService(
            ArmaRepository armaRepository,
            JugadorRepository jugadorRepository,
            JugadaRepository jugadaRepository,
            @Qualifier("A2") ValidadorCompanero a2,
            @Qualifier("A3") ValidadorCompanero a3) {

        this.armaRepository = armaRepository;
        this.jugadorRepository = jugadorRepository;
        this.jugadaRepository = jugadaRepository;
        this.a2 = a2;
        this.a3 = a3;
    }

    @Transactional
    public Jugada ejecutar(SolicitudEscenario solicitud) {
        if (solicitud == null
                || solicitud.rifleId() == null
                || solicitud.jugadorId() == null) {
            throw new IllegalArgumentException(
                    "rifleId y jugadorId son obligatorios");
        }

        int distancia = solicitud.distancia() == null
                ? 50
                : solicitud.distancia();

        if (distancia < 0) {
            throw new IllegalArgumentException(
                    "La distancia no puede ser negativa");
        }

        Arma arma = armaRepository.findById(solicitud.rifleId())
                .orElseThrow(() -> new ArmaNoEncontradaException(
                        "No existe un arma con ID " + solicitud.rifleId()));

        if (!(arma instanceof Rifle rifle)) {
            throw new IllegalArgumentException(
                    "El escenario requiere un rifle");
        }

        Jugador jugador = jugadorRepository.findById(solicitud.jugadorId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe un jugador con ID " + solicitud.jugadorId()));

        Map<String, Object> datosDisparo = new LinkedHashMap<>();
        datosDisparo.put("municionActual", rifle.getMunicionActual());
        datosDisparo.put("danoBase", rifle.getDano());
        datosDisparo.put("precision", rifle.getPrecision());
        datosDisparo.put("distancia", distancia);

        Map<String, Object> datosRecarga = new LinkedHashMap<>();
        datosRecarga.put("municionActual", rifle.getMunicionActual());
        datosRecarga.put("municionMax", rifle.getMunicionMax());
        datosRecarga.put("municionReserva", jugador.getMunicionReserva());

        // A2 va primero: sin un disparo válido no tiene sentido pedir recarga.
        ResultadoValidacion respuestaA2 = a2.validar(datosDisparo);
        if (!respuestaA2.aceptada()) {
            throw new RechazoCompaneroException(
                    a2.rol(), a2.regla(), respuestaA2);
        }

        ResultadoValidacion respuestaA3 = a3.validar(datosRecarga);
        if (!respuestaA3.aceptada()) {
            throw new RechazoCompaneroException(
                    a3.rol(), a3.regla(), respuestaA3);
        }

        // Recién aquí se escribe en la base: un rechazo no deja rastro.
        Jugada jugada = new Jugada(
                rifle.getNombre() + " dispara a " + distancia
                        + " m y recarga con la reserva de "
                        + jugador.getNombre(),
                rifle.getId(),
                jugador.getId(),
                distancia,
                valorPorDefecto(respuestaA2.danoEfectivo()),
                valorPorDefecto(respuestaA3.municionTransferida()),
                true,
                true,
                respuestaA2.mensaje(),
                respuestaA3.mensaje(),
                LocalDateTime.now());

        return jugadaRepository.save(jugada);
    }

    @Transactional(readOnly = true)
    public List<Jugada> listar() {
        return jugadaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Jugada buscar(Long id) {
        return jugadaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe una jugada con ID " + id));
    }

    private static int valorPorDefecto(Integer valor) {
        return valor == null ? 0 : valor;
    }

    public record SolicitudEscenario(
            Long rifleId,
            Long jugadorId,
            Integer distancia) {
    }
}
