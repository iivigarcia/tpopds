package com.uade.tpo.dto;

import lombok.Data;

@Data
public class EstadisticaDTO {
  private Long id;
  private Long partidoId;
  private UsuarioDTO jugador;
  private int anotaciones;
  private int asistencias;
  private int amonestaciones;
  private boolean mejorJugador;
}