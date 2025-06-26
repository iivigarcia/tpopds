package com.uade.tpo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

import com.uade.tpo.model.emparejamientoStrategy.EmparejamientoStrategy;
import com.uade.tpo.model.emparejamientoStrategy.EmparejamientoStrategyConverter;
import com.uade.tpo.model.state.EstadoPartido;
import com.uade.tpo.model.state.EstadoPartidoConverter;
import com.uade.tpo.model.state.NecesitamosJugadores;

@Data
@Entity
@Table(name = "partidos")
public class Partido {

    {
        this.estado = new NecesitamosJugadores();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "deporte_id", nullable = false)
    private Deporte deporte;

    @Column(name = "fecha", nullable = false)
    private Date fecha;

    @Column(name = "hora", nullable = false)
    private String hora;

    @Column(name = "geolocalization_id", nullable = false)
    private Integer geolocalizationId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "organizador_id", nullable = false)
    private Usuario organizador;

    @Column(name = "cantidad_jugadores")
    private int cantidadJugadores;

    @Column(name = "nivel_minimo")
    private NivelJuego nivelMinimo;

    @Column(name = "nivel_maximo")
    private NivelJuego nivelMaximo;

    @Convert(converter = EstadoPartidoConverter.class)
    @Column(name = "estado")
    private EstadoPartido estado;

    @Convert(converter = EmparejamientoStrategyConverter.class)
    @Column(name = "estrategia_emparejamiento")
    private EmparejamientoStrategy estrategiaEmparejamiento;

    @OneToMany(mappedBy = "partido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comentario> comentarios;

    @OneToMany(mappedBy = "partido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Estadistica> estadisticas;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "partido_equipos", joinColumns = @JoinColumn(name = "partido_id"), inverseJoinColumns = @JoinColumn(name = "equipo_id"))
    private List<Equipo> equipos;

    @ManyToOne
    @JoinColumn(name = "equipo_ganador_id")
    @JsonIgnore
    private Equipo ganador;

    public void setEstado(EstadoPartido nuevoEstado) {
        this.estado = nuevoEstado;
        System.out.println("El estado del partido ha cambiado a: " + this.estado.getClass().getSimpleName());
    }

    public void crear() {
        this.estado.crear(this);
    }

    public void armar() {
        this.estado.armar(this);
    }

    public void confirmar() {
        this.estado.confirmar(this);
    }

    public void comenzar() {
        this.estado.comenzar(this);
    }

    public void finalizar() {
        this.estado.finalizar(this);
    }

    public void cancelar() {
        this.estado.cancelar(this);
    }

    public void setEstrategiaEmparejamiento(EmparejamientoStrategy estrategia) {
        this.estrategiaEmparejamiento = estrategia;
    }

    public void emparejar() {
        this.estrategiaEmparejamiento.emparejar(this);
    }
}
