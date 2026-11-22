package com.gio.cs2api.domain;

public abstract class Granada extends Arma {

    private final String tipoGranada;
    private final float radioExplosion;

    public Granada(
            String nombre,
            float precio,
            String tipoGranada,
            float radioExplosion) {

        super(nombre, precio, 0, 1.0f, 1);

        this.tipoGranada = tipoGranada;
        this.radioExplosion = radioExplosion;
    }

    @Override
    public String disparar() {
        consumirMunicion();
        return efectoLanzamiento();
    }

    protected abstract String efectoLanzamiento();

    public String getTipoGranada() {
        return tipoGranada;
    }

    public float getRadioExplosion() {
        return radioExplosion;
    }
}