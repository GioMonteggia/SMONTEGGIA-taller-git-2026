package com.gio.cs2api.domain;

public class ExcepcionDominioCS2 extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ExcepcionDominioCS2(String mensaje) {
        super(mensaje);
    }
}