package com.uade.tpo.model;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.uade.tpo.model.emparejamientoStrategy.EmparejamientoStrategy;
import com.uade.tpo.model.emparejamientoStrategy.EmparejamientoStrategyConverter;
import com.uade.tpo.model.emparejamientoStrategy.EmparejarPorNivel;
import com.uade.tpo.model.notification.NotificationManager;
import com.uade.tpo.model.notification.Observado;
import com.uade.tpo.model.state.EstadoPartido;
import com.uade.tpo.model.state.EstadoPartidoConverter;
import com.uade.tpo.model.state.NecesitamosJugadores;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

@Data
@Entity
@Table(name = "partidos")
public class Partido extends Observado {

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

    public Partido() {

    }

    public void initializeDefaults() {
        if (this.estado == null) {
            this.estado = new NecesitamosJugadores();
        }
        if (this.estrategiaEmparejamiento == null) {
            this.estrategiaEmparejamiento = new EmparejarPorNivel();
        }
        if (this.notificationManager == null) {
            this.notificationManager = new NotificationManager();
            this.add(this.notificationManager);
        }
    }

    public void setNotificationManager(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    public void setEstado(EstadoPartido nuevoEstado) {
        this.estado = nuevoEstado;
        System.out.println("El estado del partido ha cambiado a: " + this.estado.getClass().getSimpleName());
        System.out.println("Notificando observadores...");
        notifyObservers();
        System.out.println("Observadores notificados.");
    }

    public void crear() {
        if (this.estado == null) {
            initializeDefaults();
        }
        this.estado.crear(this);
    }

    public void armar() {
        if (this.estado == null) {
            initializeDefaults();
        }
        this.estado.armar(this);
    }

    public void confirmar() {
        if (this.estado == null) {
            initializeDefaults();
        }
        this.estado.confirmar(this);
    }

    public void comenzar() {
        if (this.estado == null) {
            initializeDefaults();
        }
        this.estado.comenzar(this);
    }

    public void finalizar() {
        if (this.estado == null) {
            initializeDefaults();
        }
        this.estado.finalizar(this);
    }

    public void cancelar() {
        if (this.estado == null) {
            initializeDefaults();
        }
        this.estado.cancelar(this);
    }

    public void setEstrategiaEmparejamiento(EmparejamientoStrategy estrategia) {
        this.estrategiaEmparejamiento = estrategia;
    }

    public void emparejar() {
        if (this.estrategiaEmparejamiento == null) {
            initializeDefaults();
        }
        this.estrategiaEmparejamiento.emparejar(this);
    }
}
