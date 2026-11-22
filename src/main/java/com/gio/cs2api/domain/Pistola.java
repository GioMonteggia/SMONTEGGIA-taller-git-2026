
package com.gio.cs2api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("PISTOLA")
public class Pistola extends Arma implements AccionCombate {

    @Column(name = "pistola_modo_disparo")
    private String modoDisparo;

    @Column(name = "pistola_cargador")
    private int cargador;

    protected Pistola() {
        // Constructor requerido por JPA.
    }

    public Pistola(String nombre, float precio) {
        super(nombre, precio, 35, 1.0f, 12);
        this.modoDisparo = "Semiautomático";
        this.cargador = 12;
    }

    public Pistola(
            String nombre,
            float precio,
            int dano,
            int cargador) {

        super(nombre, precio, dano, 1.0f, cargador);
        this.modoDisparo = "Semiautomático";
        this.cargador = cargador;
    }

    @Override
    public String disparar() {
        consumirMunicion();
        return obtenerNombre() + " dispara.";
    }

    public String disparar(int distancia) {
        consumirMunicion();
        return obtenerNombre() + " dispara a " + distancia + " metros.";
    }

    public void actualizarEspecificos(Pistola otra) {
        this.cargador = otra.cargador;
        this.modoDisparo = otra.modoDisparo;
    }

    public String getModoDisparo() {
        return modoDisparo;
    }

    public int getCargador() {
        return cargador;
    }
}
