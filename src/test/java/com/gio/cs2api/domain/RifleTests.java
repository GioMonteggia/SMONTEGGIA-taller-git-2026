
package com.gio.cs2api.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import com.gio.cs2api.service.RifleDemostracionService;
import org.junit.jupiter.api.Test;

class RifleTests {

    private final RifleDemostracionService servicio =
            new RifleDemostracionService();

    @Test
    void constructorSimpleUsaElRifleDeFabrica() {
        Rifle rifle = new Rifle("AK-47", 2700f);

        assertEquals(30, rifle.getMunicionMax());
        assertEquals(36, rifle.getDano());
        assertEquals(30, rifle.getCargador());
    }

    @Test
    void constructorSobrecargadoUsaLoQuePasaElLlamador() {
        Rifle rifle = new Rifle("M4A4", 3100f, 33, 20);

        assertEquals(20, rifle.getMunicionMax());
        assertEquals(33, rifle.getDano());
    }

    @Test
    void servicioValidaLosDatosAntesDeCrearODisparar() {
        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.detalle("AK-47", 2700f, 36, 0));

        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.detalle("AK-47", 2700f, 0, 30));

        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.disparar("AK-47", 2700f, 30, -1, 1));

        assertThrows(
                MunicionAgotadaException.class,
                () -> servicio.disparar("AK-47", 2700f, 30, 50, 31));
    }

    @Test
    void dispararConsumeMunicionYCalculaElDanoSegunDistancia() {
        Map<String, Object> cerca =
                servicio.disparar("AK-47", 2700f, 30, 0, 1);

        Map<String, Object> lejos =
                servicio.disparar("AK-47", 2700f, 30, 100, 1);

        assertEquals(29, cerca.get("municionRestante"));
        assertEquals(29, lejos.get("municionRestante"));

        assertTrue(
                ((String) cerca.get("disparos")).contains("causa 31"));

        assertTrue(
                ((String) lejos.get("disparos")).contains("causa 15"));
    }

    @Test
    void elDanoNoBajaDeLaMitadPorMuyLejosQueEsten() {
        Map<String, Object> normal =
                servicio.disparar("AK-47", 2700f, 30, 100, 1);

        Map<String, Object> muyLejos =
                servicio.disparar("AK-47", 2700f, 30, 400, 1);

        assertTrue(
                ((String) normal.get("disparos")).contains("causa 15"));

        assertTrue(
                ((String) muyLejos.get("disparos")).contains("causa 15"));
    }
}
