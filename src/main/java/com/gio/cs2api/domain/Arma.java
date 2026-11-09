package com.gio.cs2api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_arma")
public abstract class Arma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private float precio;

    @Column(nullable = false)
    private int dano;

    @Column(nullable = false)
    private float peso;

    @Column(nullable = false)
    private int municionMax;

    @Column(nullable = false)
    private int municionActual;

    protected Arma() {
        // Constructor requerido por JPA.
    }

    public Arma(String nombre, float precio, int dano, float peso, int municionMax) {
        validarDatos(nombre, precio, dano, peso, municionMax);

        this.nombre = nombre;
        this.precio = precio;
        this.dano = dano;
        this.peso = peso;
        this.municionMax = municionMax;
        this.municionActual = municionMax;
    }

    private void validarDatos(
            String nombre,
            float precio,
            int dano,
            float peso,
            int municionMax
    ) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }

        if (precio < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }

        if (dano < 0) {
            throw new IllegalArgumentException("El daño no puede ser negativo");
        }

        if (peso <= 0) {
            throw new IllegalArgumentException("El peso debe ser mayor que 0");
        }

        if (municionMax <= 0) {
            throw new IllegalArgumentException("La munición máxima debe ser mayor que 0");
        }
    }

    public abstract String disparar();

    public String recargar() {
        municionActual = municionMax;
        return nombre + " recargada.";
    }

    public String obtenerPrecio() {
        return "Precio: $" + precio;
    }

    public String obtenerNombre() {
        return nombre;
    }

    public void actualizarDesde(Arma otra) {
        if (otra == null) {
            throw new IllegalArgumentException("El arma actualizada no puede ser nula");
        }

        if (getClass() != otra.getClass()) {
            throw new IllegalArgumentException(
                    "No se puede cambiar el tipo de arma durante una actualización"
            );
        }

        validarDatos(
                otra.nombre,
                otra.precio,
                otra.dano,
                otra.peso,
                otra.municionMax
        );

        this.nombre = otra.nombre;
        this.precio = otra.precio;
        this.dano = otra.dano;
        this.peso = otra.peso;
        this.municionMax = otra.municionMax;
        this.municionActual = otra.municionActual;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public float getPrecio() {
        return precio;
    }

    public int getDano() {
        return dano;
    }

    public float getPeso() {
        return peso;
    }

    public int getMunicionMax() {
        return municionMax;
    }

    public int getMunicionActual() {
        return municionActual;
    }

    protected void consumirMunicion() {
        if (municionActual > 0) {
            municionActual--;
        }
    }
}
