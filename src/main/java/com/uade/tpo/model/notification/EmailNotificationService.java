package com.uade.tpo.model.notification;

import com.uade.tpo.model.Partido;
import com.uade.tpo.model.Usuario;
import com.uade.tpo.model.Equipo;
import com.uade.tpo.model.Deporte;
import com.uade.tpo.repository.UsuarioDeporteRepository;
import com.uade.tpo.repository.UsuarioRepository;
import com.uade.tpo.repository.DeporteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class EmailNotificationService implements NotificationObserver {

    @Autowired
    private JavaMailSender emailSender;

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
                sendEmail(
                        usuario.getEmail(),
                        "¡Nuevo partido de " + partido.getDeporte().getNombre() + "!",
                        "Se ha creado un nuevo partido de " + partido.getDeporte().getNombre() +
                                " para el " + partido.getFecha() + " a las " + partido.getHora() +
                                ". ¡No te lo pierdas!");
            }
        }
    }

    private void notifyPartidoArmado(Partido partido) {
        List<Usuario> jugadores = getJugadoresDelPartido(partido);

        for (Usuario jugador : jugadores) {
            sendEmail(
                    jugador.getEmail(),
                    "¡Partido armado!",
                    "El partido de " + partido.getDeporte().getNombre() +
                            " para el " + partido.getFecha() + " a las " + partido.getHora() +
                            " ya tiene suficientes jugadores. ¡Prepárate para jugar!");
        }
    }

    private void notifyPartidoConfirmado(Partido partido) {
        List<Usuario> jugadores = getJugadoresDelPartido(partido);

        for (Usuario jugador : jugadores) {
            sendEmail(
                    jugador.getEmail(),
                    "¡Partido confirmado!",
                    "El partido de " + partido.getDeporte().getNombre() +
                            " para el " + partido.getFecha() + " a las " + partido.getHora() +
                            " ha sido confirmado. ¡Nos vemos en la cancha!");
        }
    }

    private void notifyPartidoEnJuego(Partido partido) {
        List<Usuario> jugadores = getJugadoresDelPartido(partido);

        for (Usuario jugador : jugadores) {
            sendEmail(
                    jugador.getEmail(),
                    "¡El partido ha comenzado!",
                    "El partido de " + partido.getDeporte().getNombre() +
                            " para el " + partido.getFecha() + " a las " + partido.getHora() +
                            " ya está en juego. ¡Disfruta del partido!");
        }
    }

    private void notifyPartidoFinalizado(Partido partido) {
        List<Usuario> jugadores = getJugadoresDelPartido(partido);

        for (Usuario jugador : jugadores) {
            sendEmail(
                    jugador.getEmail(),
                    "¡Partido finalizado!",
                    "El partido de " + partido.getDeporte().getNombre() +
                            " para el " + partido.getFecha() + " a las " + partido.getHora() +
                            " ha finalizado. ¡Gracias por participar!");
        }
    }

    private void notifyPartidoCancelado(Partido partido) {
        List<Usuario> jugadores = getJugadoresDelPartido(partido);

        for (Usuario jugador : jugadores) {
            sendEmail(
                    jugador.getEmail(),
                    "Partido cancelado",
                    "El partido de " + partido.getDeporte().getNombre() +
                            " para el " + partido.getFecha() + " a las " + partido.getHora() +
                            " ha sido cancelado. Lamentamos las molestias.");
        }
    }

    private void sendEmail(String to, String subject, String text) {
        // Solo enviar al mail propio para evitar bloqueos de gmail
        if (!"santiagonahuellopez@gmail.com".equalsIgnoreCase(to)) {
            System.out.println("subject: " + subject + " Destinatario ignorado para no generar SPAM");
            return;
        }
        System.out.println("Enviando correo electronico al destinatario");
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            emailSender.send(message);
        } catch (Exception e) {
            System.err.println("Error sending email to " + to + ": " + e.getMessage());
        }
    }

    private List<Usuario> getUsuariosInteresadosEnDeporte(Long deporteId) {
        // Primero obtenemos el deporte
        Deporte deporte = deporteRepository.findById(deporteId)
                .orElseThrow(() -> new RuntimeException("Deporte no encontrado"));

        // Obtenemos todos los usuarios que juegan este deporte
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