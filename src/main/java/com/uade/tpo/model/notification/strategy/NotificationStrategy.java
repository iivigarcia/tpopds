package com.uade.tpo.model.notification.strategy;

import com.uade.tpo.model.Partido;
import com.uade.tpo.model.Usuario;

import java.util.List;

public interface NotificationStrategy {
    
    /**
     * Determina qué usuarios deben recibir notificaciones para un evento específico
     * @param partido El partido relacionado con el evento
     * @param eventType El tipo de evento (PARTIDO_CREADO, PARTIDO_ARMADO, etc.)
     * @return Lista de usuarios que deben recibir la notificación
     */
    List<Usuario> getTargetUsers(Partido partido, String eventType);
    
    /**
     * Determina si se debe enviar notificación para un evento específico
     * @param eventType El tipo de evento
     * @return true si se debe enviar notificación, false en caso contrario
     */
    boolean shouldSendNotification(String eventType);
} 