package com.gio.cs2api.service;

import java.net.URI;
import java.time.Duration;
import java.util.Map;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Cliente HTTP genérico para un compañero. Sólo conoce la URL que publica el
 * OpenAPI ajeno; interpreta el resultado por los nombres de campo del documento
 * y no importa clases ni reglas del proyecto del compañero.
 */
public class CompaneroRestClient implements ValidadorCompanero {

    private final String rol;
    private final String regla;
    private final String urlValidacion;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public CompaneroRestClient(
            String rol,
            String regla,
            String baseUrl,
            String validarPath,
            int timeoutMs,
            ObjectMapper objectMapper) {

        this(rol, regla, baseUrl, validarPath, objectMapper,
                constructorConTimeout(timeoutMs));
    }

    private static RestClient.Builder constructorConTimeout(int timeoutMs) {
        SimpleClientHttpRequestFactory fabrica =
                new SimpleClientHttpRequestFactory();
        fabrica.setConnectTimeout(Duration.ofMillis(timeoutMs));
        fabrica.setReadTimeout(Duration.ofMillis(timeoutMs));
        return RestClient.builder().requestFactory(fabrica);
    }

    /** Constructor de paquete para poder probar el cliente con un servidor simulado. */
    CompaneroRestClient(
            String rol,
            String regla,
            String baseUrl,
            String validarPath,
            ObjectMapper objectMapper,
            RestClient.Builder constructor) {

        this.rol = rol;
        this.regla = regla;
        this.urlValidacion = unirUrls(baseUrl, validarPath);

        this.restClient = constructor
                .defaultHeader(
                        HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
        this.objectMapper = objectMapper;
    }

    @Override
    public String rol() {
        return rol;
    }

    @Override
    public String regla() {
        return regla;
    }

    @Override
    public ResultadoValidacion validar(Map<String, Object> datos) {
        try {
            ResponseEntity<String> respuesta = restClient.post()
                    .uri(URI.create(urlValidacion))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(datos)
                    .retrieve()
                    .onStatus(status -> true, (peticion, respuestaHttp) -> { })
                    .toEntity(String.class);

            String cuerpo = respuesta.getBody();
            JsonNode nodo = cuerpo == null || cuerpo.isBlank()
                    ? objectMapper.createObjectNode()
                    : objectMapper.readTree(cuerpo);

            boolean aceptada = respuesta.getStatusCode().is2xxSuccessful()
                    && leerBooleano(nodo,
                            "aceptada", "aceptado", "aprobada",
                            "valida", "valido", "ok");

            String mensaje = leerTexto(nodo,
                    "mensaje", "message", "detalle", "reason", "error");
            if (mensaje == null || mensaje.isBlank()) {
                mensaje = cuerpo == null ? "" : cuerpo;
            }

            return new ResultadoValidacion(
                    aceptada,
                    mensaje,
                    leerEntero(nodo, "danoEfectivo", "dañoEfectivo"),
                    leerEntero(nodo, "municionTransferida", "municionATransferir"),
                    respuesta.getStatusCode().value());

        } catch (RestClientException exception) {
            throw new CompaneroNoDisponibleException(
                    rol, exception.getMessage());
        } catch (JacksonException exception) {
            throw new CompaneroNoDisponibleException(
                    rol, "respuesta JSON inválida: " + exception.getMessage());
        }
    }

    private static String unirUrls(String baseUrl, String validarPath) {
        String base = baseUrl == null ? "" : baseUrl.trim().replaceAll("/+$", "");
        String ruta = validarPath == null ? "" : validarPath.trim();
        if (!ruta.startsWith("/")) {
            ruta = "/" + ruta;
        }
        return base + ruta;
    }

    private static boolean leerBooleano(JsonNode nodo, String... nombres) {
        for (String nombre : nombres) {
            JsonNode valor = nodo.get(nombre);
            if (valor != null && valor.isBoolean()) {
                return valor.booleanValue();
            }
            if (valor != null && valor.isTextual()) {
                return Boolean.parseBoolean(valor.asText());
            }
        }
        return false;
    }

    private static String leerTexto(JsonNode nodo, String... nombres) {
        for (String nombre : nombres) {
            JsonNode valor = nodo.get(nombre);
            if (valor != null && valor.isValueNode()) {
                return valor.asText();
            }
        }
        return null;
    }

    private static Integer leerEntero(JsonNode nodo, String... nombres) {
        for (String nombre : nombres) {
            JsonNode valor = nodo.get(nombre);
            if (valor != null && (valor.isNumber() || valor.isTextual())) {
                try {
                    return Integer.valueOf(valor.asText());
                } catch (NumberFormatException ignorado) {
                    // Se prueba el siguiente alias del campo.
                }
            }
        }
        return null;
    }
}
