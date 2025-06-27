package com.uade.tpo.model.notification;

import com.uade.tpo.model.Partido;

public interface NotificationStrategy {
    
    /**
     * Envía una notificación para un evento específico
     * @param partido El partido relacionado con el evento
     * @param eventType El tipo de evento (PARTIDO_CREADO, PARTIDO_ARMADO, etc.)
     */
    void sendNotification(Partido partido, String eventType);
    
    /**
     * Obtiene el nombre de la estrategia
     * @return Nombre de la estrategia
     */
    String getStrategyName();
} 