
package com.gio.cs2api.service;

import com.gio.cs2api.domain.Jugador;
import com.gio.cs2api.repository.JugadorRepository;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JugadorService {

    private final JugadorRepository jugadorRepository;

    public JugadorService(JugadorRepository jugadorRepository) {
        this.jugadorRepository = jugadorRepository;
    }

    @Transactional
    public Jugador crear(
            String nombre,
            Integer vida,
            Integer municionReserva) {

        int vidaInicial = vida == null ? 100 : vida;
        int reservaInicial =
                municionReserva == null ? 90 : municionReserva;

        validarDatos(nombre, vidaInicial, reservaInicial);

        Jugador jugador = new Jugador(
                nombre,
                vidaInicial,
                reservaInicial);

        return jugadorRepository.save(jugador);
    }

    @Transactional(readOnly = true)
    public Optional<Jugador> buscarPorId(Long id) {
        return jugadorRepository.findById(id);
    }

    private void validarDatos(
            String nombre,
            int vida,
            int municionReserva) {

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre del jugador es obligatorio");
        }

        if (vida <= 0) {
            throw new IllegalArgumentException(
                    "La vida inicial debe ser mayor que 0");
        }

        if (municionReserva < 0) {
            throw new IllegalArgumentException(
                    "La reserva de munición no puede ser negativa");
        }
    }
}
