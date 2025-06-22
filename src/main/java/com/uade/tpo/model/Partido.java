package com.uade.tpo.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;
import com.uade.tpo.model.state.EstadoPartido;
import com.uade.tpo.model.state.EstadoPartidoConverter;
import com.uade.tpo.model.state.NecesitamosJugadores;

@Data
@Entity
@Table(name = "partidos")
public class Partido {

    {
        // Bloque de inicialización de instancia para establecer el estado inicial
        this.estado = new NecesitamosJugadores();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "deporte_id", nullable = false)
    private Deporte deporte;

    @Column(name = "fecha", nullable = false)
    private Date fecha;

    @Column(name = "hora", nullable = false)
    private String hora;

    @Column(name = "ubicacion", nullable = false)
    private String ubicacion;

    @Column(name = "cantidad_jugadores")
    private int cantidadJugadores;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "partidos_jugadores", joinColumns = @JoinColumn(name = "partido_id"), inverseJoinColumns = @JoinColumn(name = "usuario_id"))
    private List<Usuario> jugadores;

    @Convert(converter = EstadoPartidoConverter.class)
    @Column(name = "estado")
    private EstadoPartido estado;

    @ManyToOne
    @JoinColumn(name = "equipo_local_id")
    private Equipo equipoLocal;

    @ManyToOne
    @JoinColumn(name = "equipo_visitante_id")
    private Equipo equipoVisitante;

    @OneToMany(mappedBy = "partido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comentario> comentarios;

    @OneToMany(mappedBy = "partido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Estadistica> estadisticas;

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
}
