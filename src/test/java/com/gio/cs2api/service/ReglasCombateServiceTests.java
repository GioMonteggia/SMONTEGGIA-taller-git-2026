package com.gio.cs2api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ReglasCombateServiceTests {

    private final ReglasCombateService servicio = new ReglasCombateService();

    @Test
    void disparoValidoDevuelveElDanioEfectivo() {
        var resultado = servicio.validarDisparo(29, 36, 0.85f, 50);

        assertTrue(resultado.aceptada());
        assertEquals(15, resultado.danoEfectivo());
    }

    @Test
    void disparoRechazadoPorFaltaDeMunicion() {
        var resultado = servicio.validarDisparo(0, 36, 0.85f, 50);

        assertFalse(resultado.aceptada());
        assertEquals("DISPARO", resultado.regla());
    }

    @Test
    void disparoRechazadoPorPrecisionFueraDeRango() {
        assertFalse(servicio.validarDisparo(10, 36, 0f, 50).aceptada());
        assertFalse(servicio.validarDisparo(10, 36, 1.1f, 50).aceptada());
    }

    @Test
    void recargaParcialAceptaTransferenciaMenorALaNecesaria() {
        var resultado = servicio.validarRecarga(29, 30, 3);

        assertTrue(resultado.aceptada());
        assertEquals(1, resultado.municionATransferir());
    }

    @Test
    void recargaRechazadaPorCargadorLlenoOSinReserva() {
        assertFalse(servicio.validarRecarga(30, 30, 10).aceptada());
        assertFalse(servicio.validarRecarga(25, 30, 0).aceptada());
    }
}
