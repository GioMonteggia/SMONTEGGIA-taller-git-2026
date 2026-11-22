
package com.gio.cs2api.service;

import com.gio.cs2api.domain.Arma;
import com.gio.cs2api.domain.Escopeta;
import com.gio.cs2api.domain.Francotirador;
import com.gio.cs2api.domain.GranadaFlash;
import com.gio.cs2api.domain.Inventario;
import com.gio.cs2api.domain.Jugador;
import com.gio.cs2api.domain.Pistola;
import com.gio.cs2api.domain.Rifle;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class JugadorDemostracionService {

    private final InventarioService inventarioService;

    public JugadorDemostracionService(
            InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    public Map<String, Object> mostrarJugador() {
        Jugador jugador = crearJugadorDemo();
        Inventario inventario = jugador.getInventario();

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("jugador", jugador.getNombre());
        respuesta.put(
                "cantidadArmas",
                inventarioService.cantidadArmas(inventario));
        respuesta.put("armas", obtenerInformacionArmas(jugador));

        return respuesta;
    }

    public Map<String, Object> dispararTodas() {
        Jugador jugador = crearJugadorDemo();

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("jugador", jugador.getNombre());
        respuesta.put(
                "resultado",
                inventarioService.dispararTodas(
                        jugador.getInventario()));

        return respuesta;
    }

    public Map<String, Object> recargarTodas() {
        Jugador jugador = crearJugadorDemo();

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("jugador", jugador.getNombre());
        respuesta.put(
                "resultado",
                inventarioService.recargarTodas(
                        jugador.getInventario()));

        return respuesta;
    }

    public Map<String, Object> mostrarTienda() {
        Jugador jugador = crearJugadorDemo();

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("jugador", jugador.getNombre());
        respuesta.put(
                "productos",
                inventarioService.mostrarTienda(
                        jugador.getInventario()));

        return respuesta;
    }

    public Map<String, Object> dispararArma(int posicion) {
        Jugador jugador = crearJugadorDemo();

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("jugador", jugador.getNombre());
        respuesta.put("posicion", posicion);
        respuesta.put(
                "resultado",
                inventarioService.dispararArma(
                        jugador.getInventario(), posicion));

        return respuesta;
    }

    private Jugador crearJugadorDemo() {
        Jugador jugador = new Jugador("Jugador Demo");
        Inventario inventario = jugador.getInventario();

        Arma rifle = new Rifle("AK-47", 2700f);
        Arma pistola = new Pistola("Glock", 700f);
        Arma escopeta = new Escopeta("Nova", 1050f);
        Arma francotirador = new Francotirador("AWP", 4750f);
        Arma granada = new GranadaFlash("Flash", 200f);

        inventarioService.agregarArma(inventario, rifle);
        inventarioService.agregarArma(inventario, pistola);
        inventarioService.agregarArma(inventario, escopeta);
        inventarioService.agregarArma(inventario, francotirador);
        inventarioService.agregarArma(inventario, granada);

        return jugador;
    }

    private List<Map<String, Object>> obtenerInformacionArmas(
            Jugador jugador) {

        return jugador.getInventario()
                .getArmas()
                .stream()
                .map(arma -> {
                    Map<String, Object> informacion =
                            new LinkedHashMap<>();

                    informacion.put(
                            "tipo",
                            arma.getClass().getSimpleName());
                    informacion.put("nombre", arma.getNombre());
                    informacion.put("precio", arma.getPrecio());
                    informacion.put("municion", arma.getMunicionActual());

                    return informacion;
                })
                .toList();
    }
}
