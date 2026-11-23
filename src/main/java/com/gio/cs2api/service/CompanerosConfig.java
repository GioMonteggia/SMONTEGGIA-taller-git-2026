package com.gio.cs2api.service;

import tools.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Crea un cliente distinto para A2 y para A3 a partir de las propiedades. */
@Configuration
@EnableConfigurationProperties(CompanerosProperties.class)
public class CompanerosConfig {

    @Bean
    @Qualifier("A2")
    public ValidadorCompanero validadorA2(
            CompanerosProperties propiedades, ObjectMapper objectMapper) {
        return crear(propiedades.a2(), objectMapper);
    }

    @Bean
    @Qualifier("A3")
    public ValidadorCompanero validadorA3(
            CompanerosProperties propiedades, ObjectMapper objectMapper) {
        return crear(propiedades.a3(), objectMapper);
    }

    private ValidadorCompanero crear(
            CompanerosProperties.Companero propiedad,
            ObjectMapper objectMapper) {

        int timeout = propiedad.timeoutMs() > 0
                ? propiedad.timeoutMs()
                : 3000;

        return new CompaneroRestClient(
                propiedad.rol(),
                propiedad.regla(),
                propiedad.baseUrl(),
                propiedad.validarPath(),
                timeout,
                objectMapper);
    }
}
