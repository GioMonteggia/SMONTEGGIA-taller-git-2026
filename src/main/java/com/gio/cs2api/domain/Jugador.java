package com.gio.cs2api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.util.List;

@Entity
@Table(name = "jugadores_cs2")
public class Jugador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private int vida;

    @Column(name = "municion_reserva", nullable = false)
    private int municionReserva;

    @Transient
    private Inventario inventario = new Inventario();

    protected Jugador() {
        this.inventario = new Inventario();
    }

    public Jugador(String nombre) {
        this(nombre, 100, 90);
    }

    public Jugador(String nombre, int vida, int municionReserva) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre del jugador no puede estar vacío"
            );
        }
        if (vida <= 0) {
            throw new IllegalArgumentException(
                    "La vida inicial debe ser mayor que 0"
            );
        }
        if (municionReserva < 0) {
            throw new IllegalArgumentException(
                    "La reserva de munición no puede ser negativa"
            );
        }

        this.nombre = nombre;
        this.vida = vida;
        this.municionReserva = municionReserva;
        this.inventario = new Inventario();
    }

    public void recibirDano(int dano) {
        if (dano <= 0) {
            throw new IllegalArgumentException(
                    "El daño recibido debe ser mayor que 0"
            );
        }
        vida = Math.max(0, vida - dano);
    }

    public void consumirMunicionReserva(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad de munición debe ser mayor que 0"
            );
        }
        if (cantidad > municionReserva) {
            throw new MunicionAgotadaException(
                    "El jugador no tiene suficiente munición de reserva"
            );
        }

        municionReserva -= cantidad;
    }

    public void agregarArma(Arma arma) {
        inventario.agregarArma(arma);
    }

    public String dispararArma(int posicion) {
        return inventario.dispararArma(posicion);
    }

    public String recargarArma(int posicion) {
        return inventario.recargarArma(posicion);
    }

    public List<String> dispararTodas() {
        return inventario.dispararTodas();
    }

    public List<String> recargarTodas() {
        return inventario.recargarTodas();
    }

    public List<String> mostrarTienda() {
        return inventario.mostrarTienda();
    }

    public int cantidadArmas() {
        return inventario.cantidadArmas();
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public int getVida() {
        return vida;
    }

    public int getMunicionReserva() {
        return municionReserva;
    }

    public Inventario getInventario() {
        return inventario;
    }
}