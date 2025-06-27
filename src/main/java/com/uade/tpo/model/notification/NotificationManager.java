package com.uade.tpo.model.notification;

import com.uade.tpo.model.Deporte;
import com.uade.tpo.model.Equipo;
import com.uade.tpo.model.Partido;
import com.uade.tpo.model.Usuario;
import com.uade.tpo.repository.DeporteRepository;
import com.uade.tpo.repository.UsuarioDeporteRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class NotificationManager implements IObservador {

    @Autowired
    private EmailNotificationStrategy emailNotificationStrategy;

    @Autowired
    private PushNotificationStrategy pushNotificationStrategy;

    @Autowired
    private DeporteRepository deporteRepository;

    @Autowired
    private UsuarioDeporteRepository usuarioDeporteRepository;

    private NotificationStrategy currentNotificationStrategy;
    private Partido partido;

    public NotificationManager() {
        // Constructor vacío para Spring
    }

    public void setPartido(Partido partido) {
        this.partido = partido;
    }

    public void notificarJugadores(List<Usuario> jugadores, String eventType) {
        if (currentNotificationStrategy == null) {
            currentNotificationStrategy = emailNotificationStrategy; // Default strategy
        }

        for (Usuario jugador : jugadores) {
            try {
                currentNotificationStrategy.sendNotification(jugador, partido, eventType);
                System.out.println("Notificación enviada a: " + jugador.getEmail() + " - Evento: " + eventType);
            } catch (Exception e) {
                System.err.println("Error enviando notificación a " + jugador.getEmail() + ": " + e.getMessage());
            }
        }
    }

    public void serNotificado() {
        if (partido == null) {
            System.err.println("Error: NotificationManager no tiene partido asignado");
            return;
        }

        String eventType = getEventTypeForStateChange();
        if (eventType != null) {
            System.out.println("Procesando notificación para evento: " + eventType);
            if ("PARTIDO_CREADO".equals(eventType)) {
                notificarJugadores(getUsuariosInteresadosEnDeporte(partido.getDeporte().getId()), eventType);
            } else {
                notificarJugadores(getJugadoresDelPartido(partido), eventType);
            }
        }
    }

    private String getEventTypeForStateChange() {
        if (partido.getEstado() instanceof com.uade.tpo.model.state.NecesitamosJugadores) {
            return "PARTIDO_CREADO";
        }
        if (partido.getEstado() instanceof com.uade.tpo.model.state.PartidoArmado) {
            return "PARTIDO_ARMADO";
        } else if (partido.getEstado() instanceof com.uade.tpo.model.state.Confirmado) {
            return "PARTIDO_CONFIRMADO";
        } else if (partido.getEstado() instanceof com.uade.tpo.model.state.EnJuego) {
            return "PARTIDO_EN_JUEGO";
        } else if (partido.getEstado() instanceof com.uade.tpo.model.state.Finalizado) {
            return "PARTIDO_FINALIZADO";
        } else if (partido.getEstado() instanceof com.uade.tpo.model.state.Cancelado) {
            return "PARTIDO_CANCELADO";
        }
        return null;
    }

    private List<Usuario> getUsuariosInteresadosEnDeporte(Long deporteId) {
        Deporte deporte = deporteRepository.findById(deporteId)
                .orElseThrow(() -> new RuntimeException("Deporte no encontrado"));

        return usuarioDeporteRepository.findByDeporte(deporte)
                .stream()
                .map(usuarioDeporte -> usuarioDeporte.getUsuario())
                .collect(Collectors.toList());
    }

    private List<Usuario> getJugadoresDelPartido(Partido partido) {
        List<Usuario> jugadores = new ArrayList<>();
        jugadores.add(partido.getOrganizador());

        if (partido.getEquipos() != null) {
            for (Equipo equipo : partido.getEquipos()) {
                if (equipo.getJugadores() != null) {
                    jugadores.addAll(equipo.getJugadores());
                }
            }
        }

        return jugadores.stream().distinct().collect(Collectors.toList());
    }

    public void setNotificationType(String strategyType) {
        switch (strategyType.toUpperCase()) {
            case "EMAIL":
                this.currentNotificationStrategy = emailNotificationStrategy;
                break;
            case "PUSH":
                this.currentNotificationStrategy = pushNotificationStrategy;
                break;
            default:
                throw new IllegalArgumentException("Tipo de estrategia no válida: " + strategyType);
        }
        System.out.println("Tipo de notificación cambiado a: " + strategyType);
    }

    public String getCurrentNotificationType() {
        return currentNotificationStrategy != null ? currentNotificationStrategy.getStrategyName() : "EMAIL";
    }

}