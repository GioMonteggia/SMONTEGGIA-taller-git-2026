package com.gio.cs2api.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Jugada del escenario de POO-09 aceptada por los dos compañeros. */
@Entity
@Table(name = "jugadas_cs2")
public class Jugada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String descripcion;

    @Column(nullable = false)
    private Long rifleId;

    @Column(nullable = false)
    private Long jugadorId;

    @Column(nullable = false)
    private int distancia;

    @Column(nullable = false)
    private int danoEfectivo;

    @Column(nullable = false)
    private int municionTransferida;

    @Column(nullable = false)
    private boolean aceptadaPorA2;

    @Column(nullable = false)
    private boolean aceptadaPorA3;

    @Column(nullable = false)
    private String mensajeA2;

    @Column(nullable = false)
    private String mensajeA3;

    @Column(nullable = false)
    private LocalDateTime registradaEn;

    protected Jugada() {
        // Constructor requerido por JPA.
    }

    public Jugada(
            String descripcion,
            Long rifleId,
            Long jugadorId,
            int distancia,
            int danoEfectivo,
            int municionTransferida,
            boolean aceptadaPorA2,
            boolean aceptadaPorA3,
            String mensajeA2,
            String mensajeA3,
            LocalDateTime registradaEn) {

        this.descripcion = descripcion;
        this.rifleId = rifleId;
        this.jugadorId = jugadorId;
        this.distancia = distancia;
        this.danoEfectivo = danoEfectivo;
        this.municionTransferida = municionTransferida;
        this.aceptadaPorA2 = aceptadaPorA2;
        this.aceptadaPorA3 = aceptadaPorA3;
        this.mensajeA2 = mensajeA2;
        this.mensajeA3 = mensajeA3;
        this.registradaEn = registradaEn;
    }

    public Long getId() {
        return id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Long getRifleId() {
        return rifleId;
    }

    public Long getJugadorId() {
        return jugadorId;
    }

    public int getDistancia() {
        return distancia;
    }

    public int getDanoEfectivo() {
        return danoEfectivo;
    }

    public int getMunicionTransferida() {
        return municionTransferida;
    }

    public boolean isAceptadaPorA2() {
        return aceptadaPorA2;
    }

    public boolean isAceptadaPorA3() {
        return aceptadaPorA3;
    }

    public String getMensajeA2() {
        return mensajeA2;
    }

    public String getMensajeA3() {
        return mensajeA3;
    }

    public LocalDateTime getRegistradaEn() {
        return registradaEn;
    }
}
