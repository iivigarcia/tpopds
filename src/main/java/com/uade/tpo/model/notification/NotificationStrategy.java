package com.uade.tpo.model.notification;

import com.uade.tpo.model.Partido;
import com.uade.tpo.model.Usuario;

public interface NotificationStrategy {

    void sendNotification(Usuario jugador, Partido partido, String eventType);

    String getStrategyName();
}