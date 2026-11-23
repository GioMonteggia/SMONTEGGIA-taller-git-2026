package com.gio.cs2api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
    info = @Info(
        title = "API REST de armas de Counter-Strike 2",
        version = "1.0.0",
        description = "API REST para gestionar armas y jugadores, consultar acciones de combate y ejecutar simulaciones con persistencia H2."
    )
)
@SpringBootApplication
public class Cs2ApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(Cs2ApiApplication.class, args);
    }

}