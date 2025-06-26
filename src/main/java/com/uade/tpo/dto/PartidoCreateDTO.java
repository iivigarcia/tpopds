package com.uade.tpo.dto;

import lombok.Data;
import java.util.Date;

@Data
public class PartidoCreateDTO {
  private Long deporteId;
  private Date fecha;
  private String hora;
  private Integer duracionHoras;
  private Integer ubicacionId;
  private Long organizadorId;
  private Integer cantidadJugadores;
  private String nivelMinimo;
  private String nivelMaximo;
}