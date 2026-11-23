package com.gio.cs2api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;

import com.gio.cs2api.domain.Jugada;
import com.gio.cs2api.domain.Jugador;
import com.gio.cs2api.domain.Rifle;
import com.gio.cs2api.repository.ArmaRepository;
import com.gio.cs2api.repository.JugadaRepository;
import com.gio.cs2api.repository.JugadorRepository;
import com.gio.cs2api.service.EscenarioService.SolicitudEscenario;
import com.gio.cs2api.service.ValidadorCompanero.ResultadoValidacion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EscenarioServiceTests {

    @Mock
    private ArmaRepository armaRepository;

    @Mock
    private JugadorRepository jugadorRepository;

    @Mock
    private JugadaRepository jugadaRepository;

    @Mock
    private ValidadorCompanero a2;

    @Mock
    private ValidadorCompanero a3;

    private EscenarioService servicio;

    @BeforeEach
    void preparar() {
        servicio = new EscenarioService(
                armaRepository, jugadorRepository, jugadaRepository, a2, a3,
                new ReglasCombateService());

        Rifle rifle = new Rifle("AK-47", 2700f, 36, 30);
        rifle.disparar();

        when(armaRepository.findById(1L)).thenReturn(Optional.of(rifle));
        when(jugadorRepository.findById(2L))
                .thenReturn(Optional.of(new Jugador("Jugador", 100, 90)));
    }

    @Test
    void soloGuardaSiLosDosCompanerosAceptan() {
        when(a2.validar(anyMap())).thenReturn(
                new ResultadoValidacion(true, "A2 ok", 15, null, 200));
        when(a3.validar(anyMap())).thenReturn(
                new ResultadoValidacion(true, "A3 ok", null, 1, 200));
        when(jugadaRepository.save(any(Jugada.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        Jugada jugada = servicio.ejecutar(new SolicitudEscenario(1L, 2L, 50));

        assertTrue(jugada.isAceptadaPorA2());
        assertTrue(jugada.isAceptadaPorA3());
        assertEquals(15, jugada.getDanoEfectivo());
        assertEquals(1, jugada.getMunicionTransferida());
        verify(jugadaRepository).save(any(Jugada.class));
    }

    @Test
    void a2RecibeUnBodyCompatibleConSuCreacionDeArma() {
        when(a2.validar(anyMap())).thenReturn(
                new ResultadoValidacion(true, "A2 ok", null, null, 201));
        when(a3.validar(anyMap())).thenReturn(
                new ResultadoValidacion(true, "A3 ok", null, null, 200));
        when(jugadaRepository.save(any(Jugada.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        servicio.ejecutar(new SolicitudEscenario(1L, 2L, 35, false));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> captor =
                ArgumentCaptor.forClass(Map.class);
        verify(a2).validar(captor.capture());
        Map<String, Object> enviado = captor.getValue();
        assertEquals("FRANCOTIRADOR", enviado.get("tipo"));
        assertEquals(2700f, enviado.get("precio"));
        assertEquals(36, enviado.get("dano"));
        assertEquals(2, enviado.get("zoom"));
    }

    @Test
    void a3RecibeLosNombresDeCampoDeSuOpenAPI() {
        when(a2.validar(anyMap())).thenReturn(
                new ResultadoValidacion(true, "A2 ok", 15, null, 200));
        when(a3.validar(anyMap())).thenReturn(
                new ResultadoValidacion(true, "A3 ok", null, null, 200));
        when(jugadaRepository.save(any(Jugada.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        servicio.ejecutar(new SolicitudEscenario(1L, 2L, 50, true));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> captor =
                ArgumentCaptor.forClass(Map.class);
        verify(a3).validar(captor.capture());
        Map<String, Object> enviado = captor.getValue();
        assertEquals(2, enviado.size());
        assertEquals(true, enviado.get("tieneBalas"));
        assertEquals(true, enviado.get("granadaLanzada"));
    }

    @Test
    void rechazoDeA2NoEscribeEnLaBaseYNoLlamaAA3() {
        when(a2.rol()).thenReturn("A2");
        when(a2.regla()).thenReturn("regla de disparo");
        when(a2.validar(anyMap())).thenReturn(
                new ResultadoValidacion(false, "Sin munición", null, null, 400));

        RechazoCompaneroException ex = assertThrows(
                RechazoCompaneroException.class,
                () -> servicio.ejecutar(new SolicitudEscenario(1L, 2L, 50)));

        assertEquals("A2", ex.getRol());
        assertEquals(400, ex.getResultado().codigoHttp());
        verify(a3, never()).validar(anyMap());
        verify(jugadaRepository, never()).save(any(Jugada.class));
    }

    @Test
    void rechazoDeA3NoEscribeEnLaBase() {
        when(a2.validar(anyMap())).thenReturn(
                new ResultadoValidacion(true, "A2 ok", 15, null, 200));
        when(a3.rol()).thenReturn("A3");
        when(a3.regla()).thenReturn("regla de combate de A3");
        when(a3.validar(anyMap())).thenReturn(
                new ResultadoValidacion(false, "Cargador lleno", null, 0, 400));

        RechazoCompaneroException ex = assertThrows(
                RechazoCompaneroException.class,
                () -> servicio.ejecutar(new SolicitudEscenario(1L, 2L, 50)));

        assertEquals("A3", ex.getRol());
        verify(jugadaRepository, never()).save(any(Jugada.class));
    }
}
