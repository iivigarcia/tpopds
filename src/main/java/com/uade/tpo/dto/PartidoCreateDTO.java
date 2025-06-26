package com.uade.tpo.dto;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class PartidoCreateDTO {
  private List<Long> equipoIds;
  private Date fecha;
}