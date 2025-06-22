package com.uade.tpo.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "usuario_deporte")
@IdClass(UsuarioDeporteId.class)
public class UsuarioDeporte {

  @Id
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "usuario_id")
  private Usuario usuario;

  @Id
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "deporte_id")
  private Deporte deporte;

  @Enumerated(EnumType.STRING)
  @Column(name = "nivel_juego", nullable = false)
  private NivelJuego nivelDeJuego;

  @Column(name = "deporte_favorito", nullable = false)
  private boolean deporteFavorito;

}