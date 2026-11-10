package com.gio.cs2api.domain;

public class ArmaNoEncontradaException extends ExcepcionDominioCS2 {

    private static final long serialVersionUID = 1L;

    public ArmaNoEncontradaException(String mensaje) {
        super(mensaje);
    }
}