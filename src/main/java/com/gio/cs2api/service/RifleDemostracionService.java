
package com.gio.cs2api.service;

import com.gio.cs2api.domain.MunicionAgotadaException;
import com.gio.cs2api.domain.Rifle;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class RifleDemostracionService {

    public Map<String, Object> detalle(
            String nombre,
            float precio,
            int dano,
            int cargador) {

        validarDatos(nombre, precio, dano, cargador);

        Rifle rifle = new Rifle(nombre, precio, dano, cargador);

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("nombre", rifle.getNombre());
        respuesta.put("tipo", "Rifle");
        respuesta.put("precio", rifle.getPrecio());
        respuesta.put("dano", rifle.getDano());
        respuesta.put("peso", rifle.getPeso());
        respuesta.put("cargador", rifle.getCargador());
        respuesta.put(
                "municion",
                rifle.getMunicionActual() + "/" + rifle.getMunicionMax());

        return respuesta;
    }

    public Map<String, Object> disparar(
            String nombre,
            float precio,
            int cargador,
            Integer distancia,
            int veces) {

        validarDatos(nombre, precio, 36, cargador);

        if (veces < 1 || veces > 50) {
            throw new IllegalArgumentException(
                    "veces debe estar entre 1 y 50");
        }

        if (distancia != null && distancia < 0) {
            throw new IllegalArgumentException(
                    "La distancia no puede ser negativa");
        }

        if (veces > cargador) {
            throw new MunicionAgotadaException(
                    "No hay suficiente munición para realizar todos los disparos");
        }

        Rifle rifle = new Rifle(nombre, precio, 36, cargador);
        int distanciaDisparo = distancia == null ? 100 : distancia;

        StringBuilder disparos = new StringBuilder();

        for (int i = 0; i < veces; i++) {
            rifle.disparar(distanciaDisparo);

            int dano = calcularDanioEfectivo(
                    rifle.getDano(),
                    distanciaDisparo,
                    rifle.getPrecision());

            disparos.append(rifle.getNombre())
                    .append(" dispara a ")
                    .append(distanciaDisparo)
                    .append(" m y causa ")
                    .append(dano)
                    .append(" de daño.");

            if (i < veces - 1) {
                disparos.append(" | ");
            }
        }

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("disparos", disparos.toString());
        respuesta.put("municionRestante", rifle.getMunicionActual());

        return respuesta;
    }

    private int calcularDanioEfectivo(
            int danoBase,
            int distancia,
            float precision) {

        float factor = Math.max(0.5f, 1f - distancia / 100f);
        return Math.round(danoBase * factor * precision);
    }

    private void validarDatos(
            String nombre,
            float precio,
            int dano,
            int cargador) {

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre del rifle es obligatorio");
        }

        if (!Float.isFinite(precio) || precio < 0) {
            throw new IllegalArgumentException(
                    "El precio debe ser válido y no negativo");
        }

        if (dano <= 0) {
            throw new IllegalArgumentException(
                    "El daño debe ser mayor que 0");
        }

        if (cargador <= 0) {
            throw new IllegalArgumentException(
                    "El cargador debe ser mayor que 0");
        }
    }
}
