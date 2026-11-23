package com.gio.cs2api.service;

/** La API de un compañero no respondió o devolvió un error de servidor. */
public class CompaneroNoDisponibleException extends RuntimeException {

    private final String rol;

    public CompaneroNoDisponibleException(String rol, String detalle) {
        super("La API de " + rol + " no respondió: " + detalle);
        this.rol = rol;
    }

    public String getRol() {
        return rol;
    }
}
