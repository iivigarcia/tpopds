package com.uade.tpo.dto;

import com.uade.tpo.model.Deporte;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class PartidoDTO {
  private Long id;
  private Deporte deporte;
  private List<EquipoDTO> equipos;
  private Date fecha;
  private String hora;
  private UsuarioDTO organizador;
  private Integer ubicacion;
  private String estadoPartido;
  private String estrategiaEmparejamiento;
  private List<ComentarioDTO> comentarios;
  private List<EstadisticaDTO> estadisticas;
}