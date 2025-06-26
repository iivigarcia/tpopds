package com.uade.tpo.service;

import com.uade.tpo.model.Partido;
import com.uade.tpo.repository.PartidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PartidoCronService {

  @Autowired
  private PartidoRepository partidoRepository;

  @Autowired
  private PartidoService partidoService;

  @Scheduled(fixedRate = 300000)
  public void procesarEstadosPartidos() {
    System.out.println("=== Ejecutando cron job de partidos: " + LocalDateTime.now() + " ===");

    List<Partido> todosLosPartidos = partidoRepository.findAll();
    List<Partido> partidos = todosLosPartidos.stream()
        .filter(p -> {
          String estado = p.getEstado() != null ? p.getEstado().getClass().getSimpleName() : null;
          return "Confirmado".equals(estado) || "EnJuego".equals(estado);
        })
        .collect(Collectors.toList());

    LocalDateTime fechaActual = LocalDateTime.now();
    int partidosProcesados = 0;
    int partidosConfirmados = 0;
    int partidosEnJuego = 0;

    for (Partido partido : partidos) {
      try {
        String estadoActual = partido.getEstado() != null ? partido.getEstado().getClass().getSimpleName()
            : "Sin estado";

        procesarPartido(partido, fechaActual);
        partidosProcesados++;

        if ("Confirmado".equals(estadoActual)) {
          partidosConfirmados++;
        } else if ("EnJuego".equals(estadoActual)) {
          partidosEnJuego++;
        }
      } catch (Exception e) {
        System.err.println("Error procesando partido ID " + partido.getId() + ": " + e.getMessage());
      }
    }

    System.out.println("=== Cron job completado ===");
    System.out.println("Total partidos procesados: " + partidosProcesados);
    System.out.println("Partidos confirmados evaluados: " + partidosConfirmados);
    System.out.println("Partidos en juego evaluados: " + partidosEnJuego);
    System.out.println("=====================================");
  }

  private void procesarPartido(Partido partido, LocalDateTime fechaActual) {
    String estadoActual = partido.getEstado() != null ? partido.getEstado().getClass().getSimpleName() : "Sin estado";

    if ("Confirmado".equals(estadoActual)) {
      procesarPartidoConfirmado(partido, fechaActual);
    } else if ("EnJuego".equals(estadoActual)) {
      procesarPartidoEnJuego(partido, fechaActual);
    }
  }

  private void procesarPartidoConfirmado(Partido partido, LocalDateTime fechaActual) {
    LocalDateTime fechaHoraPartido = obtenerFechaHoraPartido(partido);

    if (fechaHoraPartido != null && fechaHoraPartido.isBefore(fechaActual) || fechaHoraPartido.isEqual(fechaActual)) {
      System.out.println("Partido ID " + partido.getId() + " confirmado - iniciando automáticamente");
      try {
        partidoService.comenzarPartido(partido.getId());
        System.out.println("Partido ID " + partido.getId() + " iniciado automáticamente");
      } catch (Exception e) {
        System.err.println("Error al iniciar partido ID " + partido.getId() + ": " + e.getMessage());
      }
    }
  }

  private void procesarPartidoEnJuego(Partido partido, LocalDateTime fechaActual) {
    LocalDateTime fechaHoraInicio = obtenerFechaHoraPartido(partido);

    if (fechaHoraInicio != null) {
      LocalDateTime fechaHoraFinalizacion = fechaHoraInicio.plusMinutes(partido.getDuracionMinutos());

      if (fechaHoraFinalizacion.isBefore(fechaActual) || fechaHoraFinalizacion.isEqual(fechaActual)) {
        System.out.println("Partido ID " + partido.getId() + " en juego - finalizando automáticamente");
        try {
          partidoService.finalizarPartido(partido.getId());
          System.out.println("Partido ID " + partido.getId() + " finalizado automáticamente");
        } catch (Exception e) {
          System.err.println("Error al finalizar partido ID " + partido.getId() + ": " + e.getMessage());
        }
      }
    }
  }

  private LocalDateTime obtenerFechaHoraPartido(Partido partido) {
    try {
      LocalDate fecha = partido.getFecha();
      LocalTime hora = LocalTime.parse(partido.getHora(), DateTimeFormatter.ofPattern("HH:mm"));

      return LocalDateTime.of(fecha, hora);
    } catch (Exception e) {
      System.err.println("Error al parsear fecha/hora del partido ID " + partido.getId() + ": " + e.getMessage());
    }
    return null;
  }

}