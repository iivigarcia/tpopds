package com.uade.tpo.model.notification;

import com.uade.tpo.model.Partido;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NotificationManager {
    
    @Autowired
    private List<NotificationStrategy> strategies;
    
    private NotificationStrategy currentStrategy;
    
    public NotificationManager() {
        this.currentStrategy = new EmailNotificationStrategy();
    }
    
    public void setStrategy(NotificationStrategy strategy) {
        this.currentStrategy = strategy;
        System.out.println("Estrategia de notificación cambiada a: " + strategy.getStrategyName());
    }
    
    public void setStrategyByName(String strategyName) {
        for (NotificationStrategy strategy : strategies) {
            if (strategy.getStrategyName().equalsIgnoreCase(strategyName)) {
                this.currentStrategy = strategy;
                System.out.println("Estrategia de notificación cambiada a: " + strategyName);
                return;
            }
        }
        throw new IllegalArgumentException("Estrategia no encontrada: " + strategyName);
    }
    
    public NotificationStrategy getCurrentStrategy() {
        return currentStrategy;
    }
    
    public String getCurrentStrategyName() {
        return currentStrategy != null ? currentStrategy.getStrategyName() : "NONE";
    }
    
    public void sendNotification(Partido partido, String eventType) {
        if (currentStrategy != null) {
            currentStrategy.sendNotification(partido, eventType);
        } else {
            System.out.println("No hay estrategia de notificación configurada");
        }
    }
    
    public void notifyPartidoCreated(Partido partido) {
        sendNotification(partido, "PARTIDO_CREADO");
    }
    
    public void notifyPartidoArmado(Partido partido) {
        sendNotification(partido, "PARTIDO_ARMADO");
    }
    
    public void notifyPartidoConfirmado(Partido partido) {
        sendNotification(partido, "PARTIDO_CONFIRMADO");
    }
    
    public void notifyPartidoEnJuego(Partido partido) {
        sendNotification(partido, "PARTIDO_EN_JUEGO");
    }
    
    public void notifyPartidoFinalizado(Partido partido) {
        sendNotification(partido, "PARTIDO_FINALIZADO");
    }
    
    public void notifyPartidoCancelado(Partido partido) {
        sendNotification(partido, "PARTIDO_CANCELADO");
    }
} 