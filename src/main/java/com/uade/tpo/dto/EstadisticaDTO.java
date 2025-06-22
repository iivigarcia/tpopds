package com.uade.tpo.dto;

import lombok.Data;

@Data
public class EstadisticaDTO {
  private Long id;
  private String descripcion;
  private Long partidoId; // Solo el ID del partido para evitar referencias circulares
}