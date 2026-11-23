package com.gio.cs2api.service;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * URLs y reglas de los dos servicios ajenos que consulta el escenario.
 * Se enlazan desde las propiedades {@code companeros.a2.*} y
 * {@code companeros.a3.*}, que pueden sobreescribirse con variables de entorno.
 */
@ConfigurationProperties(prefix = "companeros")
public record CompanerosProperties(Companero a2, Companero a3) {

    public record Companero(
            String nombre,
            String rol,
            String regla,
            String baseUrl,
            String openapiUrl,
            String validarPath,
            int timeoutMs) {
    }
}
