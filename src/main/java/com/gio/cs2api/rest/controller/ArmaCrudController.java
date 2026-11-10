package com.gio.cs2api.rest.controller;

import com.gio.cs2api.domain.Arma;
import com.gio.cs2api.domain.ArmaNoEncontradaException;
import com.gio.cs2api.domain.Pistola;
import com.gio.cs2api.domain.Rifle;
import com.gio.cs2api.repository.ArmaRepository;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/armas")
public class ArmaCrudController {

    private final ArmaRepository armaRepository;

    public ArmaCrudController(ArmaRepository armaRepository) {
        this.armaRepository = armaRepository;
    }

    @GetMapping
    public List<Arma> listar() {
        return armaRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Arma> buscarPorId(@PathVariable Long id) {
        Arma arma = armaRepository.findById(id)
                .orElseThrow(() -> new ArmaNoEncontradaException(
                        "No existe un arma con ID " + id
                ));
        return ResponseEntity.ok(arma);
    }
    @PostMapping
    public ResponseEntity<Arma> crear(@RequestBody Map<String, Object> datos) {
        Arma arma = construirArma(datos);
        Arma guardada = armaRepository.save(arma);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable Long id,
            @RequestBody Map<String, Object> datos) {

        return armaRepository.findById(id)
                .<ResponseEntity<?>>map(existente -> {
                    Arma nueva = construirArma(datos);

                    if (!existente.getClass().equals(nueva.getClass())) {
                        return ResponseEntity.badRequest().body(
                                Map.of("error",
                                        "No se puede cambiar el tipo de arma durante una actualización")
                        );
                    }

                    existente.actualizarDesde(nueva);

                    if (existente instanceof Rifle rifleExistente
                            && nueva instanceof Rifle rifleNuevo) {
                        rifleExistente.actualizarEspecificos(rifleNuevo);
                    } else if (existente instanceof Pistola pistolaExistente
                            && nueva instanceof Pistola pistolaNueva) {
                        pistolaExistente.actualizarEspecificos(pistolaNueva);
                    }

                    return ResponseEntity.ok(armaRepository.save(existente));
                })
                .orElseThrow(() -> new ArmaNoEncontradaException("No existe un arma con ID " + id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!armaRepository.existsById(id)) {
            throw new ArmaNoEncontradaException("No existe un arma con ID " + id);
        }

        armaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private Arma construirArma(Map<String, Object> datos) {
        String tipo = texto(datos, "tipo").toLowerCase();
        String nombre = texto(datos, "nombre");
        float precio = numero(datos, "precio").floatValue();

        return switch (tipo) {
            case "rifle" -> new Rifle(
                    nombre,
                    precio,
                    entero(datos, "dano", 36),
                    entero(datos, "cargador", 30)
            );
            case "pistola" -> new Pistola(
                    nombre,
                    precio,
                    entero(datos, "dano", 35),
                    entero(datos, "cargador", 12)
            );
            default -> throw new IllegalArgumentException(
                    "Tipo no admitido para este CRUD: " + tipo
            );
        };
    }

    private String texto(Map<String, Object> datos, String campo) {
        Object valor = datos.get(campo);
        if (!(valor instanceof String texto) || texto.isBlank()) {
            throw new IllegalArgumentException(
                    "El campo '" + campo + "' es obligatorio y debe ser texto"
            );
        }
        return texto;
    }

    private Number numero(Map<String, Object> datos, String campo) {
        Object valor = datos.get(campo);
        if (!(valor instanceof Number numero)) {
            throw new IllegalArgumentException(
                    "El campo '" + campo + "' es obligatorio y debe ser numérico"
            );
        }
        return numero;
    }

    private int entero(Map<String, Object> datos, String campo, int predeterminado) {
        Object valor = datos.get(campo);
        if (valor == null) {
            return predeterminado;
        }
        if (!(valor instanceof Number numero)) {
            throw new IllegalArgumentException(
                    "El campo '" + campo + "' debe ser numérico"
            );
        }
        try {
            return new java.math.BigDecimal(numero.toString()).intValueExact();
        } catch (ArithmeticException | NumberFormatException exception) {
            throw new IllegalArgumentException(
                    "El campo '" + campo + "' debe ser un número entero"
            );
        }
    }
}
