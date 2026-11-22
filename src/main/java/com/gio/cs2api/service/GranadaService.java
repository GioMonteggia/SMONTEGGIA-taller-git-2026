package com.gio.cs2api.service;

import com.gio.cs2api.domain.Granada;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import org.springframework.stereotype.Service;

@Service
public class GranadaService {

    private static final long COOLDOWN_MILISEGUNDOS = 1000L;

    private final Map<Granada, Long> ultimoLanzamiento =
            Collections.synchronizedMap(new WeakHashMap<>());

    public String lanzar(Granada granada) {
        if (granada == null) {
            throw new IllegalArgumentException(
                    "La granada es obligatoria");
        }

        synchronized (granada) {
            if (granada.getMunicionActual() <= 0) {
                return granada.obtenerNombre()
                        + ": no quedan granadas.";
            }

            long ahora = System.currentTimeMillis();
            Long ultimo = ultimoLanzamiento.get(granada);

            if (ultimo != null) {
                long transcurrido = ahora - ultimo;

                if (transcurrido < COOLDOWN_MILISEGUNDOS) {
                    long restante =
                            COOLDOWN_MILISEGUNDOS - transcurrido;

                    return granada.obtenerNombre()
                            + ": cooldown activo. Espere "
                            + restante
                            + " ms.";
                }
            }

            String resultado = granada.disparar();
            ultimoLanzamiento.put(granada, ahora);

            return resultado;
        }
    }

    public long getCooldownRestante(Granada granada) {
        if (granada == null) {
            throw new IllegalArgumentException(
                    "La granada es obligatoria");
        }

        synchronized (granada) {
            Long ultimo = ultimoLanzamiento.get(granada);

            if (ultimo == null) {
                return 0;
            }

            long transcurrido =
                    System.currentTimeMillis() - ultimo;

            if (transcurrido >= COOLDOWN_MILISEGUNDOS) {
                return 0;
            }

            return COOLDOWN_MILISEGUNDOS - transcurrido;
        }
    }

    public long getCooldownMilisegundos() {
        return COOLDOWN_MILISEGUNDOS;
    }
}