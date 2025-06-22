package com.uade.tpo.dto;

import lombok.Data;
import java.util.Date;

@Data
public class PartidoCreateDTO {
  private Long equipoLocalId;
  private Long equipoVisitanteId;
  private Date fecha;
}