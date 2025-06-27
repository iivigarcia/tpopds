package com.uade.tpo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

import com.uade.tpo.model.emparejamientoStrategy.EmparejamientoStrategy;
import com.uade.tpo.model.emparejamientoStrategy.EmparejamientoStrategyConverter;
import com.uade.tpo.model.emparejamientoStrategy.EmparejarPorNivel;
import com.uade.tpo.model.state.EstadoPartido;
import com.uade.tpo.model.state.EstadoPartidoConverter;
import com.uade.tpo.model.state.NecesitamosJugadores;
import com.uade.tpo.model.notification.NotificationManager;

@Data
@Entity
@Table(name = "partidos")
public class Partido {

    {
        this.estado = new NecesitamosJugadores();
        this.estrategiaEmparejamiento = new EmparejarPorNivel();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "deporte_id", nullable = false)
    private Deporte deporte;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "hora", nullable = false)
    private String hora;

    @Column(name = "duracion_minutos", nullable = false)
    private int duracionMinutos;

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

    // Transient field for notification manager (not persisted)
    @Transient
    private NotificationManager notificationManager;

    public void setNotificationManager(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    public void setEstado(EstadoPartido nuevoEstado) {
        EstadoPartido estadoAnterior = this.estado;
        this.estado = nuevoEstado;
        System.out.println("El estado del partido ha cambiado a: " + this.estado.getClass().getSimpleName());
        
        // Notify observers about state change
        if (notificationManager != null) {
            notifyStateChange(estadoAnterior, nuevoEstado);
        }
    }

    private void notifyStateChange(EstadoPartido estadoAnterior, EstadoPartido nuevoEstado) {
        String eventType = getEventTypeForStateChange(nuevoEstado);
        if (eventType != null) {
            notificationManager.notifyObservers(this, eventType);
        }
    }

    private String getEventTypeForStateChange(EstadoPartido nuevoEstado) {
        if (nuevoEstado instanceof com.uade.tpo.model.state.PartidoArmado) {
            return "PARTIDO_ARMADO";
        } else if (nuevoEstado instanceof com.uade.tpo.model.state.Confirmado) {
            return "PARTIDO_CONFIRMADO";
        } else if (nuevoEstado instanceof com.uade.tpo.model.state.EnJuego) {
            return "PARTIDO_EN_JUEGO";
        } else if (nuevoEstado instanceof com.uade.tpo.model.state.Finalizado) {
            return "PARTIDO_FINALIZADO";
        } else if (nuevoEstado instanceof com.uade.tpo.model.state.Cancelado) {
            return "PARTIDO_CANCELADO";
        }
        return null;
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
