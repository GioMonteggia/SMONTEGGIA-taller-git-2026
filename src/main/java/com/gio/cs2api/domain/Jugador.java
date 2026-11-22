
package com.gio.cs2api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

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
        // Constructor requerido por JPA.
        this.inventario = new Inventario();
    }

    public Jugador(String nombre) {
        this(nombre, 100, 90);
    }

    public Jugador(String nombre, int vida, int municionReserva) {
        this.nombre = nombre;
        this.vida = vida;
        this.municionReserva = municionReserva;
        this.inventario = new Inventario();
    }

    public void actualizarEstadoCombate(
            int vidaNueva,
            int municionReservaNueva) {

        this.vida = vidaNueva;
        this.municionReserva = municionReservaNueva;
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
