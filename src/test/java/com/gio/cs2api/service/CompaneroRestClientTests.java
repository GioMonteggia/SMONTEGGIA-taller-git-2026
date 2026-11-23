package com.gio.cs2api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.util.LinkedHashMap;
import java.util.Map;

import tools.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class CompaneroRestClientTests {

    private MockRestServiceServer servidor;
    private CompaneroRestClient cliente;

    @BeforeEach
    void preparar() {
        RestClient.Builder constructor = RestClient.builder();
        servidor = MockRestServiceServer.bindTo(constructor).build();
        cliente = new CompaneroRestClient(
                "A2",
                "regla de disparo",
                "http://companero.test",
                "/api/reglas/disparo",
                new ObjectMapper(),
                constructor);
    }

    @Test
    void interpretaUnaRespuestaAceptadaDelOpenAPI() {
        servidor.expect(requestTo("http://companero.test/api/reglas/disparo"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json(
                        "{\"municionActual\":29,\"danoBase\":36,"
                        + "\"precision\":0.85,\"distancia\":50}"))
                .andRespond(withSuccess(
                        "{\"aceptada\":true,\"regla\":\"DISPARO\","
                        + "\"mensaje\":\"La regla acepta el disparo.\","
                        + "\"danoEfectivo\":15}",
                        MediaType.APPLICATION_JSON));

        var resultado = cliente.validar(datosDisparo());

        assertTrue(resultado.aceptada());
        assertEquals(15, resultado.danoEfectivo());
        assertEquals(200, resultado.codigoHttp());
        servidor.verify();
    }

    @Test
    void interpretaUnRechazoConHttp400() {
        servidor.expect(requestTo("http://companero.test/api/reglas/disparo"))
                .andRespond(withBadRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"aceptada\":false,\"mensaje\":\"Sin munición\"}"));

        var resultado = cliente.validar(datosDisparo());

        assertFalse(resultado.aceptada());
        assertEquals("Sin munición", resultado.mensaje());
        assertEquals(400, resultado.codigoHttp());
        servidor.verify();
    }

    @Test
    void unErrorDeTransporteSeAtribuyeAlCompanero() {
        servidor.expect(requestTo("http://companero.test/api/reglas/disparo"))
                .andRespond(request -> {
                    throw new java.net.ConnectException("sin conexión");
                });

        CompaneroNoDisponibleException ex = assertThrows(
                CompaneroNoDisponibleException.class,
                () -> cliente.validar(datosDisparo()));

        assertEquals("A2", ex.getRol());
        servidor.verify();
    }

    private static Map<String, Object> datosDisparo() {
        Map<String, Object> datos = new LinkedHashMap<>();
        datos.put("municionActual", 29);
        datos.put("danoBase", 36);
        datos.put("precision", 0.85f);
        datos.put("distancia", 50);
        return datos;
    }
}
