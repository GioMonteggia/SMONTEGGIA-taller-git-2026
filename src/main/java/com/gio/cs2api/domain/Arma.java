
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
public abstract class Arma implements AccionCombate {

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

    public Arma(
            String nombre,
            float precio,
            int dano,
            float peso,
            int municionMax) {

        this.nombre = nombre;
        this.precio = precio;
        this.dano = dano;
        this.peso = peso;
        this.municionMax = municionMax;
        this.municionActual = municionMax;
    }

    public abstract String disparar();

    public String recargar() {
        municionActual = municionMax;
        return nombre + " recargada.";
    }

    public String describirAcciones() {
        return obtenerNombre() + ": permite disparar y recargar.";
    }

    public String obtenerPrecio() {
        return "Precio: $" + precio;
    }

    public String obtenerNombre() {
        return nombre;
    }

    public void actualizarDesde(Arma otra) {
        int municionAnterior = this.municionActual;

        this.nombre = otra.nombre;
        this.precio = otra.precio;
        this.dano = otra.dano;
        this.peso = otra.peso;
        this.municionMax = otra.municionMax;

        // Conserva la munición al actualizar el arma.
        // Si el nuevo cargador es menor, ajusta al máximo permitido.
        this.municionActual = Math.min(
                municionAnterior,
                this.municionMax);
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
    municionActual--;
    }
}
