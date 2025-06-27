package com.uade.tpo.model.notification;

import com.uade.tpo.model.Partido;
import com.uade.tpo.model.Usuario;
import com.uade.tpo.model.Equipo;
import com.uade.tpo.model.Deporte;
import com.uade.tpo.repository.UsuarioDeporteRepository;
import com.uade.tpo.repository.UsuarioRepository;
import com.uade.tpo.repository.DeporteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
public class EmailNotificationStrategy implements NotificationStrategy {

    @Autowired
    private MailAdapter mailAdapter;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioDeporteRepository usuarioDeporteRepository;

    @Autowired
    private DeporteRepository deporteRepository;

    @Override
    public void sendNotification(Usuario jugador, Partido partido, String eventType) {
        switch (eventType) {
            case "PARTIDO_CREADO":
                notifyPartidoCreated(jugador, partido);
                break;
            case "PARTIDO_ARMADO":
                notifyPartidoArmado(jugador, partido);
                break;
            case "PARTIDO_CONFIRMADO":
                notifyPartidoConfirmado(jugador, partido);
                break;
            case "PARTIDO_EN_JUEGO":
                notifyPartidoEnJuego(jugador, partido);
                break;
            case "PARTIDO_FINALIZADO":
                notifyPartidoFinalizado(jugador, partido);
                break;
            case "PARTIDO_CANCELADO":
                notifyPartidoCancelado(jugador, partido);
                break;
        }
    }

    @Override
    public String getStrategyName() {
        return "EMAIL";
    }

    private void notifyPartidoCreated(Usuario jugador, Partido partido) {

        if (!jugador.getId().equals(partido.getOrganizador().getId())) {
            mailAdapter.enviarMail(
                    jugador.getEmail(),
                    "¡Nuevo partido de " + partido.getDeporte().getNombre() + "!",
                    "Se ha creado un nuevo partido de " + partido.getDeporte().getNombre() +
                            " para el " + partido.getFecha() + " a las " + partido.getHora() +
                            ". ¡No te lo pierdas!");
        }

    }

    private void notifyPartidoArmado(Usuario jugador, Partido partido) {

        mailAdapter.enviarMail(
                jugador.getEmail(),
                "¡Partido armado!",
                "El partido de " + partido.getDeporte().getNombre() +
                        " para el " + partido.getFecha() + " a las " + partido.getHora() +
                        " ya tiene suficientes jugadores. ¡Prepárate para jugar!");

    }

    private void notifyPartidoConfirmado(Usuario jugador, Partido partido) {

        mailAdapter.enviarMail(
                jugador.getEmail(),
                "¡Partido confirmado!",
                "El partido de " + partido.getDeporte().getNombre() +
                        " para el " + partido.getFecha() + " a las " + partido.getHora() +
                        " ha sido confirmado. ¡Nos vemos en la cancha!");

    }

    private void notifyPartidoEnJuego(Usuario jugador, Partido partido) {

        mailAdapter.enviarMail(
                jugador.getEmail(),
                "¡El partido ha comenzado!",
                "El partido de " + partido.getDeporte().getNombre() +
                        " para el " + partido.getFecha() + " a las " + partido.getHora() +
                        " ya está en juego. ¡Disfruta del partido!");

    }

    private void notifyPartidoFinalizado(Usuario jugador, Partido partido) {

        mailAdapter.enviarMail(
                jugador.getEmail(),
                "¡Partido finalizado!",
                "El partido de " + partido.getDeporte().getNombre() +
                        " para el " + partido.getFecha() + " a las " + partido.getHora() +
                        " ha finalizado. ¡Gracias por participar!");

    }

    private void notifyPartidoCancelado(Usuario jugador, Partido partido) {

        mailAdapter.enviarMail(
                jugador.getEmail(),
                "Partido cancelado",
                "El partido de " + partido.getDeporte().getNombre() +
                        " para el " + partido.getFecha() + " a las " + partido.getHora() +
                        " ha sido cancelado. Lamentamos las molestias.");

    }

}