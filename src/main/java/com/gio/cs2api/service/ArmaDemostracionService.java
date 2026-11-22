package com.gio.cs2api.service;

import com.gio.cs2api.domain.Arma;
import com.gio.cs2api.domain.Escopeta;
import com.gio.cs2api.domain.Francotirador;
import com.gio.cs2api.domain.Granada;
import com.gio.cs2api.domain.GranadaFlash;
import com.gio.cs2api.domain.GranadaHumo;
import com.gio.cs2api.domain.GranadaIncendiaria;
import com.gio.cs2api.domain.GranadaSenuelo;
import com.gio.cs2api.domain.Pistola;
import com.gio.cs2api.domain.Rifle;
import com.gio.cs2api.domain.Subfusil;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class ArmaDemostracionService {

    private final GranadaService granadaService;

    public ArmaDemostracionService(GranadaService granadaService) {
        this.granadaService = granadaService;
    }

    public Map<String, Object> crearArma(
            String tipo,
            String nombre,
            float precio,
            Integer dano,
            Integer cargador) {

        if (tipo == null || tipo.isBlank()) {
            throw new IllegalArgumentException(
                    "El tipo de arma es obligatorio");
        }

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre no puede estar vacío");
        }

        if (!Float.isFinite(precio) || precio < 0) {
            throw new IllegalArgumentException(
                    "El precio debe ser un número válido y no negativo");
        }

        if ((dano == null) != (cargador == null)) {
            throw new IllegalArgumentException(
                    "Los parámetros dano y cargador deben enviarse juntos");
        }

        String tipoNormalizado = tipo.toLowerCase(Locale.ROOT);

        Arma arma = switch (tipoNormalizado) {
            case "pistola" -> {
                if (dano != null) {
                    validarDanoYCargador(dano, cargador);
                    yield new Pistola(nombre, precio, dano, cargador);
                }
                yield new Pistola(nombre, precio);
            }

            case "escopeta" -> {
                if (dano != null) {
                    validarDanoYCargador(dano, cargador);
                    yield new Escopeta(
                            nombre, precio, dano, cargador, 1.5f);
                }
                yield new Escopeta(nombre, precio);
            }

            case "subfusil" -> new Subfusil(nombre, precio);

            case "francotirador" ->
                    new Francotirador(nombre, precio);

            case "rifle" -> {
                if (dano != null) {
                    validarDanoYCargador(dano, cargador);
                    yield new Rifle(nombre, precio, dano, cargador);
                }
                yield new Rifle(nombre, precio);
            }

            case "humo" -> new GranadaHumo(nombre, precio);

            case "flash" -> new GranadaFlash(nombre, precio);

            case "incendiaria" ->
                    new GranadaIncendiaria(nombre, precio);

            case "senuelo" -> new GranadaSenuelo(nombre, precio);

            default -> throw new IllegalArgumentException(
                    "Tipo de arma no válido: " + tipo);
        };

        String mensaje;

        if (arma instanceof Granada granada) {
            mensaje = granadaService.lanzar(granada);
        } else {
            mensaje = arma.disparar();
        }

        Map<String, Object> respuesta = new LinkedHashMap<>();

        respuesta.put("tipo", arma.getClass().getSimpleName());
        respuesta.put("nombre", arma.getNombre());
        respuesta.put("precio", arma.getPrecio());
        respuesta.put("dano", arma.getDano());
        respuesta.put("peso", arma.getPeso());
        respuesta.put("municionMax", arma.getMunicionMax());
        respuesta.put("municionActual", arma.getMunicionActual());
        respuesta.put("mensaje", mensaje);
        respuesta.put("municionRestante", arma.getMunicionActual());

        return respuesta;
    }

    public Map<String, Object> demostrarPolimorfismo() {
        Arma rifle = new Rifle("AK-47", 2700f);
        Arma pistola = new Pistola("Glock", 700f);

        Map<String, Object> rifleRespuesta = new LinkedHashMap<>();
        rifleRespuesta.put("tipo", rifle.getClass().getSimpleName());
        rifleRespuesta.put("mensaje", rifle.disparar());

        Map<String, Object> pistolaRespuesta = new LinkedHashMap<>();
        pistolaRespuesta.put("tipo", pistola.getClass().getSimpleName());
        pistolaRespuesta.put("mensaje", pistola.disparar());

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("rifle", rifleRespuesta);
        respuesta.put("pistola", pistolaRespuesta);

        return respuesta;
    }

    private void validarDanoYCargador(int dano, int cargador) {
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