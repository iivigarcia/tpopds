package com.uade.tpo.dto;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class PartidoDTO {
  private Long id;
  private List<EquipoDTO> equipos;
  private Date fecha;
  private List<ComentarioDTO> comentarios;
  private List<EstadisticaDTO> estadisticas;
}