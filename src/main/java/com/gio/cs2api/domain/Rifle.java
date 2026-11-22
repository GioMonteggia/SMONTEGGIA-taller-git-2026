
package com.gio.cs2api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("RIFLE")
public class Rifle extends Arma implements AccionCombate {

    @Column(name = "rifle_cargador")
    private int cargador;

    @Column(name = "precision_arma")
    private float precision;

    protected Rifle() {
        // Constructor requerido por JPA.
    }

    public Rifle(String nombre, float precio) {
        this(nombre, precio, 36, 30);
    }

    public Rifle(String nombre, float precio, int dano, int cargador) {
        super(nombre, precio, dano, 3.7f, cargador);
        this.cargador = cargador;
        this.precision = 0.85f;
    }

    @Override
    public String disparar() {
        return disparar(100);
    }

    public String disparar(int distancia) {
        consumirMunicion();
        return obtenerNombre() + " dispara a " + distancia + " m.";
    }

    public void actualizarEspecificos(Rifle otro) {
        this.cargador = otro.cargador;
        this.precision = otro.precision;
    }

    public int getCargador() {
        return cargador;
    }

    public float getPrecision() {
        return precision;
    }
}
