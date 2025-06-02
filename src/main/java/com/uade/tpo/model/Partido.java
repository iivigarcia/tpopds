package com.uade.tpo.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "partidos")
public class Partido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "equipo_local_id")
    private Equipo equipoLocal;

    @ManyToOne
    @JoinColumn(name = "equipo_visitante_id")
    private Equipo equipoVisitante;

    private Date fecha;

    @OneToMany(mappedBy = "partido")
    private List<Comentario> comentarios;

    @OneToMany(mappedBy = "partido")
    private List<Estadistica> estadisticas;
}
