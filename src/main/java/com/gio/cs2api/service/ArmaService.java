package com.gio.cs2api.service;

import com.gio.cs2api.domain.Arma;
import com.gio.cs2api.domain.ArmaNoEncontradaException;
import com.gio.cs2api.domain.Pistola;
import com.gio.cs2api.domain.Rifle;
import com.gio.cs2api.repository.ArmaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ArmaService {

    private final ArmaRepository armaRepository;

    public ArmaService(ArmaRepository armaRepository) {
        this.armaRepository = armaRepository;
    }

    @Transactional(readOnly = true)
    public List<Arma> listar() {
        return armaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Arma buscarPorId(Long id) {
        return armaRepository.findById(id)
                .orElseThrow(() -> new ArmaNoEncontradaException(
                        "No existe un arma con ID " + id));
    }

    @Transactional
    public Arma crear(Map<String, Object> datos) {
        return armaRepository.save(construirArma(datos));
    }

    @Transactional
    public Arma actualizar(Long id, Map<String, Object> datos) {
        Arma existente = buscarPorId(id);
        Arma nueva = construirArma(datos);

        if (!existente.getClass().equals(nueva.getClass())) {
            throw new IllegalArgumentException(
                    "No se puede cambiar el tipo de arma durante una actualización");
        }

        existente.actualizarDesde(nueva);

        if (existente instanceof Rifle rifleExistente
                && nueva instanceof Rifle rifleNuevo) {
            rifleExistente.actualizarEspecificos(rifleNuevo);
        } else if (existente instanceof Pistola pistolaExistente
                && nueva instanceof Pistola pistolaNueva) {
            pistolaExistente.actualizarEspecificos(pistolaNueva);
        }

        return armaRepository.save(existente);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!armaRepository.existsById(id)) {
            throw new ArmaNoEncontradaException(
                    "No existe un arma con ID " + id);
        }

        armaRepository.deleteById(id);
    }

    private Arma construirArma(Map<String, Object> datos) {
        if (datos == null) {
            throw new IllegalArgumentException(
                    "Los datos del arma son obligatorios");
        }

        String tipo = texto(datos, "tipo").toLowerCase(Locale.ROOT);
        String nombre = texto(datos, "nombre");
        float precio = numero(datos, "precio").floatValue();

        int dano;
        int cargador;

        switch (tipo) {
            case "rifle" -> {
                dano = entero(datos, "dano", 36);
                cargador = entero(datos, "cargador", 30);
            }
            case "pistola" -> {
                dano = entero(datos, "dano", 35);
                cargador = entero(datos, "cargador", 12);
            }
            default -> throw new IllegalArgumentException(
                    "Tipo no admitido para este CRUD: " + tipo);
        }

        validarDatos(nombre, precio, dano, cargador);

        return switch (tipo) {
            case "rifle" ->
                    new Rifle(nombre, precio, dano, cargador);
            case "pistola" ->
                    new Pistola(nombre, precio, dano, cargador);
            default -> throw new IllegalArgumentException(
                    "Tipo no admitido para este CRUD: " + tipo);
        };
    }

    private void validarDatos(
            String nombre,
            float precio,
            int dano,
            int cargador) {

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre del arma es obligatorio");
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

    private String texto(Map<String, Object> datos, String campo) {
        Object valor = datos.get(campo);

        if (!(valor instanceof String texto) || texto.isBlank()) {
            throw new IllegalArgumentException(
                    "El campo '" + campo
                            + "' es obligatorio y debe ser texto");
        }

        return texto;
    }

    private Number numero(Map<String, Object> datos, String campo) {
        Object valor = datos.get(campo);

        if (!(valor instanceof Number numero)) {
            throw new IllegalArgumentException(
                    "El campo '" + campo
                            + "' es obligatorio y debe ser numérico");
        }

        return numero;
    }

    private int entero(
            Map<String, Object> datos,
            String campo,
            int predeterminado) {

        Object valor = datos.get(campo);

        if (valor == null) {
            return predeterminado;
        }

        if (!(valor instanceof Number numero)) {
            throw new IllegalArgumentException(
                    "El campo '" + campo + "' debe ser numérico");
        }

        try {
            return new BigDecimal(numero.toString()).intValueExact();
        } catch (ArithmeticException | NumberFormatException exception) {
            throw new IllegalArgumentException(
                    "El campo '" + campo
                            + "' debe ser un número entero");
        }
    }
}