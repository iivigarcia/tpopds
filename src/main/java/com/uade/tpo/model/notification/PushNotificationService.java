package com.uade.tpo.model.notification;

import com.uade.tpo.model.Partido;
import com.uade.tpo.model.Usuario;
import com.uade.tpo.model.Equipo;
import com.uade.tpo.model.Deporte;
import com.uade.tpo.repository.UsuarioDeporteRepository;
import com.uade.tpo.repository.UsuarioRepository;
import com.uade.tpo.repository.DeporteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class PushNotificationService implements NotificationObserver {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private UsuarioDeporteRepository usuarioDeporteRepository;
    
    @Autowired
    private DeporteRepository deporteRepository;
    
    @Override
    public void update(Partido partido, String eventType) {
        switch (eventType) {
            case "PARTIDO_CREADO":
                notifyPartidoCreated(partido);
                break;
            case "PARTIDO_ARMADO":
                notifyPartidoArmado(partido);
                break;
            case "PARTIDO_CONFIRMADO":
                notifyPartidoConfirmado(partido);
                break;
            case "PARTIDO_EN_JUEGO":
                notifyPartidoEnJuego(partido);
                break;
            case "PARTIDO_FINALIZADO":
                notifyPartidoFinalizado(partido);
                break;
            case "PARTIDO_CANCELADO":
                notifyPartidoCancelado(partido);
                break;
        }
    }
    
    private void notifyPartidoCreated(Partido partido) {
        List<Usuario> usuariosInteresados = getUsuariosInteresadosEnDeporte(partido.getDeporte().getId());
        
        for (Usuario usuario : usuariosInteresados) {
            if (!usuario.getId().equals(partido.getOrganizador().getId())) {
                sendPushNotification(
                    usuario.getId(),
                    "¡Nuevo partido de " + partido.getDeporte().getNombre() + "!",
                    "Se ha creado un nuevo partido para el " + partido.getFecha() + " a las " + partido.getHora()
                );
            }
        }
    }
    
    private void notifyPartidoArmado(Partido partido) {
        List<Usuario> jugadores = getJugadoresDelPartido(partido);
        
        for (Usuario jugador : jugadores) {
            sendPushNotification(
                jugador.getId(),
                "¡Partido armado!",
                "El partido ya tiene suficientes jugadores. ¡Prepárate para jugar!"
            );
        }
    }
    
    private void notifyPartidoConfirmado(Partido partido) {
        List<Usuario> jugadores = getJugadoresDelPartido(partido);
        
        for (Usuario jugador : jugadores) {
            sendPushNotification(
                jugador.getId(),
                "¡Partido confirmado!",
                "El partido ha sido confirmado. ¡Nos vemos en la cancha!"
            );
        }
    }
    
    private void notifyPartidoEnJuego(Partido partido) {
        List<Usuario> jugadores = getJugadoresDelPartido(partido);
        
        for (Usuario jugador : jugadores) {
            sendPushNotification(
                jugador.getId(),
                "¡El partido ha comenzado!",
                "El partido ya está en juego. ¡Disfruta del partido!"
            );
        }
    }
    
    private void notifyPartidoFinalizado(Partido partido) {
        List<Usuario> jugadores = getJugadoresDelPartido(partido);
        
        for (Usuario jugador : jugadores) {
            sendPushNotification(
                jugador.getId(),
                "¡Partido finalizado!",
                "El partido ha finalizado. ¡Gracias por participar!"
            );
        }
    }
    
    private void notifyPartidoCancelado(Partido partido) {
        List<Usuario> jugadores = getJugadoresDelPartido(partido);
        
        for (Usuario jugador : jugadores) {
            sendPushNotification(
                jugador.getId(),
                "Partido cancelado",
                "El partido ha sido cancelado. Lamentamos las molestias."
            );
        }
    }
    
    private void sendPushNotification(Long userId, String title, String body) {
        try {
            System.out.println("Push notification sent to user " + userId + ": " + title + " - " + body);
            
        } catch (Exception e) {
            System.err.println("Error sending push notification to user " + userId + ": " + e.getMessage());
        }
    }
    
    private List<Usuario> getUsuariosInteresadosEnDeporte(Long deporteId) {
        // Primero obtenemos el deporte
        Deporte deporte = deporteRepository.findById(deporteId)
            .orElseThrow(() -> new RuntimeException("Deporte no encontrado"));
        
        // Obtenemos todos los usuarios que practican este deporte
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
} 