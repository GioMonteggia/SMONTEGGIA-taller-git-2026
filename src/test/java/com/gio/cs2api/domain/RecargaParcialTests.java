package com.gio.cs2api.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class RecargaParcialTests {

    @Test
    void recargarTransfiereSoloLaMunicionDisponible() {
        Rifle rifle = new Rifle("AK-47", 2700f, 36, 30);
        for (int i = 0; i < 5; i++) {
            rifle.disparar();
        }
        assertEquals(25, rifle.getMunicionActual());

        assertEquals(3, rifle.recargar(3));
        assertEquals(28, rifle.getMunicionActual());

        assertEquals(2, rifle.recargar(10));
        assertEquals(30, rifle.getMunicionActual());
    }

    @Test
    void recargarConCantidadNegativaNoModificaElCargador() {
        Rifle rifle = new Rifle("M4A4", 3100f, 33, 20);
        rifle.disparar();

        assertEquals(0, rifle.recargar(-5));
        assertEquals(19, rifle.getMunicionActual());
    }
}
