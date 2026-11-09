package com.gio.cs2api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("RIFLE")
public class Rifle extends Arma {

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

        if (cargador <= 0) {
            throw new IllegalArgumentException(
                    "El cargador no puede ser 0 o negativo"
            );
        }

        if (dano <= 0) {
            throw new IllegalArgumentException(
                    "El daño no puede ser 0 o negativo"
            );
        }

        this.cargador = cargador;
        this.precision = 0.85f;
    }

    @Override
    public String disparar() {
        return disparar(100);
    }

    public String disparar(int distancia) {
        if (distancia < 0) {
            throw new IllegalArgumentException(
                    "La distancia no puede ser negativa"
            );
        }

        consumirMunicion();

        float factor = Math.max(0.5f, 1f - distancia / 100f);
        int danioEfectivo = Math.round(getDano() * factor * precision);

        return obtenerNombre() + " dispara a " + distancia + " m y causa "
                + danioEfectivo + " de daño.";
    }

    public void actualizarEspecificos(Rifle otro) {
        if (otro == null) {
            throw new IllegalArgumentException("El rifle no puede ser nulo");
        }

        if (otro.cargador <= 0) {
            throw new IllegalArgumentException("El cargador debe ser mayor que 0");
        }

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
