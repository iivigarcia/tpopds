package com.uade.tpo.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "equipo_jugadores")
public class EquipoJugador {

  @EmbeddedId
  private EquipoJugadorId id;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("equipoId")
  @JoinColumn(name = "equipo_id")
  private Equipo equipo;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("usuarioId")
  @JoinColumn(name = "usuario_id")
  private Usuario usuario;

  @Column(nullable = false)
  private boolean inscrito = true;

  @Column(nullable = false)
  private boolean confirmado = false;
}