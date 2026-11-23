package com.gio.cs2api.service;

import org.springframework.stereotype.Service;

@Service
public class ReglasCombateService {

    public ResultadoDisparo validarDisparo(
            Integer municionActual,
            Integer danoBase,
            Float precision,
            Integer distancia) {

        if (municionActual == null
                || danoBase == null
                || precision == null
                || distancia == null) {
            return new ResultadoDisparo(
                    false,
                    "DISPARO",
                    "Todos los datos del disparo son obligatorios.",
                    null);
        }

        if (municionActual <= 0) {
            return new ResultadoDisparo(
                    false,
                    "DISPARO",
                    "El rifle no tiene munición.",
                    null);
        }

        if (danoBase <= 0) {
            return new ResultadoDisparo(
                    false,
                    "DISPARO",
                    "El daño base debe ser mayor que cero.",
                    null);
        }

        if (precision <= 0 || precision > 1) {
            return new ResultadoDisparo(
                    false,
                    "DISPARO",
                    "La precisión debe ser mayor que cero y como máximo igual a uno.",
                    null);
        }

        if (distancia < 0) {
            return new ResultadoDisparo(
                    false,
                    "DISPARO",
                    "La distancia no puede ser negativa.",
                    null);
        }

        float factor = Math.max(
                0.5f,
                1f - distancia / 100f);

        int danoEfectivo = Math.round(
                danoBase * factor * precision);

        if (danoEfectivo <= 0) {
            return new ResultadoDisparo(
                    false,
                    "DISPARO",
                    "El daño efectivo debe ser mayor que cero.",
                    danoEfectivo);
        }

        return new ResultadoDisparo(
                true,
                "DISPARO",
                "La regla acepta el disparo.",
                danoEfectivo);
    }

    public ResultadoRecarga validarRecarga(
            Integer municionActual,
            Integer municionMax,
            Integer municionReserva) {

        if (municionActual == null
                || municionMax == null
                || municionReserva == null) {
            return new ResultadoRecarga(
                    false,
                    "RECARGA",
                    "Todos los datos de la recarga son obligatorios.",
                    null);
        }

        if (municionMax <= 0) {
            return new ResultadoRecarga(
                    false,
                    "RECARGA",
                    "La capacidad máxima del cargador debe ser mayor que cero.",
                    null);
        }

        if (municionActual < 0 || municionActual > municionMax) {
            return new ResultadoRecarga(
                    false,
                    "RECARGA",
                    "La munición actual no es válida para la capacidad del cargador.",
                    null);
        }

        if (municionReserva < 0) {
            return new ResultadoRecarga(
                    false,
                    "RECARGA",
                    "La munición de reserva no puede ser negativa.",
                    null);
        }

        int municionNecesaria = municionMax - municionActual;

        if (municionNecesaria == 0) {
            return new ResultadoRecarga(
                    false,
                    "RECARGA",
                    "El cargador ya está lleno.",
                    0);
        }

        if (municionReserva == 0) {
            return new ResultadoRecarga(
                    false,
                    "RECARGA",
                    "No hay munición de reserva para recargar.",
                    0);
        }

        int municionATransferir = Math.min(
                municionNecesaria,
                municionReserva);

        return new ResultadoRecarga(
                true,
                "RECARGA",
                "La regla acepta la recarga.",
                municionATransferir);
    }

    public record ResultadoDisparo(
            boolean aceptada,
            String regla,
            String mensaje,
            Integer danoEfectivo) {
    }

    public record ResultadoRecarga(
            boolean aceptada,
            String regla,
            String mensaje,
            Integer municionATransferir) {
    }
}