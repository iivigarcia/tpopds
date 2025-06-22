package com.uade.tpo.dto;

import lombok.Data;

@Data
public class EstadisticaCreateDTO {
  private Long partidoId;
  private Long jugadorId;
  private int anotaciones;
  private int asistencias;
  private int amonestaciones;
  private boolean mejorJugador;
}