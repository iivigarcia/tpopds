package com.uade.tpo.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PartidoCreateDTO {
  private Long deporteId;
  private LocalDate fecha;
  private String hora;
  private Integer duracionMinutos;
  private Integer ubicacionId;
  private Long organizadorId;
  private Integer cantidadJugadores;
  private String nivelMinimo;
  private String nivelMaximo;
}