package com.uade.tpo.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "comentarios")
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String mensaje;

    @Column(nullable = false)
    private Date fecha;

    @ManyToOne
    @JoinColumn(name = "jugador_id", nullable = false)
    private Usuario jugador;

    @ManyToOne
    @JoinColumn(name = "partido_id", nullable = false)
    private Partido partido;
}
