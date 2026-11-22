package com.gio.cs2api.domain;

public class Escopeta extends Arma {

    private final int cartuchos;
    private final float dispersion;

    public Escopeta(String nombre, float precio) {
        super(nombre, precio, 80, 3.0f, 8);
        this.cartuchos = 8;
        this.dispersion = 1.5f;
    }

    public Escopeta(
            String nombre,
            float precio,
            int dano,
            int cartuchos,
            float dispersion) {

        super(nombre, precio, dano, 3.0f, cartuchos);
        this.cartuchos = cartuchos;
        this.dispersion = dispersion;
    }

    @Override
    public String disparar() {
        consumirMunicion();
        return obtenerNombre() + " dispara una ráfaga de escopeta.";
    }

    public int getCartuchos() {
        return cartuchos;
    }

    public float getDispersion() {
        return dispersion;
    }
}