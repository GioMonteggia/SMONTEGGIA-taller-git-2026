package com.gio.cs2api.service;

import com.gio.cs2api.domain.Arma;
import com.gio.cs2api.domain.Granada;
import com.gio.cs2api.domain.Inventario;
import com.gio.cs2api.domain.MunicionAgotadaException;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class InventarioService {

    private final GranadaService granadaService;

    public InventarioService(GranadaService granadaService) {
        this.granadaService = granadaService;
    }

    public void agregarArma(Inventario inventario, Arma arma) {
        validarInventario(inventario);

        if (arma == null) {
            throw new IllegalArgumentException(
                    "No se puede agregar un arma nula");
        }

        inventario.agregarArma(arma);
    }

    public Arma obtenerArma(Inventario inventario, int posicion) {
        validarInventario(inventario);

        List<Arma> armas = inventario.getArmas();

        if (posicion < 0 || posicion >= armas.size()) {
            throw new IllegalArgumentException(
                    "La posición del arma no existe");
        }

        Arma arma = armas.get(posicion);

        if (arma == null) {
            throw new IllegalArgumentException(
                    "No existe un arma en esa posición");
        }

        return arma;
    }

    public String dispararArma(Inventario inventario, int posicion) {
        Arma arma = obtenerArma(inventario, posicion);

        if (arma.getMunicionActual() <= 0) {
            throw new MunicionAgotadaException(
                    "No queda munición en " + arma.getNombre());
        }

        if (arma instanceof Granada granada) {
            return granadaService.lanzar(granada);
        }

        return arma.disparar();
    }

    public String recargarArma(Inventario inventario, int posicion) {
        Arma arma = obtenerArma(inventario, posicion);
        return arma.recargar();
    }

    public List<String> dispararTodas(Inventario inventario) {
        validarInventario(inventario);

        List<String> resultados = new ArrayList<>();
        List<Arma> armas = inventario.getArmas();

        for (int i = 0; i < armas.size(); i++) {
            resultados.add(dispararArma(inventario, i));
        }

        return resultados;
    }

    public List<String> recargarTodas(Inventario inventario) {
        validarInventario(inventario);

        List<String> resultados = new ArrayList<>();
        List<Arma> armas = inventario.getArmas();

        for (int i = 0; i < armas.size(); i++) {
            resultados.add(recargarArma(inventario, i));
        }

        return resultados;
    }

    public List<String> mostrarTienda(Inventario inventario) {
        validarInventario(inventario);

        List<String> productos = new ArrayList<>();

        for (Arma arma : inventario.getArmas()) {
            productos.add(
                    arma.obtenerNombre()
                            + " - "
                            + arma.obtenerPrecio());
        }

        return productos;
    }

    public int cantidadArmas(Inventario inventario) {
        validarInventario(inventario);
        return inventario.getArmas().size();
    }

    private void validarInventario(Inventario inventario) {
        if (inventario == null) {
            throw new IllegalArgumentException(
                    "El inventario es obligatorio");
        }
    }
}