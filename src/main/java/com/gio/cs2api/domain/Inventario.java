package com.gio.cs2api.domain;

import java.util.ArrayList;
import java.util.List;

public class Inventario {

    private final List<Arma> armas;

    public Inventario() {
        this.armas = new ArrayList<>();
    }

    public void agregarArma(Arma arma) {
        armas.add(arma);
    }

    public List<Arma> getArmas() {
        return List.copyOf(armas);
    }
}