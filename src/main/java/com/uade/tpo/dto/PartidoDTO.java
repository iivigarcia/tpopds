package com.uade.tpo.dto;

import com.uade.tpo.model.Deporte;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class PartidoDTO {
  private Long id;
  private Deporte deporte;
  private List<EquipoDTO> equipos;
  private List<EquipoJugadorDTO> participaciones;
  private LocalDate fecha;
  private String hora;
  private Integer duracionMinutos;
  private UsuarioDTO organizador;
  private Integer ubicacion;
  private String estadoPartido;
  private String estrategiaEmparejamiento;
  private List<ComentarioDTO> comentarios;
  private List<EstadisticaDTO> estadisticas;
  private String equipoGanador;
}