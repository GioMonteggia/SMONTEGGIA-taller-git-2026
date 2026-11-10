package com.gio.cs2api.repository;

import com.gio.cs2api.domain.Jugador;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JugadorRepository extends JpaRepository<Jugador, Long> {
}