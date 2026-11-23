package com.gio.cs2api.service;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.stream.Collectors;

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
 * OpenAPI ajeno. Interpreta el resultado por los nombres de campo del documento
 * y, cuando el compañero no publica un campo booleano, infiere la aceptación
 * del código HTTP (2xx = aceptado). No importa clases ni reglas del proyecto
 * del compañero.
 */
public class CompaneroRestClient implements ValidadorCompanero {

    private final String rol;
    private final String regla;
    private final String urlValidacion;
    private final boolean usarQuery;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public CompaneroRestClient(
            String rol,
            String regla,
            String baseUrl,
            String validarPath,
            int timeoutMs,
            ObjectMapper objectMapper) {

        this(rol, regla, baseUrl, validarPath, timeoutMs, objectMapper, false);
    }

    public CompaneroRestClient(
            String rol,
            String regla,
            String baseUrl,
            String validarPath,
            int timeoutMs,
            ObjectMapper objectMapper,
            boolean usarQuery) {

        this(rol, regla, baseUrl, validarPath, objectMapper,
                constructorConTimeout(timeoutMs), usarQuery);
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

        this(rol, regla, baseUrl, validarPath, objectMapper, constructor, false);
    }

    CompaneroRestClient(
            String rol,
            String regla,
            String baseUrl,
            String validarPath,
            ObjectMapper objectMapper,
            RestClient.Builder constructor,
            boolean usarQuery) {

        this.rol = rol;
        this.regla = regla;
        this.urlValidacion = unirUrls(baseUrl, validarPath);
        this.usarQuery = usarQuery;

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
            ResponseEntity<String> respuesta = enviar(datos);

            String cuerpo = respuesta.getBody();
            JsonNode nodo = cuerpo == null || cuerpo.isBlank()
                    ? objectMapper.createObjectNode()
                    : objectMapper.readTree(cuerpo);

            Boolean explicita = leerBooleanoOpcional(nodo,
                    "aceptada", "aceptado", "aprobada",
                    "valida", "valido", "ok");

            boolean aceptada = respuesta.getStatusCode().is2xxSuccessful()
                    && (explicita == null || explicita);

            String mensaje = leerTexto(nodo,
                    "mensaje", "message", "detalle", "reason", "error",
                    "nombre");
            if (mensaje == null || mensaje.isBlank()) {
                mensaje = cuerpo == null ? "" : cuerpo;
            }
            // La columna mensaje_compañero de H2 admite 255 caracteres. Algunos
            // compañeros no devuelven un campo "mensaje" y el cuerpo completo
            // puede ser más largo; se recorta para que la jugada se persista.
            if (mensaje.length() > 250) {
                mensaje = mensaje.substring(0, 250);
            }

            return new ResultadoValidacion(
                    aceptada,
                    mensaje,
                    leerEntero(nodo,
                            "danoEfectivo", "dañoEfectivo", "danioEfectivo",
                            "danioInfligido", "danoRecibido"),
                    leerEntero(nodo,
                            "municionTransferida", "municionATransferir",
                            "balasTransferidas", "balasRestantes",
                            "municionRestante"),
                    respuesta.getStatusCode().value());

        } catch (RestClientException exception) {
            throw new CompaneroNoDisponibleException(
                    rol, exception.getMessage());
        } catch (JacksonException exception) {
            throw new CompaneroNoDisponibleException(
                    rol, "respuesta JSON inválida: " + exception.getMessage());
        }
    }

    private ResponseEntity<String> enviar(Map<String, Object> datos) {
        if (usarQuery) {
            return restClient.post()
                    .uri(URI.create(urlValidacion + construirQuery(datos)))
                    .retrieve()
                    .onStatus(status -> true, (peticion, respuesta) -> { })
                    .toEntity(String.class);
        }

        return restClient.post()
                .uri(URI.create(urlValidacion))
                .contentType(MediaType.APPLICATION_JSON)
                .body(datos)
                .retrieve()
                .onStatus(status -> true, (peticion, respuesta) -> { })
                .toEntity(String.class);
    }

    private static String construirQuery(Map<String, Object> datos) {
        if (datos == null || datos.isEmpty()) {
            return "";
        }

        return "?" + datos.entrySet().stream()
                .filter(entrada -> entrada.getValue() != null)
                .map(entrada -> codificar(entrada.getKey())
                        + "=" + codificar(String.valueOf(entrada.getValue())))
                .collect(Collectors.joining("&"));
    }

    private static String codificar(String valor) {
        return URLEncoder.encode(valor, StandardCharsets.UTF_8);
    }

    private static String unirUrls(String baseUrl, String validarPath) {
        String base = baseUrl == null ? "" : baseUrl.trim().replaceAll("/+$", "");
        String ruta = validarPath == null ? "" : validarPath.trim();
        if (!ruta.startsWith("/")) {
            ruta = "/" + ruta;
        }
        return base + ruta;
    }

    private static Boolean leerBooleanoOpcional(JsonNode nodo, String... nombres) {
        for (String nombre : nombres) {
            JsonNode valor = nodo.get(nombre);
            if (valor != null && valor.isBoolean()) {
                return valor.booleanValue();
            }
            if (valor != null && valor.isTextual()) {
                return Boolean.parseBoolean(valor.asText().trim());
            }
        }
        return null;
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
