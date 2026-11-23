package com.gio.cs2api.service;

import java.util.Map;

/**
 * Cliente de una de las dos APIs ajenas. A2 y A3 se inyectan con este mismo
 * contrato; ninguna regla del compañero se copia al código de A1.
 */
public interface ValidadorCompanero {

    String rol();

    String regla();

    ResultadoValidacion validar(Map<String, Object> datos);

    record ResultadoValidacion(
            boolean aceptada,
            String mensaje,
            Integer danoEfectivo,
            Integer municionTransferida,
            Integer codigoHttp) {
    }
}
