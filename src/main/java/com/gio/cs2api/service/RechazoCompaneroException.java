package com.gio.cs2api.service;

import com.gio.cs2api.service.ValidadorCompanero.ResultadoValidacion;

/** Una de las APIs ajenas rechazó el escenario. Identifica al autor del rechazo. */
public class RechazoCompaneroException extends RuntimeException {

    private final String rol;
    private final String regla;
    private final ResultadoValidacion resultado;

    public RechazoCompaneroException(
            String rol, String regla, ResultadoValidacion resultado) {
        super(rol + " rechazó el escenario: " + resultado.mensaje());
        this.rol = rol;
        this.regla = regla;
        this.resultado = resultado;
    }

    public String getRol() {
        return rol;
    }

    public String getRegla() {
        return regla;
    }

    public ResultadoValidacion getResultado() {
        return resultado;
    }
}
