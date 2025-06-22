package com.uade.tpo.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "estadisticas")
public class Estadistica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "partido_id", nullable = false)
    private Partido partido;

    @ManyToOne
    @JoinColumn(name = "jugador_id", nullable = false)
    private Usuario jugador;

    private int anotaciones;

    private int asistencias;

    private int amonestaciones;

    @Column(name = "mejor_jugador")
    private boolean mejorJugador;
}
